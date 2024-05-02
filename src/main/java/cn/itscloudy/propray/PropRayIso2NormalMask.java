package cn.itscloudy.propray;

import cn.itscloudy.propray.ui.SwingUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import lombok.Getter;

import java.awt.*;

public class PropRayIso2NormalMask {
    private static final Key<Font> INLAY_FONT_KEY = Key.create("INLAY_FONT_KEY");
    private static final Key<Font> INLAY_FALLBACK_FONT_KEY = Key.create("INLAY_FALLBACK_FONT_KEY");
    private final int requiredHeight;
    private int baseLineY;
    private Font font;
    private FontMetrics fontMetrics;
    private String newText;
    private final PropRayCanvas replacementLayerCanvas;
    private final Editor editor;
    @Getter
    private final int startOffset;
    @Getter
    private final int endOffset;
    private Rectangle cover;
    private String coveredText;

    private static final Color MARK_COLOR = JBColor.WHITE;
    private static final Color FONT_COLOR = JBColor.BLACK;
    PropRayIso2NormalMask(Editor editor, int visualStartOffset, String newText, String coveredText) {
        this.editor = editor;
        this.startOffset = visualStartOffset;
        this.endOffset = visualStartOffset + coveredText.length();
        this.replacementLayerCanvas = PropRayCanvas.getOrBind(editor);
        replacementLayerCanvas.clearAndAdd(this);
        this.requiredHeight = editor.getLineHeight();
        updateText(newText, coveredText);
    }

    public void paint(Graphics2D g) {
        if (cover == null) {
            return;
        }
        g.setColor(MARK_COLOR);
        g.fillRect(cover.x, cover.y, cover.width, cover.height);
        g.setFont(font);
        g.setColor(FONT_COLOR);
        g.drawString(newText, cover.x, cover.y + baseLineY);
    }

    public void updateText(String newText, String coveredText) {
        this.newText = newText.replace("\t", "    ").replace("\r", "");
        this.coveredText = coveredText;
        this.font = getFont(editor, this.newText);
        fontMetrics = editor.getContentComponent().getFontMetrics(this.font);
        this.baseLineY = SwingUtil.getBaselineY(fontMetrics, requiredHeight);
    }

    public void render() {
        int replacedTextWidth = fontMetrics.stringWidth(coveredText);

        // cover for new text
        Point recTopLeft = editor.offsetToXY(startOffset);
        cover = new Rectangle(recTopLeft.x, recTopLeft.y, replacedTextWidth, requiredHeight);
        replacementLayerCanvas.repaint();
    }

    public static Font getFont(Editor editor, String text) {
        Font font = editor.getUserData(INLAY_FONT_KEY);
        if (font == null) {
            updateEditorFontKeys(editor);
            font = editor.getUserData(INLAY_FONT_KEY);
        }
        if (shouldUseFallback(font, text)) {
            return getFallbackFont(editor);
        }
        return font;
    }

    public static void updateEditorFontKeys(Editor editor) {
        Font font = editor.getColorsScheme().getFont(EditorFontType.PLAIN);
        editor.putUserData(INLAY_FONT_KEY, font);
        editor.putUserData(INLAY_FALLBACK_FONT_KEY, UIUtil.getFontWithFallback(font));
    }

    public static Font getFallbackFont(Editor editor) {
        Font font = editor.getUserData(INLAY_FALLBACK_FONT_KEY);
        if (font == null) {
            updateEditorFontKeys(editor);
            font = editor.getUserData(INLAY_FALLBACK_FONT_KEY);
        }
        return font;
    }

    public static boolean shouldUseFallback(Font font, String text) {
        return !SystemInfoRt.isMac && font.canDisplayUpTo(text) != -1;
    }
}
