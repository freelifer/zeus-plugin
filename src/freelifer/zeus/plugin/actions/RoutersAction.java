package freelifer.zeus.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
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
    private static final String ACTIVITY_API_JAVA_CLASS_NAME = "ActivityApi";

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

        Dialogs.showErrorNotification(project, "Not Implement");
    }
}
