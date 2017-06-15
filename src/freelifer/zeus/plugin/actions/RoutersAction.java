package freelifer.zeus.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import freelifer.zeus.plugin.util.*;

import java.io.File;
import java.util.List;

/**
 * Android Routers框架 自动识别AndroidManifest.xml <br>
 * 来自动生产Activity跳转api接口
 *
 * @author zhukun on 2017/6/15.
 * @version 1.0
 */
public class RoutersAction extends AnAction {

    //    private static final String ANDROIDMANIFEST_FILE_PATH = "/main/AndroidManifest.xml";
    private static final String ANDROIDMANIFEST_FILE_PATH = "/AndroidManifest.xml";
    //    private static final String JAVA_CLASS_DIR_PATH = "/main/java/";
    private static final String JAVA_CLASS_DIR_PATH = "/";

    private Project project;
    private String packageName;
    private List<String> classList;

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext context = e.getDataContext();
        Project project = CommonDataKeys.PROJECT.getData(context);
        if (project == null) {
            return;
        }
        this.project = project;

        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length == 0) {
            Dialogs.showErrorDialog("Not Module ,please to create one module!", e);
            return;
        }
        for (Module module : modules) {
            String moduleName = module.getName();
            VirtualFile[] list = ModuleRootManager.getInstance(module).getSourceRoots();
            Log.d("%s module path: %s.", moduleName, list.toString());
//            String modulePath = module.getmodule
            findAndroidManifest(list);
        }
    }

    private void findAndroidManifest(VirtualFile[] list) {
        if (list == null) {
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
        if (classList == null || classList.size() == 0) {
            return;
        }
        for (String className : classList) {
            String classPath = className.replace('.', File.separatorChar);
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
                        PsiClassUtils.filter(psiFieldModifierList, "BindIntent", (String annotationName, PsiAnnotation psiAnnotation) -> {

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
        for (XmlTag xmlTag : activityXmlTags) {
            String activityName = xmlTag.getAttributeValue("android:name", null);
            activityName = ClassNameUtils.getClassFullName(this.packageName, activityName);
            Log.d("Android Activity Name:%s", activityName);
            if (classList == null) {
                classList = New.arrayList();
            }
            classList.add(activityName);
        }
    }
}
