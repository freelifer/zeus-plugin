package org.jetbrains.zeus;


import com.intellij.icons.AllIcons;
import com.intellij.ide.fileTemplates.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import icons.ImagesIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 文件模板
 * @author zhukun
 * @version 1.0
 */
public class ZeusFileTemplateProvider implements FileTemplateGroupDescriptorFactory {

    @NonNls
    public static final String OBJECT_FILE_TEMPLATE = "Object.java";
    @NonNls
    public static final String MVPACTIVITY_FILE_TEMPLATE = "MvpActivity.java";
    @NonNls
    public static final String MVPCONTRACT_FILE_TEMPLATE = "MvpContract.java";
    @NonNls
    public static final String MVPPRESENTER_FILE_TEMPLATE = "MvpPresenter.java";


    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("zeus-plugin", AllIcons.Nodes.Plugin);
        group.addTemplate(new FileTemplateDescriptor(OBJECT_FILE_TEMPLATE, StdFileTypes.JAVA.getIcon()));
        group.addTemplate(new FileTemplateDescriptor(MVPACTIVITY_FILE_TEMPLATE, StdFileTypes.JAVA.getIcon()));
        return group;
    }

    @Nullable
    public static PsiElement createFromTemplate(String templateName, @NotNull PsiDirectory directory, Map<String, Object> map) throws Exception {
        try {
            final FileTemplateManager manager = FileTemplateManager.getInstance(directory.getProject());
            Properties defaultProperties = manager.getDefaultProperties();
            if (map == null) {
                map = new HashMap<>();
            }
            FileTemplateUtil.putAll(map, defaultProperties);
            final FileTemplate template = manager.getJ2eeTemplate(templateName);
            return FileTemplateUtil.createFromTemplate(template, templateName, map, directory, null);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static PsiElement createFromTemplate(@NotNull Project project,
                                                @NotNull VirtualFile rootDir,
                                                @NotNull String templateName,
                                                @NotNull String fileName,
                                                @NotNull Map map) throws Exception {
        rootDir.refresh(false, false);
        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(rootDir);
        if (directory != null) {
            return createFromTemplate(templateName, fileName, directory, map);
        }

        return null;
    }

    public static PsiElement createFromTemplate(String templateName, String fileName, @NotNull PsiDirectory directory, Map map) throws Exception {
        FileTemplateManager manager = FileTemplateManager.getInstance(directory.getProject());
        FileTemplate template = manager.getJ2eeTemplate(templateName);
        String result = FileTemplateUtil.mergeTemplate(map, template.getText(), true);

        return null;
    }

    @Nullable
    public static PsiElement createFromTemplate(@NotNull Project project,
                                                @NotNull VirtualFile rootDir,
                                                @NotNull String templateName,
                                                @NotNull String fileName,
                                                @NotNull Properties properties) throws Exception {
        rootDir.refresh(false, false);
        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(rootDir);
        if (directory != null) {
            return createFromTemplate(templateName, fileName, directory, properties);
        }

        return null;
    }

    @Nullable
    public static PsiElement createFromTemplate(@NotNull Project project,
                                                @NotNull VirtualFile rootDir,
                                                @NotNull String templateName,
                                                @NotNull String fileName) throws Exception {
        return createFromTemplate(project, rootDir, templateName, fileName, FileTemplateManager.getInstance(project).getDefaultProperties());
    }

    public static PsiElement createFromTemplate(String templateName, String fileName, @NotNull PsiDirectory directory, Properties properties) throws Exception {
        FileTemplateManager manager = FileTemplateManager.getInstance(directory.getProject());
        FileTemplate template = manager.getJ2eeTemplate(templateName);
        return FileTemplateUtil.createFromTemplate(template, fileName, properties, directory);
    }

    public static PsiElement createFromTemplate(String templateName, String fileName, @NotNull PsiDirectory directory) throws Exception {
        return createFromTemplate(templateName, fileName, directory, FileTemplateManager.getInstance(directory.getProject()).getDefaultProperties());
    }

    @NotNull
    public static String getFileNameByNewElementName(@NotNull String name) {
        if (!FileUtilRt.extensionEquals(name, "xml")) {
            name += ".xml";
        }
        return name;
    }
}
