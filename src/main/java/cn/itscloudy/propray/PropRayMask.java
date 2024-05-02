package cn.itscloudy.propray;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.util.ui.UIUtil;

import java.awt.*;

public abstract class PropRayMask {
    private static final Key<Font> INLAY_FONT_KEY = Key.create("INLAY_FONT_KEY");
    private static final Key<Font> INLAY_FALLBACK_FONT_KEY = Key.create("INLAY_FALLBACK_FONT_KEY");

    protected final int requiredHeight;
    protected final Editor editor;

    PropRayMask(Editor editor) {
        this.editor = editor;
        this.requiredHeight = editor.getLineHeight();
    }

    abstract int getStartOffset();

    abstract int getEndOffset();

    abstract void paintBy(Graphics2D g2);

    Rectangle getRectangle(FontMetrics fontMetrics, int offset, String text) {
        int textWidth = fontMetrics.stringWidth(text);
        Point recTopLeft = editor.offsetToXY(offset);
        return new Rectangle(recTopLeft.x, recTopLeft.y, textWidth, requiredHeight);
    }

    Font getFont(Editor editor, String text) {
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

    boolean shouldUseFallback(Font font, String text) {
        return !SystemInfoRt.isMac && font.canDisplayUpTo(text) != -1;
    }

    void updateEditorFontKeys(Editor editor) {
        Font font = editor.getColorsScheme().getFont(EditorFontType.PLAIN);
        editor.putUserData(INLAY_FONT_KEY, font);
        editor.putUserData(INLAY_FALLBACK_FONT_KEY, UIUtil.getFontWithFallback(font));
    }

    Font getFallbackFont(Editor editor) {
        Font font = editor.getUserData(INLAY_FALLBACK_FONT_KEY);
        if (font == null) {
            updateEditorFontKeys(editor);
            font = editor.getUserData(INLAY_FALLBACK_FONT_KEY);
        }
        return font;
    }
}
