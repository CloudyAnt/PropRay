package cn.itscloudy.propray;

import cn.itscloudy.propray.ui.ArrowBubbleBorder;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import lombok.Getter;

import javax.swing.*;

public class ControlBox {
    @Getter
    private JPanel root;
    private JCheckBox fullTextScanningCb;
    private JTextField searchField;
    private JLabel shortcutPrompt;
    private JPanel content;

    ControlBox() {
        content.setBorder(new ArrowBubbleBorder(ArrowBubbleBorder.Direction.RIGHT, 5, JBColor.GRAY));
//        root.setVisible(false);
        root.setBorder(JBUI.Borders.emptyRight(14));
    }

    private void createUIComponents() {
        root = new Root();
    }

    private static class Root extends JBPanel<Root> {
        private static final int Y = 100;

        Root() {
            setOpaque(false);
        }
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, Y, width, height);
        }
    }
}
