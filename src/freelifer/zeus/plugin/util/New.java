package freelifer.zeus.plugin.util;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhukun on 2017/6/15.
 * @version 1.0
 */
public class New {

    private New() {
    }

    @NotNull
    public static <K, V> Map<K, V> map() {
        return new HashMap<K, V>();
    }

    @NotNull
    public static <E> List<E> arrayList() {
        return new ArrayList<E>();
    }

    @NotNull
    public static <V> WeakReference<V> weakRef(@NotNull final V referent) {
        return new WeakReference<V>(referent);
    }
}
