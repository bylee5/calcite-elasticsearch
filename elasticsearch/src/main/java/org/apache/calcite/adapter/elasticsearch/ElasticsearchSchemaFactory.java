package org.apache.calcite.adapter.elasticsearch;

import java.util.Map;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

@SuppressWarnings("UnusedDeclaration")
public class ElasticsearchSchemaFactory implements SchemaFactory{

	public ElasticsearchSchemaFactory(){
	}

	public Schema create(SchemaPlus parentSchema, String name,
			Map<String, Object> operand){
		Map map = (Map) operand;
		String host = (String) map.get("host");
		String index = (String) map.get("index");
		return new ElasticsearchSchema(host, index);
	}
}
