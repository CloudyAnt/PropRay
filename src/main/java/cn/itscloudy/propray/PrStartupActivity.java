package cn.itscloudy.propray;

import com.intellij.ide.IdeEventQueue;
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
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PrStartupActivity implements StartupActivity, ProjectActivity {

    boolean ran;

    @Override
    public void runActivity(@NotNull Project project) {
        runAppAndProjectActivity(project);
    }

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        runAppAndProjectActivity(project);
        return null;
    }

    private void runAppAndProjectActivity(@NotNull Project project) {
        runAppActivity(project);
    }

    private void runAppActivity(@NotNull Project project) {
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

            IdeEventQueue.getInstance().addDispatcher(new PrEventDispatcher(), this);
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
            PropRayInstaller.install(editor);
        }

        @Override
        public void editorReleased(@NotNull EditorFactoryEvent event) {
            PropRayInstaller.uninstall(event.getEditor());
        }
    }

    private static class PrEventDispatcher implements IdeEventQueue.EventDispatcher {

        @Override
        public boolean dispatch(@NotNull AWTEvent awtEvent) {
            if (awtEvent instanceof KeyEvent keyEvent &&
                    (keyEvent.getID() == KeyEvent.KEY_TYPED || keyEvent.getID() == KeyEvent.KEY_PRESSED)) {
                ControlBox focusedControlBox = PrContext.getInstance().getFocusedControlBox();
                if (focusedControlBox != null) {
                    focusedControlBox.afterKeyTyped(keyEvent);
                    keyEvent.consume();
                    return true;
                }
            }
            return false;
        }
    }
}
