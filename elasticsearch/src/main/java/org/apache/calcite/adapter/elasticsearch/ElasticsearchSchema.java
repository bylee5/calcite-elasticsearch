package org.apache.calcite.adapter.elasticsearch;

import java.util.Map;

import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.metamodel.DataContextFactory;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.schema.Schema;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.google.common.collect.ImmutableMap;

public class ElasticsearchSchema extends AbstractSchema {
	UpdateableDataContext esdc;
	
	public ElasticsearchSchema(String host, String index) {
		super();
		try {
			final Client client = new TransportClient()
	                .addTransportAddress(new InetSocketTransportAddress(host, 9300));
			final String indexName = index;
			this.esdc = DataContextFactory.createElasticSearchDataContext(client, indexName);
			
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.calcite.schema.impl.AbstractSchema#getTableMap()
	 */
	@Override
	protected Map<String, Table> getTableMap() {
		// TODO Auto-generated method stub
		final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
		Schema schema = esdc.getDefaultSchema();
		for (org.apache.metamodel.schema.Table table: schema.getTables()) {
			builder.put(table.getName(), new ElasticsearchTable(table, esdc));
		}
		return builder.build();
	}
}