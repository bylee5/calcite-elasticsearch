package org.apache.calcite.adapter.elasticsearch;



import org.apache.calcite.avatica.util.DateTimeUtils;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.function.Function1;
import org.apache.calcite.linq4j.tree.Primitive;
import org.apache.metamodel.schema.Table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by bylee on 12/7/15.
 */
public class ElasticsearchEnumerator1 implements Enumerator<Object> {
    private final Iterator<Table> cursor;
    private final Function1<Table, Object> getter;
    private Object current;

    public ElasticsearchEnumerator1(Iterator<Table> cursor, Function1<Table, Object> getter) {
        this.cursor = cursor;
        this.getter = getter;
    }

    public Object current() {
        return current;
    }

    public boolean moveNext() {
        try {
            if (cursor.hasNext()) {
                Table map = cursor.next();
                current = getter.apply(map);
                return true;
            } else {
                current = null;
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        throw new UnsupportedOperationException();
    }

    public void close() {

    }

    static Function1<Table, Map> mapGetter() {
        return new Function1<Table, Map>() {
            public Map apply(Table a0) {
                return (Map) a0;
            }
        };
    }

    static Function1<Table, Object> singletonGetter(final String fieldName,
                                                       final Class fieldClass) {
        return new Function1<Table, Object>() {
            public Object apply(Table a0) {
                return null;
            }
        };
    }

    /**
     * @param fields List of fields to project; or null to return map
     */
    static Function1<Table, Object[]> listGetter(
            final List<Map.Entry<String, Class>> fields) {
        return new Function1<Table, Object[]>() {
            public Object[] apply(Table a0) {
                Object[] objects = new Object[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    final Map.Entry<String, Class> field = fields.get(i);
                    final String name = field.getKey();
                    //objects[i] = convert(a0.get(name), field.getValue());
                }
                return objects;
            }
        };
    }

    static Function1<Table, Object> getter(
            List<Map.Entry<String, Class>> fields) {
        //noinspection unchecked
        return fields == null
                ? (Function1) mapGetter()
                : fields.size() == 1
                ? singletonGetter(fields.get(0).getKey(), fields.get(0).getValue())
                : listGetter(fields);
    }

    private static Object convert(Object o, Class clazz) {
        if (o == null) {
            return null;
        }
        Primitive primitive = Primitive.of(clazz);
        if (primitive != null) {
            clazz = primitive.boxClass;
        } else {
            primitive = Primitive.ofBox(clazz);
        }
        if (clazz.isInstance(o)) {
            return o;
        }
        if (o instanceof Date && primitive != null) {
            o = ((Date) o).getTime() / DateTimeUtils.MILLIS_PER_DAY;
        }
        if (o instanceof Number && primitive != null) {
            return primitive.number((Number) o);
        }
        return o;
    }
}
