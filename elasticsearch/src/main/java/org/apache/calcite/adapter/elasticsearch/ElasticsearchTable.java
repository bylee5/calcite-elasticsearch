package org.apache.calcite.adapter.elasticsearch;


import java.util.List;
import java.util.Map;

import org.apache.calcite.adapter.java.AbstractQueryableTable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.AbstractTableQueryable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.metamodel.DataContext;

public class ElasticsearchTable extends AbstractQueryableTable
implements TranslatableTable {
	protected final String schemaName;
	
	 /** Creates a ElasticsearchTable. */
	ElasticsearchTable(String schemaName) {
	    super(Object[].class);
	    this.schemaName = schemaName;
	  }
	
	public String toString() {
	    return "ElasticsearchTable {" + schemaName + "}";
	  }

	public <T> Queryable<T> asQueryable(QueryProvider queryProvider,
		      SchemaPlus schema, String tableName) {
		// TODO Auto-generated method stub
		return new ElasticsearchQueryable<T>(queryProvider, schema, this, tableName);
	}

	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		// TODO Auto-generated method stub
		final RelDataType mapType =
		        typeFactory.createMapType(
		            typeFactory.createSqlType(SqlTypeName.VARCHAR),
		            typeFactory.createTypeWithNullability(
		                typeFactory.createSqlType(SqlTypeName.ANY), true));
		    return typeFactory.builder().add("_MAP", mapType).build();//Adds a field with given name and type.
	}

	public RelNode toRel(
		      RelOptTable.ToRelContext context,
		      RelOptTable relOptTable) {
		// TODO Auto-generated method stub
		final RelOptCluster cluster = context.getCluster();
		return new ElasticsearchTableScan(cluster, cluster.traitSetOf(ElasticsearchRel.CONVENTION),
		        relOptTable, this, null);
	}
	
	  public Enumerable<Object> find(DataContext dc, String filterJson,
		      String projectJson, List<Map.Entry<String, Class>> fields) {
		  //구현 
		    return null;
		  }

	public static class ElasticsearchQueryable<T> extends AbstractTableQueryable<T> {
	    public ElasticsearchQueryable(QueryProvider queryProvider, SchemaPlus schema,
	    		ElasticsearchTable table, String tableName) {
	      super(queryProvider, schema, table, tableName);
	    }

	    public Enumerator<T> enumerator() {
	      //noinspection unchecked
	      final Enumerable<T> enumerable =
	          (Enumerable<T>) getTable().find(getElasticsearch(), null, null, null);
	      return enumerable.enumerator();
	    }

	    private DataContext getElasticsearch() {
	      return schema.unwrap(ElasticsearchSchema.class).dc;
	    }

	    private ElasticsearchTable getTable() {
	      return (ElasticsearchTable) table;
	    }
	    /** Called via code-generation.
	     *
	     * @see org.apache.calcite.adapter.mongodb.MongoMethod#MONGO_QUERYABLE_FIND
	     */
	    @SuppressWarnings("UnusedDeclaration")
	    public Enumerable<Object> find(String filterJson,
	        String projectJson, List<Map.Entry<String, Class>> fields) {
	      return getTable().find(getElasticsearch(), filterJson, projectJson, fields);
	    }
	  }
}
