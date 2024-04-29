package cn.itscloudy.propray;

import cn.itscloudy.propray.ui.*;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlBox {
    private static final int ROOT_Y = 40;
    private static final int RIGHT_OFFSET = 40;
    private static final Color BORDER_COLOR = Color.decode("#00818F");
    public static final JBColor SHADOW_HINT_COLOR = new JBColor(Gray.x8C, Gray.x99);

    @Getter
    private JPanel root;
    private JCheckBox fullTextScanningCb;
    private JTextField searchField;
    private JLabel shortcutPrompt;
    private JPanel content;
    @Getter
    private JComponent switchButton;

    ControlBox() {
        root.setVisible(false);
        root.setBorder(JBUI.Borders.emptyRight(SwitchButton.W + RIGHT_OFFSET + 2));
        fullTextScanningCb.addActionListener(e ->
                ((LineScanPrompt)shortcutPrompt).repaint(fullTextScanningCb.isSelected()));
    }

    private void createUIComponents() {
        root = new Root();
        switchButton = new SwitchButton();
        ArrowBubbleBorder.Direction bubbleDirection = ArrowBubbleBorder.Direction.RIGHT;
        content = new ArrowBubbleBorderPanel(new ArrowBubbleBorder(bubbleDirection, 5, BORDER_COLOR));
        searchField = new SearchField();
        shortcutPrompt = new LineScanPrompt();
    }

    public void afterKeyTyped(KeyEvent keyEvent) {
        searchField.dispatchEvent(keyEvent);
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

    private class SwitchButton extends RoundCornerLabel {

        private static final int W = 30;
        private static final int H = 30;

        SwitchButton() {
            super(10);
            setIcon(SwingUtil.findIcon("/icons/ray.png"));
            setOpaque(true);
            setBackground(JBColor.WHITE);
            setHorizontalAlignment(SwingConstants.CENTER);
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    root.setVisible(!root.isVisible());
                }
            });
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x + width - RIGHT_OFFSET - W, (root.getHeight() - H) / 2 + ROOT_Y, W, H);
        }
    }

    private class SearchField extends JTextField {

        SearchField() {
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    super.keyReleased(e);
                }
            });
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    PrContext.getInstance().unsetFocusedControlBox(ControlBox.this);
                }

                @Override
                public void focusGained(FocusEvent e) {
                    PrContext.getInstance().setFocusedControlBox(ControlBox.this);
                }
            });
            String prompt = PrConst.get("SearchPrompt");
            TextPrompt tp = new TextPrompt(prompt, this, JBUI.insets(4, 7, 0, 0));
            tp.setForeground(SHADOW_HINT_COLOR);
        }
    }

    private static class LineScanPrompt extends JLabel {

        private boolean hide;
        private final Color originalColor;

        LineScanPrompt() {
            setOpaque(true);
            setText(PrConst.get("LineScanningShortcut"));
            originalColor = getForeground();
        }

        private void repaint(boolean hide) {
            this.hide = hide;
            setForeground(hide ? SHADOW_HINT_COLOR : originalColor);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (hide) {
                int midstH = getHeight() / 2;
                g.drawLine(0, midstH, getWidth(), midstH);
            }
        }
    }
}
