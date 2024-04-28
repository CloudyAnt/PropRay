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
    public static final int RADIUS = 6;
    public static final int STROKE_WIDTH = 1;
    public static final int ARROW_HALF_WIDTH = 5;
    @Setter
    private Direction direction;
    @Getter
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
                insets.top + (arrowHeight * yOff),
                insets.left + (arrowHeight * xOff),
                insets.bottom + bottomOff,
                insets.right + rightOff);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        int w = width - 1;
        int h = height - 1;

        Area body = getInterior(w, h);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(STROKE_WIDTH));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.draw(body);

        g2.dispose();
    }

    public Area getInterior(int w, int h) {
        Area arrow = direction.arrowCreator.create(w, h, ARROW_HALF_WIDTH , arrowHeight);

        int bodyWidth = w - direction.wOff * arrowHeight;
        int bodyHeight = h - direction.hOff * arrowHeight;
        Area body = SwingUtil.getRoundRectangle(bodyWidth, bodyHeight, RADIUS, 0);
        if (direction.xOff != 0 || direction.yOff != 0) {
            body.transform(AffineTransform.getTranslateInstance(
                    direction.xOff * arrowHeight,
                    direction.yOff * arrowHeight));
        }

        body.add(arrow);
        return body;
    }

    public enum Direction {
        UP(0, 1, 0, 1, (w, h, ahw, ah) -> {
            GeneralPath path = new GeneralPath();
            float pinX = (float) w / 2;
            path.moveTo(pinX, 0);
            path.lineTo(pinX - ahw, ah);
            path.lineTo(pinX + ahw, ah);
            path.closePath();
            return new Area(path);
        }),
        DOWN(0, 0, 0, 1, (w, h, ahw, ah) -> {
            GeneralPath path = new GeneralPath();
            float pinX = (float) w / 2;
            path.moveTo(pinX, h);
            path.lineTo(pinX - ahw, h - ah);
            path.lineTo(pinX + ahw, h - ah);
            path.closePath();
            return new Area(path);
        }),
        LEFT(1, 0, 1, 0, (w, h, ahw, ah) -> {
            GeneralPath path = new GeneralPath();
            float pinY = (float) h / 2;
            path.moveTo(0, pinY);
            path.lineTo(ah, pinY - ahw);
            path.lineTo(ah, pinY + ahw);
            path.closePath();
            return new Area(path);
        }),
        RIGHT(0, 0, 1, 0, (w, h, ahw, ah) -> {
            GeneralPath path = new GeneralPath();
            float pinY = (float) h / 2;
            path.moveTo(w, pinY);
            path.lineTo(w - ah, pinY - ahw);
            path.lineTo(w - ah, pinY + ahw);
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
        Area create(Integer w, Integer h, Integer arrowHalfWidth, Integer arrowHeight);
    }
}
