package freelifer.zeus.plugin.util;

import java.util.List;

/**
 * @author zhukun on 2017/3/16.
 * @version 1.0
 */
public class Tools {

    public static boolean isEmpty(String src) {
        return (src == null || src.length() == 0);
    }

    public static <T> boolean contains(List<T> list, T src) {
        if (list == null || list.size() == 0) {
            return false;
        }
        for (T t : list) {
            if (src.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public static String filter(String src) {
        if (isEmpty(src)) {
            return src;
        }
        if (src.startsWith("\"")) {
            int len = src.length();
            return src.substring(1, len - 1);
        }
        return src;

    }
}
