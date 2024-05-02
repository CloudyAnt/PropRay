package cn.itscloudy.propray;

import com.intellij.openapi.editor.Editor;
import com.intellij.ui.JBColor;
import lombok.Setter;

import java.awt.*;

public class PropRaySearchResultMask extends PropRayMask {

    private final int start;
    private final String str;
    private Font font;
    private Rectangle rect;
    @Setter
    private boolean current;

    PropRaySearchResultMask(Editor editor, int start, String str) {
        super(editor);
        this.start = start;
        this.str = str;

        updateText(editor, start, str);
    }

    private void updateText(Editor editor, int start, String str) {
        this.font = getFont(editor, this.str);
        FontMetrics fontMetrics = editor.getContentComponent().getFontMetrics(this.font);
        rect = getRectangle(fontMetrics, start, str);
    }

    @Override
    public int getStartOffset() {
        return start;
    }

    @Override
    public int getEndOffset() {
        return start + str.length();
    }

    @Override
    void paintBy(Graphics2D g2) {
        g2.setFont(font);
        if (current) {
            g2.setColor(PrConst.MAIN_COLOR);
        } else {
            g2.setColor(JBColor.WHITE);
        }
        g2.draw(rect);
    }
}
