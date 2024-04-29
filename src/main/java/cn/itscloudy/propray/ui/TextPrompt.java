package cn.itscloudy.propray.ui;

import com.intellij.util.ui.JBUI;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextPrompt extends JLabel implements FocusListener, DocumentListener {
    public enum Show {
        ALWAYS,
        FOCUS_GAINED,
        FOCUS_LOST
    }
    private final JTextComponent host;
    private final Document document;

    @Setter
    @Getter
    private Show show;

    private int focusLost;
    public TextPrompt(String text, JTextComponent host) {
        this(text, host, Show.ALWAYS, true, null);
    }

    public TextPrompt(String text, JTextComponent host, Insets insets) {
        this(text, host, Show.ALWAYS, false, insets);
    }

    public TextPrompt(String text, JTextComponent host, Show show, boolean useCompBorderInsets, Insets insets) {
        this.host = host;
        setShow(show);
        document = host.getDocument();

        setText(text);
        setFont(host.getFont());
        setForeground(host.getForeground());
        if (useCompBorderInsets) {
            setBorder(new EmptyBorder(host.getInsets()));
        } else {
            setBorder(JBUI.Borders.empty(insets.top, insets.left, insets.bottom, insets.right));
        }
        setHorizontalAlignment(JLabel.LEADING);
        setVerticalAlignment(JLabel.TOP);

        host.addFocusListener(this);
        document.addDocumentListener(this);

        host.setLayout(new BorderLayout());
        host.add(this);
        checkForPrompt();
    }

    public void addToHost() {
        host.add(this);
    }

    public void uninstall() {
        host.removeFocusListener(this);
        document.removeDocumentListener(this);
    }

    private void checkForPrompt() {
        //  Text has been entered, remove the prompt

        if (document.getLength() > 0) {
            setVisible(false);
            return;
        }

        //  Check the Show property and component focus to determine if the
        //  prompt should be displayed.

        if (host.hasFocus()) {
            setVisible(show == Show.ALWAYS
                    || show == Show.FOCUS_GAINED);
        } else {
            setVisible(show == Show.ALWAYS
                    || show == Show.FOCUS_LOST);
        }
    }

    //  Implement FocusListener

    public void focusGained(FocusEvent e) {
        checkForPrompt();
    }

    public void focusLost(FocusEvent e) {
        focusLost++;
        checkForPrompt();
    }

    //  Implement DocumentListener

    public void insertUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    public void removeUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    public void changedUpdate(DocumentEvent e) {
    }
}