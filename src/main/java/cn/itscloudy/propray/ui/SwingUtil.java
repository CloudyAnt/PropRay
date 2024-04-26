package cn.itscloudy.propray.ui;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ReflectionUtil;

import javax.swing.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

public class SwingUtil {
    private SwingUtil() {
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
}
