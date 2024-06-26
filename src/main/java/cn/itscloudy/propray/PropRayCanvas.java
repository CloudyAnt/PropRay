package cn.itscloudy.propray;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SoftWrap;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import org.codehaus.plexus.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PropRayCanvas extends JComponent {

    private final transient Editor editor;
    private static final Key<PropRayCanvas> KEY = Key.create("PropRayCanvas");
    private final List<PropRayMask> masks = new ArrayList<>();
    private final transient Object modLock = new Object();

    PropRayCanvas(Editor editor) {
        this.editor = editor;
        JComponent contentComponent = editor.getContentComponent();
        contentComponent.add(this);
        updateBounds();
    }

    public static PropRayCanvas getOrBind(Editor editor) {
        PropRayCanvas canvas = editor.getUserData(KEY);
        if (canvas == null) {
            if (editor.getUserData(KEY) == null) {
                editor.putUserData(KEY, new PropRayCanvas(editor));
            }
            canvas = editor.getUserData(KEY);
        }
        return canvas;
    }

    public void updateBounds() {
        JComponent contentComponent = editor.getContentComponent();
        setBounds(0, 0, contentComponent.getWidth(), contentComponent.getHeight());
    }

    public boolean removeMaskIfContains(int start, int end) {
        List<PropRayMask> removals = new ArrayList<>();
        for (PropRayMask mask : masks) {
            if (mask.getStartOffset() <= start && mask.getEndOffset() >= end) {
                removals.add(mask);
            }
        }

        if (removals.isEmpty()) {
            return false;
        }

        synchronized (modLock) {
            masks.removeAll(removals);
            repaint();
        }
        return true;
    }

    public void clear() {
        if (masks.isEmpty()) {
            return;
        }
        synchronized (modLock) {
            masks.clear();
            repaint();
        }
    }

    public void add(PropRayMask mask) {
        synchronized (modLock) {
            masks.add(mask);
        }
    }

    public void clearAndAdd(PropRayMask mask) {
        synchronized (modLock) {
            masks.clear();
            masks.add(mask);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (PropRayMask mark : masks) {
            mark.paintBy(g2);
        }
    }

    public static java.util.List<Rectangle> getRectangles(Editor editor, int from, int to) {
        int fromLine = editor.offsetToLogicalPosition(from).line;

        String text = editor.getDocument().getText(new TextRange(from, to));
        String[] lines = text.split("\n");
        int leftIndex = from;
        java.util.List<Rectangle> rectangles = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int rightIndex = leftIndex + line.length();
            int lineIndex = fromLine + i;

            if (StringUtils.isBlank(line)) {
                leftIndex = rightIndex + 1;
                continue;
            }

            List<? extends SoftWrap> softWrapsForLine = editor.getSoftWrapModel().getSoftWrapsForLine(lineIndex);
            if (softWrapsForLine.isEmpty()) {
                rectangles.add(toRectangle(editor, leftIndex, rightIndex));
            } else {
                SoftWrap firstWrap = softWrapsForLine.get(0);
                int wrapAt = firstWrap.getStart();
                rectangles.add(toRectangle(editor, leftIndex, wrapAt));
                int wrapLen = firstWrap.getText().length();
                for (int j = 1; j < softWrapsForLine.size(); j++) {
                    int left = wrapAt + wrapLen;
                    SoftWrap wrap = softWrapsForLine.get(j);
                    wrapAt = wrap.getStart();
                    rectangles.add(toRectangle(editor, left, wrapAt));
                    wrapLen = wrap.getText().length();
                }
            }
            leftIndex = rightIndex + 1;
        }
        return rectangles;
    }

    public static Rectangle toRectangle(Editor editor, int leftIndex, int rightIndex) {
        Point startPoint = editor.offsetToXY(leftIndex);
        Point endPoint = editor.offsetToXY(rightIndex);
        int lineW = endPoint.x - startPoint.x;
        return new Rectangle(startPoint.x, startPoint.y, lineW, editor.getLineHeight());
    }
}
