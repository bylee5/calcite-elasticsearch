package org.apache.calcite.adapter.elasticsearch;

import au.com.bytecode.opencsv.CSVReader;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.avatica.util.DateTimeUtils;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.function.Function1;
import org.apache.calcite.linq4j.tree.Primitive;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Table;

import java.io.*;
import java.sql.Types;
import java.text.ParseException;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by bylee on 12/8/15.
*/
class ElasticsearchEnumerator implements Enumerator<Object> {
    private final DataContext esdc;
    private final Table table;
    private DataSet ds;
    private Row current;
    private int[] fields;

    public ElasticsearchEnumerator(DataContext esdc, Table table, int[] fields) {
        this.esdc = esdc;
        this.table = table;
        this.ds = esdc.query().from(table).select(table.getColumns()).execute();
        this.fields = fields;
    }

    public Object[] current() {
        Object[] objects = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            objects[i] = current.getValue(i);
        }
        return objects;
    }

    public boolean moveNext() {
        try {
            if (ds.next()) {
                current = ds.getRow();
                return true;
            } else {
                ds = null;
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
        if (ds instanceof DataSet) {
            ds.close();
        }
    }

    static int[] identityList(int n) {
        int[] integers = new int[n];
        for (int i = 0; i < n; i++) {
            integers[i] = i;
        }
        return integers;
    }
}
