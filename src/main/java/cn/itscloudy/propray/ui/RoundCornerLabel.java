package cn.itscloudy.propray.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public class RoundCornerLabel extends JLabel {
    private final int arc;

    public RoundCornerLabel(int arc) {
        setOpaque(true);
        this.arc = arc;
    }
    @Override
    protected void paintComponent(Graphics g) {
        int r = arc / 2;
        Border border = getBorder();
        int vOffset = 0;
        int hOffset = 0;
        int x = 0;
        int y = 0;
        if (border != null) {
            Insets borderInsets = border.getBorderInsets(this);
            vOffset = borderInsets.top + borderInsets.bottom;
            hOffset = borderInsets.left + borderInsets.right;
            x = borderInsets.left;
            y = borderInsets.top;
        }
        int w = getWidth() - hOffset;
        int h = getHeight() - vOffset;

        Area area = SwingUtil.getRoundRectangle(w, h, r, 0);
        area.transform(AffineTransform.getTranslateInstance(x, y));

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(area);
        this.ui.paint(g2, this);
        g2.dispose();
    }
}
