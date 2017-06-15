package freelifer.zeus.plugin.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;

/**
 * @author zhukun on 2017/3/16.
 * @version 1.0
 */
public class PsiClassUtils {

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
