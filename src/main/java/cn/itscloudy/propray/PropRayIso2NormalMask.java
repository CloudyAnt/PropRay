package cn.itscloudy.propray;

import cn.itscloudy.propray.ui.SwingUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.ui.JBColor;
import lombok.Getter;

import java.awt.*;

public class PropRayIso2NormalMask extends PropRayMask {

    private final int requiredHeight;
    private int baseLineY;
    private Font font;
    private String newText;
    private final Editor editor;
    @Getter
    private final int startOffset;
    @Getter
    private final int endOffset;
    private Rectangle cover;

    private static final Color MARK_COLOR = JBColor.WHITE;
    private static final Color FONT_COLOR = JBColor.BLACK;
    PropRayIso2NormalMask(Editor editor, int visualStartOffset, String newText, String coveredText) {
        super(editor);
        this.editor = editor;
        this.startOffset = visualStartOffset;
        this.endOffset = visualStartOffset + coveredText.length();
        this.requiredHeight = editor.getLineHeight();
        updateText(newText, coveredText);
    }

    public void paintBy(Graphics2D g) {
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
        FontMetrics fontMetrics;
        this.newText = newText.replace("\t", "    ").replace("\r", "");
        this.font = getFont(editor, this.newText);
        fontMetrics = editor.getContentComponent().getFontMetrics(this.font);
        this.baseLineY = SwingUtil.getBaselineY(fontMetrics, requiredHeight);

        cover = getRectangle(fontMetrics, startOffset, coveredText);
    }
}
