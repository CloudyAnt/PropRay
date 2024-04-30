package cn.itscloudy.propray;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBLayeredPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class PropRayEditorAffairs {
    private static final Key<PropRayEditorAffairs> KEY = Key.create(PropRayEditorAffairs.class.getName());

    private final Editor editor;
    private final JBLayeredPane layeredPane;

    private PropRayEditorAffairs(Editor editor, JBLayeredPane layeredPane) {
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

        PropRayEditorAffairs propRayEditorAffairs = new PropRayEditorAffairs(editor, (JBLayeredPane) comp);
        editor.putUserData(KEY, propRayEditorAffairs);
        ApplicationManager.getApplication().executeOnPooledThread(propRayEditorAffairs::init);
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


        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                PropRayCanvas.getOrBind(editor).clear();
            }
        });
    }
}
