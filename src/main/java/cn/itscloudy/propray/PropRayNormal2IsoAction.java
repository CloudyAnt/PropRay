package cn.itscloudy.propray;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class PropRayNormal2IsoAction extends AnAction {

    public PropRayNormal2IsoAction() {
        Presentation tp = getTemplatePresentation();
        tp.setText(PrConst.get("Action.Normal2Iso"));
        tp.setDescription(PrConst.get("Action.Normal2Iso.Desc"));
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
        String isoText = PropRayUtil.toIso(selectedText);

        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
            int offset = selectionModel.getSelectionStart();
            int end = selectionModel.getSelectionEnd();
            editor.getDocument().replaceString(offset, end, isoText);
        });

        PropRayEditorConsul.hideControlBoxOf(editor);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean show = editor != null
                && "properties".equals(editor.getVirtualFile().getExtension())
                && PropRayUtil.containsNonIsoChars(editor.getSelectionModel().getSelectedText());

        e.getPresentation().setEnabledAndVisible(show);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
