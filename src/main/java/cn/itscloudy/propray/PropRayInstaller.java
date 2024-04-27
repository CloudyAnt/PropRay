package cn.itscloudy.propray;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBLayeredPane;

import javax.swing.*;
import java.awt.*;

public class PropRayInstaller {
    private static final Key<PropRayInstaller> KEY = Key.create(PropRayInstaller.class.getName());

    private final Editor editor;
    private final JBLayeredPane layeredPane;

    private PropRayInstaller(Editor editor, JBLayeredPane layeredPane) {
        this.editor = editor;
        this.layeredPane = layeredPane;
    }

    static void install(Editor editor) {
        if (true) {
            // TODO delete this after ControlBox finished
            return;
        }

        Component comp = editor.getComponent().getComponents()[1];
        if (!(comp instanceof JBLayeredPane)) {
            comp = editor.getComponent().getComponents()[0];
            if (!(comp instanceof JBLayeredPane)) {
                return;
            }
        }

        PropRayInstaller propRayInstaller = new PropRayInstaller(editor, (JBLayeredPane) comp);
        editor.putUserData(KEY, propRayInstaller);
        ApplicationManager.getApplication().executeOnPooledThread(propRayInstaller::init);
    }

    static void uninstall(Editor editor) {
        editor.putUserData(KEY, null);
    }


    void init() {
        ControlBox controlBox = new ControlBox();
        JComponent switchButton = controlBox.getSwitchButton();
        layeredPane.add(switchButton);
        layeredPane.setLayer(switchButton, 100);

        JPanel box = controlBox.getRoot();
        layeredPane.add(box);
        layeredPane.setLayer(box, 100);
        // Do something
    }
}
