package cn.itscloudy.propray;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
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
        CaretModel caretModel = editor.getCaretModel();
        int visualLineStart = caretModel.getVisualLineStart();
        int visualLineEnd = caretModel.getVisualLineEnd();
        String iso = editor.getDocument().getText(new TextRange(visualLineStart, visualLineEnd));
        if (iso.endsWith("\n")) {
            iso = iso.substring(0, iso.length() - 1);
            visualLineEnd -= 1;
        }

        String normal = PropRayUtil.toNormal(iso);
        new PropRayIso2NormalMask(editor, visualLineEnd, normal, iso).render();
    }

}
