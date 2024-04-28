package cn.itscloudy.propray;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class PropRayReplaceAction extends AnAction {

    public PropRayReplaceAction() {
        Presentation tp = getTemplatePresentation();
        tp.setText(PrConst.get("Replace2Ues"));
        tp.setDescription(PrConst.get("Replace2UesDesc"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            return;
        }
        String reversedText = PropRayUtil.reverse(selectedText);

        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
            int offset = selectionModel.getSelectionStart();
            int end = selectionModel.getSelectionEnd();
            editor.getDocument().replaceString(offset, end, reversedText);
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean show = editor != null
                && editor.getSelectionModel().getSelectionStart() != editor.getSelectionModel().getSelectionEnd()
                && "properties".equals(editor.getVirtualFile().getExtension());

        e.getPresentation().setEnabledAndVisible(show);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}