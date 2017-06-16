package freelifer.zeus.plugin.util;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

/**
 * @author zhukun on 2017/6/16.
 */
public class InjectWriter extends WriteCommandAction.Simple {
    protected InjectWriter(Project project, PsiFile... files) {
        super(project, files);
    }

    @Override
    protected void run() throws Throwable {

    }
}
