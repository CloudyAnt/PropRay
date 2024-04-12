package cn.itscloudy.propray.ui;

import com.intellij.util.ui.JBUI;
import lombok.Getter;
import lombok.Setter;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

@Getter
public class ArrowBubbleBorder extends AbstractBorder {
    @Setter
    private Direction direction;
    private final int arrowHeight;
    private final Color borderColor;

    public ArrowBubbleBorder(Direction direction, int arrowHeight, Color borderColor) {
        this.direction = direction;
        this.arrowHeight = arrowHeight;
        this.borderColor = borderColor;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        int yOff = direction.yOff;
        int bottomOff = yOff > 0 ? 0 : arrowHeight * direction.hOff;
        int xOff = direction.xOff;
        int rightOff = xOff > 0 ? 0 : arrowHeight * direction.wOff;
        return JBUI.insets(
                insets.top + (arrowHeight * yOff) + 1,
                insets.left + (arrowHeight * xOff) + 1,
                insets.bottom + bottomOff + 1,
                insets.right + rightOff + 1);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        int w = width - 1;
        int h = height - 1;

        Area arrow = direction.arrowCreator.create(w, h, arrowHeight);

        int bodyWidth = w - direction.wOff * arrowHeight;
        int bodyHeight = h - direction.hOff * arrowHeight;
        Area body = SwingUtil.getRoundRectangle(bodyWidth, bodyHeight, 6, 0);
        if (direction.xOff != 0 || direction.yOff != 0) {
            body.transform(AffineTransform.getTranslateInstance(
                    direction.xOff * arrowHeight,
                    direction.yOff * arrowHeight));
        }
        body.add(arrow);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(1));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.draw(body);

        g2.dispose();
    }

    public enum Direction {
        UP(0, 1, 0, 1, (w, h, ah) -> {
            GeneralPath path = new GeneralPath();
            float pinX = (float) w / 2;
            path.moveTo(pinX, 0);
            path.lineTo(pinX - 5, ah);
            path.lineTo(pinX + 5, ah);
            path.closePath();
            return new Area(path);
        }),
        DOWN(0, 0, 0, 1, (w, h, ah) -> {
            GeneralPath path = new GeneralPath();
            float pinX = (float) w / 2;
            path.moveTo(pinX, h);
            path.lineTo(pinX - 5, h - ah);
            path.lineTo(pinX + 5, h - ah);
            path.closePath();
            return new Area(path);
        }),
        LEFT(1, 0, 1, 0, (w, h, ah) -> {
            GeneralPath path = new GeneralPath();
            float pinY = (float) h / 2;
            path.moveTo(0, pinY);
            path.lineTo(ah, pinY - 5);
            path.lineTo(ah, pinY + 5);
            path.closePath();
            return new Area(path);
        }),
        RIGHT(0, 0, 1, 0, (w, h, ah) -> {
            GeneralPath path = new GeneralPath();
            float pinY = (float) h / 2;
            path.moveTo(w, pinY);
            path.lineTo(w - ah, pinY - 5);
            path.lineTo(w - ah, pinY + 5);
            path.closePath();
            return new Area(path);
        });
        public final int xOff;
        public final int yOff;
        public final int wOff;
        public final int hOff;
        private final ArrowCreator arrowCreator;

        Direction(int xOff, int yOff, int wOff, int hOff, ArrowCreator arrowCreator) {
            this.xOff = xOff;
            this.yOff = yOff;
            this.wOff = wOff;
            this.hOff = hOff;
            this.arrowCreator = arrowCreator;
        }
    }

    private interface ArrowCreator {
        Area create(Integer w, Integer h, Integer ah);
    }
}
