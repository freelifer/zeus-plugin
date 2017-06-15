package freelifer.zeus.plugin.settings;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author zhukun on 2017/4/20.
 * @version 1.0
 */
public class Settings implements Configurable {

    public static final String XLS_FILE_PATH_KEY = "__xls_file_path_key";
    public static final String TARGET_FILE_PATH_KEY = "__target_file_path_key";
    public static final String LIST_KEY = "__list_key";

    private SettingsPanel settingsPanel;

    @Nls
    @Override
    public String getDisplayName() {
        return "zeus-plugin";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
//        reset();
        settingsPanel = new SettingsPanel();
        settingsPanel.reset();
        return settingsPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
//        PropertiesComponent.getInstance().setValue("abcd", textArea1.getText());
        settingsPanel.apply();
    }

    @Override
    public void reset() {
//        CommonActionsPanel.Buttons.ADD;
//        CommonActionsPanel panel = new CommonActionsPanel();
//        panel.getAnActionButton()
//        CustomLineBorder border = new CustomLineBorder();
//        ToolbarDecorator list = ToolbarDecorator.createDecorator(list1);
//        button1.setIcon(IconUtil.getAddIcon());
//        String[] data = {"AAA", "BBB", "CCC"};
//        list1.setListData(data);
//        textArea1.setText(PropertiesComponent.getInstance().getValue("abcd"));

        settingsPanel.reset();
    }
}
