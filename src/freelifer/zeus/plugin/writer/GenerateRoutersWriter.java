package freelifer.zeus.plugin.writer;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import freelifer.zeus.plugin.entity.RouterField;
import freelifer.zeus.plugin.util.ClassNameUtils;

import java.util.List;

/**
 * @author zhukun on 2017/6/16.
 */
public class GenerateRoutersWriter extends WriteCommandAction.Simple {

    private Project project;
    private PsiClass clazz;
    private PsiFile psiFile;
    protected PsiElementFactory psiElementFactory;

    private List<RouterField> routerFields;

    public GenerateRoutersWriter(PsiFile psiFile, PsiClass clazz, String command, List<RouterField> routerFields) {
        super(clazz.getProject(), command);

        this.project = clazz.getProject();
        this.clazz = clazz;
        this.psiFile = psiFile;
        this.psiElementFactory = JavaPsiFacade.getElementFactory(project);

        this.routerFields = routerFields;
    }

    @Override
    protected void run() throws Throwable {
        generateMethod();

        // reformat class
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
        styleManager.optimizeImports(psiFile);
        styleManager.shortenClassReferences(clazz);
        new ReformatCodeProcessor(project, clazz.getContainingFile(), null, false).runWithoutProgress();
    }

    private void generateMethod() {
        if (routerFields == null || routerFields.size() == 0) {
            return;
        }
        StringBuilder method = new StringBuilder();
        for (RouterField routerField: routerFields) {
            method.setLength(0);
            method.append("@Notify(\""+ routerField.className +"\")");
            method.append("Navigate to");
            method.append(ClassNameUtils.getSimpleClassName(routerField.className));
            method.append("(");
            List<RouterField.IntentField> intentFields = routerField.intentFieldList;
            if (intentFields != null && intentFields.size() != 0) {
                int len = intentFields.size();
                for (int i = 0; i < len; i++) {
                    RouterField.IntentField intentField = intentFields.get(i);
                    method.append("@Input " + intentField.typeName + " " + intentField.value);
                    if (i != len -1){
                        method.append(", ");
                    }
                }
            }
            method.append(");");
            clazz.add(psiElementFactory.createMethodFromText(method.toString(), clazz));
        }
    }
}
