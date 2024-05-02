package cn.itscloudy.propray;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.components.JBLayeredPane;
import com.intellij.util.Range;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class PropRayEditorConsul {
    public static final Key<PropRayEditorConsul> KEY = Key.create(PropRayEditorConsul.class.getName());

    private final Editor editor;

    private PropRayEditorConsul(Editor editor) {
        this.editor = editor;
    }

    static void install(Editor editor) {
        Component comp = editor.getComponent().getComponents()[1];
        if (!(comp instanceof JBLayeredPane)) {
            comp = editor.getComponent().getComponents()[0];
            if (!(comp instanceof JBLayeredPane)) {
                return;
            }
        }

        PropRayEditorConsul propRayEditorConsul = new PropRayEditorConsul(editor);
        editor.putUserData(KEY, propRayEditorConsul);

        final JBLayeredPane layeredPane = (JBLayeredPane) comp;
        ApplicationManager.getApplication().executeOnPooledThread(() -> propRayEditorConsul.init(layeredPane));
    }

    static void uninstall(Editor editor) {
        editor.putUserData(KEY, null);
    }


    void init(JBLayeredPane layeredPane) {
        ControlBox controlBox = new ControlBox(this);
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

    void afterSelection(TextRange range) {
        int startOffset = range.getStartOffset();
        int endOffset = range.getEndOffset();
        if (startOffset == endOffset) {
            return;
        }
        PropRayCanvas.getOrBind(editor).removeMaskIfContains(startOffset, endOffset);
    }

    void afterVisibleAreaChanged() {
        PropRayCanvas.getOrBind(editor).clear();
    }

    java.util.List<Range<Integer>> searchResults = new ArrayList<>();

    int search(String str) {
        String text = editor.getDocument().getText();
        String normal = PropRayUtil.toNormal(text);

        return searchResults.size();
    }

    boolean goToSearchResult(int offset) {
        // TODO jump to occurence
        return true;
    }
}
