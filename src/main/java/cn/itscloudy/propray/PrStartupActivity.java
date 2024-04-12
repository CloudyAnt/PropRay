package cn.itscloudy.propray;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class PrStartupActivity implements StartupActivity {

    boolean ran;

    @Override
    public void runActivity(@NotNull Project project) {
        if (!ran) {
            DumbService.getInstance(project).smartInvokeLater(() ->
                    ProgressManager.getInstance().run(new InitTask(project)));
        }
        ran = true;
    }

    public static class InitTask extends Task.Backgroundable implements Disposable {
        public InitTask(@NotNull Project project) {
            super(project, PrConst.get("Init"));
        }

        @Override
        public void dispose() {
        }

        @Override
        public void run(@NotNull ProgressIndicator progressIndicator) {
            PrEditorFactoryListener factoryListener = new PrEditorFactoryListener();
            EditorFactory.getInstance().addEditorFactoryListener(factoryListener,this);
            Editor[] allEditors = EditorFactory.getInstance().getAllEditors();
            for (Editor editor : allEditors) {
                PrEditorFactoryListener.init(editor);
            }
        }
    }

    public static class PrEditorFactoryListener implements EditorFactoryListener {
        private static final Key<Boolean> INITIALIZED = Key.create("PrEditorFactoryListener.INITIALIZED");

        @Override
        public void editorCreated(@NotNull EditorFactoryEvent event) {
            init(event.getEditor());
        }

        public static void init(@NotNull Editor editor) {
            if (editor.getUserData(INITIALIZED) != null) {
                return;
            }
            editor.putUserData(INITIALIZED, true);
            VirtualFile virtualFile = editor.getVirtualFile();
            if (virtualFile == null || !"properties".equals(virtualFile.getExtension())) {
                return;
            }
            PropRay.install(editor);
        }

        @Override
        public void editorReleased(@NotNull EditorFactoryEvent event) {
            PropRay.uninstall(event.getEditor());
        }
    }
}
