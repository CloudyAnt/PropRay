package cn.itscloudy.propray.ui;

import com.intellij.ui.components.JBPanel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public class ArrowBubbleBorderPanel extends JBPanel<ArrowBubbleBorderPanel> {

    private final ArrowBubbleBorder border;

    public ArrowBubbleBorderPanel(ArrowBubbleBorder border) {
        this.border = border;
        setBorder(border);
    }

    @Override
    protected void paintComponent(Graphics g) {
        ArrowBubbleBorder.Direction direction = border.getDirection();
        int w = getWidth();
        int h = getHeight();
        Area interior = border.getInterior(w, h);
        if (direction.xOff != 0 || direction.yOff != 0) {
            interior.transform(AffineTransform.getTranslateInstance(
                    direction.xOff * border.getArrowHeight(),
                    direction.yOff * border.getArrowHeight()));
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(interior);
        g2.dispose();
    }
}
