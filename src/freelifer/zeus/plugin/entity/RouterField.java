package freelifer.zeus.plugin.entity;

import java.util.List;

/**
 * @author zhukun on 2017/6/16.
 * @version 1.0
 */
public final class RouterField {

    public String className;
    public List<IntentField> intentFieldList;

    public static RouterField create() {
        return new RouterField();
    }

    public final static class IntentField {
        public String annotationName;
        public String typeName;
        public String value;

        public static IntentField create() {
            return new IntentField();
        }
    }
}
