package freelifer.zeus.plugin.util;

/**
 * @author zhukun on 2017/3/16.
 * @version 1.0
 */
public class Log {
    public static final boolean DEBUG = true;

    public static void d(String output, Object... args) {
        if (DEBUG) {
            System.out.println(String.format(output, args));
        }
    }

    public static void e(Exception e) {
        e.printStackTrace();
    }
}
