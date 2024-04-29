package cn.itscloudy.propray;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

public class PropRayLineScanAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean act = editor != null
                && "properties".equals(editor.getVirtualFile().getExtension());
        if (act) {
            act(editor);
        }
    }

    private void act(Editor editor) {
        String text = editor.getDocument().getText();
    }

}
