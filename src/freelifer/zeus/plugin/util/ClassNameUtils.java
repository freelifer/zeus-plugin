package freelifer.zeus.plugin.util;

/**
 * @author zhukun on 2017/3/16.
 * @version 1.0
 */
public class ClassNameUtils {

    public static String getClassFullName(String packageName, String className) {
        if (Tools.isEmpty(className)) {
            return className;
        }
        if (className.startsWith(".")) {
            if (Tools.isEmpty(packageName)) {
                return null;
            }
            return packageName + className;
        } else {
            return className;
        }
    }
}
