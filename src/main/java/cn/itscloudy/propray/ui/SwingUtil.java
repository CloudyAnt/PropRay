package cn.itscloudy.propray.ui;

import cn.itscloudy.propray.PrConst;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.components.fields.ExtendableTextComponent;
import com.intellij.util.ReflectionUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

public class SwingUtil {
    private SwingUtil() {
    }

    public static int getBaselineY(FontMetrics fontMetrics, int lineHeight) {
        int fontHeight = fontMetrics.getAscent() + fontMetrics.getDescent();
        return Math.round(((float) lineHeight - fontHeight) / 2) + fontMetrics.getAscent() + fontMetrics.getLeading();
    }

    public static Area getRoundRectangle(int width, int height, int radius, int offset) {
        radius = radius - offset;
        GeneralPath path = new GeneralPath();
        path.moveTo(radius + offset, offset);
        path.quadTo(offset, offset, offset, radius + offset);

        int bottom = height - offset;
        path.lineTo(offset, height - radius - offset);
        path.quadTo(offset, bottom, radius + offset, bottom);

        int right = width - offset;
        path.lineTo(right - radius, bottom);
        path.quadTo(right, bottom, right, bottom - radius);

        path.lineTo(right, radius + offset);
        path.quadTo(right, offset, right - radius, offset);

        path.closePath();
        return new Area(path);
    }

    public static Icon findIcon(String path) {
        Class<?> callerClass = ReflectionUtil.getGrandCallerClass();
        return callerClass == null ? null : IconLoader.findIcon(path, callerClass.getClassLoader());
    }


    public static ExtendableTextComponent.Extension createExtension(Icon icon, String tooltipId, Runnable action) {
        String tooltip = PrConst.get(tooltipId);
        return new ExtendableTextComponent.Extension() {

            @Override
            public Icon getIcon(boolean hovered) {
                return icon;
            }

            @Override
            public int getIconGap() {
                return 8;
            }

            @Override
            public boolean isIconBeforeText() {
                return false;
            }

            @Override
            public Runnable getActionOnClick() {
                return action;
            }

            @Override
            public String getTooltip() {
                return tooltip;
            }
        };
    }
}
