package freelifer.zeus.plugin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author zhukun on 2017/3/16.
 * @version 1.0
 */
public class PsiClassUtils {

    public static PsiFile createPsiClass(final Project project, final PsiDirectory psiDirectory, final String className) {
        Computable<PsiFile> c = () -> {
            // 文件名，文件类型，文件内容 创建PsiFile(内存中)
            PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText(className + ".java", StdFileTypes.JAVA, "");

            // 文件PsiFile 添加到文件目录PsiDirectory中(磁盘中)
            psiDirectory.add(psiFile);
            FileEditorManager.getInstance(project).openFile(psiFile.getVirtualFile(), true);
            return psiFile;
        };

        return ApplicationManager.getApplication().runWriteAction(c);
    }

    public static boolean filter(@NotNull PsiModifierList psiModifierList, @NotNull String filters, FilterListener listener) {
        boolean flag = false;
        PsiAnnotation[] psiAnnotations = psiModifierList.getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            PsiJavaCodeReferenceElement element = psiAnnotation.getNameReferenceElement();
            if (element == null) {
                continue;
            }
            String annotationName = element.getText();
            if (filters.equals(annotationName)) {
                flag = true;
                listener.filter(annotationName, psiAnnotation);
            }
        }
        return flag;
    }

    public interface FilterListener {
        void filter(String annotationName, PsiAnnotation psiAnnotation);
    }
}
