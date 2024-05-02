package cn.itscloudy.propray;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.components.JBLayeredPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PropRayEditorConsul {
    public static final Key<PropRayEditorConsul> KEY = Key.create(PropRayEditorConsul.class.getName());

    private final Editor editor;
    private final PropRayCanvas canvas;
    private final ControlBox controlBox;

    private PropRayEditorConsul(Editor editor) {
        this.editor = editor;
        canvas = PropRayCanvas.getOrBind(editor);
        controlBox = new ControlBox(this);
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
        JComponent switchButton = controlBox.getSwitchButton();
        layeredPane.add(switchButton);
        layeredPane.setLayer(switchButton, 100);

        JPanel box = controlBox.getRoot();
        layeredPane.add(box);
        layeredPane.setLayer(box, 100);


        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                canvas.clear();
            }
        });
    }

    void afterSelection(TextRange range) {
        int startOffset = range.getStartOffset();
        int endOffset = range.getEndOffset();
        if (startOffset == endOffset) {
            return;
        }
        canvas.removeMaskIfContains(startOffset, endOffset);
    }

    java.util.List<PropRaySearchResultMask> searchResults = new ArrayList<>();
    int currentSearchResultIndex = 0;

    int search(String str) {
        clearSearchResults();

        String isoStr = PropRayUtil.toIso(str);
        String text = editor.getDocument().getText();

        int idx = text.indexOf(isoStr);
        int num = 0;
        while (idx != -1) {
            PropRaySearchResultMask mask = new PropRaySearchResultMask(editor, idx, isoStr);
            mask.setCurrent(num == currentSearchResultIndex);

            searchResults.add(mask);
            canvas.add(mask);
            idx = text.indexOf(isoStr, idx + 1);

            num++;
        }

        if (!searchResults.isEmpty()) {
            canvas.repaint();
        }
        return searchResults.size();
    }

    void clearSearchResults() {
        searchResults.clear();
        canvas.clear();
        currentSearchResultIndex = 0;
    }

    void goToSearchResult(int offset) {
        searchResults.get(currentSearchResultIndex).setCurrent(false);
        int idx = currentSearchResultIndex + offset;
        if (idx < 0) {
            idx = searchResults.size() - 1;
        } else if (idx >= searchResults.size()) {
            idx = idx % searchResults.size();
        }
        searchResults.get(idx).setCurrent(true);
        currentSearchResultIndex = idx;
        canvas.repaint();
    }

    static void hideControlBoxOf(Editor editor) {
        if (editor == null) {
            return;
        }

        PropRayEditorConsul editorConsul = editor.getUserData(PropRayEditorConsul.KEY);
        if (editorConsul != null) {
            editorConsul.controlBox.hideRoot();
        }
    }
}
