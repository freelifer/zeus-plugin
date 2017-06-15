package freelifer.zeus.plugin.settings;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author zhukun on 2017/4/20.
 * @version 1.0
 */
public class SettingsPanel extends JPanel {

    private JBList<String> fileList;
    private JButton addBtn;

    private int i = 1;

    public SettingsPanel() {
        super();
        init();
    }

    private void init() {

        JBTabbedPane jTabbedPane = new JBTabbedPane();
        createSettingsTab(jTabbedPane);
        createTTemplatesTab(jTabbedPane);
        setLayout(new GridLayout(1, 1));
        add(jTabbedPane);
    }

    public void reset() {
        String xlsPath = PropertiesComponent.getInstance().getValue(Settings.XLS_FILE_PATH_KEY);
        xlsTextField.setText(xlsPath);
        String targetPath = PropertiesComponent.getInstance().getValue(Settings.TARGET_FILE_PATH_KEY);
        targetTextField.setText(targetPath);

//        SettingsInfo settingsInfo = SettingsHelper.getInstance().getSettingsInfo();
//        java.util.List<SettingsInfo.Template> templates = SettingsHelper.getInstance().getTemplates();
//        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
//        if (templates != null && templates.size() > 0) {
//            for (SettingsInfo.Template template : templates) {
//                defaultListModel.addElement(template.name);
//            }
//        }
//        fileList.setModel(defaultListModel);
//
//        xlsTextField.setText(settingsInfo.config.xls);
    }

    public void apply() {
        PropertiesComponent.getInstance().setValue(Settings.XLS_FILE_PATH_KEY, xlsTextField.getText());
        PropertiesComponent.getInstance().setValue(Settings.TARGET_FILE_PATH_KEY, targetTextField.getText());
//        DefaultListModel<String> listModel = (DefaultListModel<String>) fileList.getModel();
//
//        SettingsInfo settingsInfo = SettingsHelper.getInstance().getSettingsInfo();
//        settingsInfo.config.xls = xlsTextField.getText();
//        java.util.List<SettingsInfo.Template> list = new ArrayList<>();
//        if (listModel != null && listModel.size() > 0) {
//            for (int i = 0; i < listModel.size(); i++) {
//                String str = listModel.get(i);
//                SettingsInfo.Template template = new SettingsInfo.Template();
//                template.name = str;
//                list.add(template);
//            }
//        }
//        settingsInfo.config.templates = list;
//        SettingsHelper.getInstance().commit();

    }

    JTextField xlsTextField;
    JTextField targetTextField;

    private void createSettingsTab(JBTabbedPane jTabbedPane) {
        JBPanel settings = new JBPanel(new BorderLayout());
        jTabbedPane.add("Settings", settings);

        JBPanel xlsxPanel = new JBPanel(new GridBagLayout());
        JLabel label = new JLabel("xls文件：");
        xlsTextField = new JTextField();
        JButton button = new JButton("...");

        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        xlsxPanel.add(label, s);
        s.gridwidth = 1;
        s.weightx = 1;
        xlsxPanel.add(xlsTextField, s);
        s.gridwidth = 0;
        s.weightx = 0;
        xlsxPanel.add(button, s);
        JLabel targetLabel = new JLabel("生成文件目录：");
        targetTextField = new JTextField();
        JButton targetButton = new JButton("...");

        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        xlsxPanel.add(targetLabel, s);
        s.gridwidth = 1;
        s.weightx = 1;
        xlsxPanel.add(targetTextField, s);
        s.gridwidth = 0;
        s.weightx = 0;
        xlsxPanel.add(targetButton, s);
        settings.add("North", xlsxPanel);

        button.addActionListener((e) -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int state = jfc.showOpenDialog(null);
            if (state != 1) {
                File f = jfc.getSelectedFile();
                xlsTextField.setText(f.getAbsolutePath());
            }
        });

        targetButton.addActionListener((e) -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int state = jfc.showOpenDialog(null);
            if (state != 1) {
                File f = jfc.getSelectedFile();
                targetTextField.setText(f.getAbsolutePath());
            }
        });
    }

    private void createTTemplatesTab(JBTabbedPane jTabbedPane) {
        JBPanel templatesTab = new JBPanel(new BorderLayout());
        jTabbedPane.add("Templates", templatesTab);
        java.util.List<SettingsInfo.Template> templates = SettingsHelper.getInstance().getTemplates();
        JPanel north = new JPanel();
        templatesTab.add("North", north);
        fileList = new JBList<>();
        if (templates != null && templates.size() > 0) {
            DefaultListModel defaultListModel = new DefaultListModel();
            for (SettingsInfo.Template template : templates) {
                defaultListModel.addElement(template.name);
            }
            fileList.setModel(defaultListModel);
        }

        JBScrollPane west = new JBScrollPane();
        west.setPreferredSize(new Dimension(198, 164));
        west.setViewportView(fileList);
        templatesTab.add("West", west);

        ImageButton addBtn = new ImageButton(IconUtil.getAddIcon());

        addBtn.setOnClickListener((e) -> {
            DefaultListModel<String> listModel = (DefaultListModel<String>) fileList.getModel();
            SettingsInfo.Template template = new SettingsInfo.Template();
            template.name = "New File " + i;
            SettingsHelper.getInstance().putTemplate(template);
            listModel.addElement(template.name);
            i++;
        });

        ImageButton removeBtn = new ImageButton(IconUtil.getRemoveIcon());
        removeBtn.setOnClickListener((e) -> {
            int index = fileList.getSelectedIndex();
            if (index < 0) {
                return;
            }
            DefaultListModel<String> listModel = (DefaultListModel<String>) fileList.getModel();
            if (index >= listModel.size()) {
                return;
            }
            listModel.remove(index);
        });


        north.add(addBtn);
        north.add(removeBtn);
        templatesTab.add("Center", new JButton("Center"));

//        add("North", toolbar.getComponent());
    }
}
