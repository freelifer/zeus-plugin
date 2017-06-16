package freelifer.zeus.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import freelifer.zeus.plugin.entity.RouterField;
import freelifer.zeus.plugin.util.*;
import freelifer.zeus.plugin.writer.GenerateRoutersWriter;

import java.io.File;
import java.util.List;

/**
 * @author zhukun on 2017/6/16.
 * @version 1.0
 */
public class GenerateRoutersAction extends AnAction {

    //    private static final String ANDROIDMANIFEST_FILE_PATH = "/main/AndroidManifest.xml";
    private static final String ANDROIDMANIFEST_FILE_PATH = "/AndroidManifest.xml";
    //    private static final String JAVA_CLASS_DIR_PATH = "/main/java/";
    private static final String JAVA_CLASS_DIR_PATH = "/";
    private static final String NOTIFY = "Notify";
    private static final String BINDINTENT = "BindIntent";

    private Project project;
    private String packageName;
    private List<RouterField> routerFields;

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if (project == null || editor == null) {
            return;
        }
        this.project = project;
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }

        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length == 0) {
            Dialogs.showErrorNotification(project, "Not Module ,please to create one module!");
            return;
        }
        for (Module module : modules) {
            String moduleName = module.getName();
            VirtualFile[] list = ModuleRootManager.getInstance(module).getSourceRoots();
            Log.d("%s module path: %s.", moduleName, list.toString());
//            String modulePath = module.getmodule
            findAndroidManifest(list);

            filterExistenceRouterFields(getTargetClass(editor, file));

            boolean flag = true;
            if (flag) { // generate injections
                new GenerateRoutersWriter(file, getTargetClass(editor, file), "Generate Injections", routerFields).execute();
            } else { // just notify user about no element selected
                Dialogs.showInfoNotification(project, "No injection was selected");
            }
        }
    }

    private void filterExistenceRouterFields(PsiClass psiClass) {
        if (routerFields == null || routerFields.size() == 0) {
            return;
        }
        PsiMethod[] methods = psiClass.getAllMethods();
        List<String> classList = New.arrayList();
        for (PsiMethod method : methods) {
            PsiModifierList psiModifierList = method.getModifierList();
            boolean found = PsiClassUtils.filter(psiModifierList, NOTIFY, (String annotationName, PsiAnnotation psiAnnotation) -> {
                PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.findAttributeValue("value");
                if (psiAnnotationMemberValue == null) {
                    return;
                }
                String className = Tools.filter(psiAnnotationMemberValue.getText());
                classList.add(className);
                Log.d("Notify annotationName: %s, value: %s", annotationName, className);
            });
        }

        if (classList.size() == 0) {
            return;
        }

        int len = routerFields.size();

        for (int i = len - 1; i >= 0; i--) {
            RouterField routerField = routerFields.get(i);
            if (Tools.contains(classList, routerField.className)) {
                routerFields.remove(i);
            }
        }

        if (routerFields.size() == 0) {
            Dialogs.showInfoNotification(project, "Not New Method Found");
        }
    }

    /**
     * @param list module src 目录
     */
    private void findAndroidManifest(VirtualFile[] list) {
        if (list == null) {
            return;
        }
        if (list.length != 1) {
            Dialogs.showErrorNotification(project, "the module has too many Source Roots! I don't support it yet");
            return;
        }
        for (VirtualFile virtualFile : list) {
            Log.d("findAndroidManifest list %s", virtualFile.getPath());
            VirtualFile targetVF = LocalFileSystem.getInstance().refreshAndFindFileByPath(virtualFile.getPath() + ANDROIDMANIFEST_FILE_PATH);
            if (targetVF != null) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(targetVF);
                Log.d("PsiFile class %s", psiFile.getClass().toString());
                if (psiFile instanceof XmlFile) {
                    XmlFile xmlFile = (XmlFile) psiFile;
                    XmlTag manifestXmlTag = xmlFile.getRootTag();
                    if (manifestXmlTag == null) {
                        continue;
                    }
                    parseManifestXmlTag(manifestXmlTag);
                    XmlTag applicationXmlTag = manifestXmlTag.findFirstSubTag("application");
                    if (applicationXmlTag == null) {
                        continue;
                    }
                    parseApplicationXmlTag(applicationXmlTag);
                    XmlTag[] activityXmlTags = applicationXmlTag.findSubTags("activity");
                    parseActivityXmlTags(activityXmlTags);
                    Log.d("XmlFile %s %s", manifestXmlTag, applicationXmlTag, activityXmlTags);
                }
            }

            scanActivityFile(virtualFile.getPath());

        }
    }

    private void scanActivityFile(String path) {
        if (routerFields == null || routerFields.size() == 0) {
            return;
        }
        for (RouterField routerField : routerFields) {
            String classPath = routerField.className.replace('.', File.separatorChar);
            classPath = path + JAVA_CLASS_DIR_PATH + classPath + ".java";
            Log.d("Java Class Path %s", classPath);
            VirtualFile targetVF = LocalFileSystem.getInstance().refreshAndFindFileByPath(classPath);
            if (targetVF != null) {
                Log.d("Java Class Path %s", targetVF.getPath());
                PsiJavaFile psiJavaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(targetVF);
                if (psiJavaFile == null) {
                    continue;
                }
                PsiClass[] psiClasses = psiJavaFile.getClasses();
                for (PsiClass psiClass : psiClasses) {
                    // field 解析
                    PsiField[] psiField = psiClass.getFields();
                    for (final PsiField field : psiField) {
                        // field注解和访问权限
                        PsiModifierList psiFieldModifierList = field.getModifierList();
                        if (psiFieldModifierList == null) {
                            continue;
                        }
                        boolean found = PsiClassUtils.filter(psiFieldModifierList, BINDINTENT, (String annotationName, PsiAnnotation psiAnnotation) -> {
                            RouterField.IntentField intentFild = RouterField.IntentField.create();
                            intentFild.annotationName = annotationName;
                            String typeName = field.getTypeElement().getText();
                            String value = field.getNameIdentifier().getText();
                            intentFild.typeName = typeName;
                            intentFild.value = value;
                            if (routerField.intentFieldList == null) {
                                routerField.intentFieldList = New.arrayList();
                            }
                            routerField.intentFieldList.add(intentFild);
                            Log.d("BindIntent annotationName: %s, typeName: %s, value: %s", annotationName, typeName, value);
                        });
                    }
                }
            }
        }
    }


    private void parseManifestXmlTag(XmlTag manifestXmlTag) {
        String packageName = manifestXmlTag.getAttributeValue("package", null);
        Log.d("Android package:%s", packageName);
        this.packageName = packageName;
    }

    private void parseApplicationXmlTag(XmlTag applicationXmlTag) {

    }

    private void parseActivityXmlTags(XmlTag[] activityXmlTags) {
        if (activityXmlTags == null || activityXmlTags.length == 0) {
            return;
        }
        if (routerFields != null) {
            routerFields.clear();
        }
        for (XmlTag xmlTag : activityXmlTags) {
            String activityName = xmlTag.getAttributeValue("android:name", null);
            activityName = ClassNameUtils.getClassFullName(this.packageName, activityName);
            Log.d("Android Activity Name:%s", activityName);
            if (routerFields == null) {
                routerFields = New.arrayList();
            }
            RouterField routerField = RouterField.create();
            routerField.className = activityName;
            routerFields.add(routerField);
        }
    }

    private PsiClass getTargetClass(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if (element == null) {
            return null;
        } else {
            PsiClass target = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            return target instanceof SyntheticElement ? null : target;
        }
    }
}
