package freelifer.zeus.plugin.settings;


import java.io.Serializable;
import java.util.List;

/**
 * @author zhukun on 2017/4/25.
 * @version 1.0
 */
public class SettingsInfo implements Serializable {

    public int version;
    public Config config;


    @Override
    public String toString() {
        return "version: " + version + " config: " + ((config == null) ? null : config.toString());
    }

    public static class Config implements Serializable {
        public String xls;
        public List<Template> templates;

        @Override
        public String toString() {
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("[xls: ");
            sBuilder.append(xls);
            if (templates != null && templates.size() > 0) {
                sBuilder.append(" templates: ");
                for (Template template : templates) {
                    sBuilder.append(template.toString());
                    sBuilder.append("\n");
                }
                sBuilder.append("]");
            } else {
                sBuilder.append(" templates: null]");
            }
            return sBuilder.toString();
        }
    }

    public static class Template implements Serializable {
        public String name;

        @Override
        public String toString() {
            return "[name: " + name + "]";
        }
    }
}
