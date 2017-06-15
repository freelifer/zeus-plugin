package freelifer.zeus.plugin.settings;


import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import freelifer.zeus.plugin.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置帮助类
 * <p>
 * Created by zhukun on 2017/4/25.
 */
public class SettingsHelper {

    public static final String BUNDLED_CONFIG_PATH = "/PIGTools/lib/config";
    private String configName = "pig.json";

    private SettingsInfo settingsInfo;

    public SettingsHelper() {
    }

    private static class SigleTonHolder {
        private static final SettingsHelper INSTANCE = new SettingsHelper();
    }

    public static SettingsHelper getInstance() {
        return SigleTonHolder.INSTANCE;
    }

    public SettingsInfo getSettingsInfo() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settingsInfo;
    }

    public void putTemplate(SettingsInfo.Template template) {

        if (settingsInfo.config == null) {
            settingsInfo.config = new SettingsInfo.Config();
        }
        if (settingsInfo.config.templates == null) {
            settingsInfo.config.templates = new ArrayList<>();
        }
        settingsInfo.config.templates.add(template);

        commit();
    }

    public List<SettingsInfo.Template> getTemplates() {
        if (settingsInfo == null || settingsInfo.config == null) {
            return null;
        }

        if (settingsInfo.config.templates == null || settingsInfo.config.templates.size() <= 0) {
            return null;
        }
        return settingsInfo.config.templates;
    }

    public SettingsInfo readConfig() {
        SettingsInfo settings = null;
        String configPath = FileUtil.toSystemIndependentName(PathManager.getPluginsPath() + BUNDLED_CONFIG_PATH);

        File file = new File(configPath, configName);
        if (file.exists()) {
            VirtualFile root = LocalFileSystem.getInstance().findFileByPath(file.getAbsolutePath());

            String json = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
                Document document = FileDocumentManager.getInstance().getDocument(root);
                return document != null ? document.getText() : null;
            });

            Gson gson = new Gson();
            settings = gson.fromJson(json, SettingsInfo.class);
        }

        return settings;
    }

    private void init() throws IOException {
        String configPath = FileUtil.toSystemIndependentName(PathManager.getPluginsPath() + BUNDLED_CONFIG_PATH);

        File file = new File(configPath, configName);
        if (file.exists()) {
            VirtualFile root = LocalFileSystem.getInstance().findFileByPath(file.getAbsolutePath());

            String json = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
                Document document = FileDocumentManager.getInstance().getDocument(root);
                return document != null ? document.getText() : null;
            });

            Gson gson = new Gson();
            settingsInfo = gson.fromJson(json, SettingsInfo.class);
        } else {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    return;
                }
            }
            if (file.createNewFile()) {
                defaultSettingsInfo();
            }
        }
    }

    private void defaultSettingsInfo() {
        SettingsInfo settingsInfo = new SettingsInfo();
        settingsInfo.version = 1;
        settingsInfo.config = new SettingsInfo.Config();
        this.settingsInfo = settingsInfo;

        commit();
    }

    public void commit() {
        Gson gson = new Gson();
        String json = gson.toJson(settingsInfo, SettingsInfo.class);
        Log.d("json: " + json);
        String configPath = FileUtil.toSystemIndependentName(PathManager.getPluginsPath() + BUNDLED_CONFIG_PATH);
        File file = new File(configPath, configName);
        VirtualFile root = LocalFileSystem.getInstance().findFileByPath(file.getAbsolutePath());

        ApplicationManager.getApplication().runWriteAction(() -> {

//            Document document = FileDocumentManager.getInstance().getDocument(root);
//            document.setText(json);
//            PsiDocumentManager.getInstance(this.project).doPostponedOperationsAndUnblockDocument(document);
            try {
                OutputStream os = root.getOutputStream(null);
                os.write(json.getBytes());
                os.flush();

                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    public void print() {
        Log.d(settingsInfo == null ? "null" : settingsInfo.toString());
    }

}
