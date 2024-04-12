package cn.itscloudy.propray;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBLayeredPane;

import javax.swing.*;
import java.awt.*;

public class PropRay {
    private static final Key<PropRay> KEY = Key.create(PropRay.class.getName());
    private static final int W = 200;
    private static final int H = 200;

    private final Editor editor;
    private final JBLayeredPane layeredPane;

    private PropRay(Editor editor, JBLayeredPane layeredPane) {
        this.editor = editor;
        this.layeredPane = layeredPane;
    }

    static void install(Editor editor) {
        Component comp = editor.getComponent().getComponents()[1];
        if (!(comp instanceof JBLayeredPane)) {
            comp = editor.getComponent().getComponents()[0];
            if (!(comp instanceof JBLayeredPane)) {
                return;
            }
        }

        PropRay propRay = new PropRay(editor, (JBLayeredPane) comp);
        editor.putUserData(KEY, propRay);
        ApplicationManager.getApplication().executeOnPooledThread(propRay::init);
    }

    static void uninstall(Editor editor) {
        editor.putUserData(KEY, null);
    }


    void init() {
        JPanel p = new ControlBox().getRoot();
        layeredPane.add(p);
        layeredPane.setLayer(p, 100);
        // Do something
    }
}
