package cn.itscloudy.propray;

import cn.itscloudy.propray.ui.ArrowBubbleBorder;
import cn.itscloudy.propray.ui.SwingUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ControlBox {
    private static final int ROOT_Y = 70;
    private static final int RIGHT_OFFSET = 60;

    @Getter
    private JPanel root;
    private JCheckBox fullTextScanningCb;
    private JTextField searchField;
    private JLabel shortcutPrompt;
    private JPanel content;
    @Getter
    private JComponent switchButton;

    ControlBox() {
        content.setBorder(new ArrowBubbleBorder(ArrowBubbleBorder.Direction.RIGHT, 5, JBColor.GRAY));
        root.setVisible(false);
        root.setBorder(JBUI.Borders.emptyRight(switchButton.getWidth() + 2));
    }

    private void createUIComponents() {
        root = new Root();
        switchButton = new SwitchButton();
    }

    private static class Root extends JBPanel<Root> {

        Root() {
            setOpaque(false);
        }
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, ROOT_Y, width, height);
        }
    }

    private class SwitchButton extends JLabel {

        SwitchButton() {
            super(SwingUtil.findIcon("/icons/ray.png"));
            setOpaque(true);
            setBackground(JBColor.WHITE);
            setSize(new Dimension(30, 30));
            setBorder(JBUI.Borders.emptyRight(RIGHT_OFFSET));
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    root.setVisible(!root.isVisible());
                }
            });
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, (root.getHeight() - height) / 2 + ROOT_Y, width, height);
        }
    }
}
