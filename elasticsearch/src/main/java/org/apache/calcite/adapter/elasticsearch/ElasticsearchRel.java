package org.apache.calcite.adapter.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.util.Pair;

public interface ElasticsearchRel extends RelNode {
	void implement(Implementor implementor);
	
	Convention CONVENTION = new Convention.Impl("Elasticsearch", ElasticsearchRel.class);
	
	class Implementor {
	    final List<Pair<String, String>> list =
	        new ArrayList<Pair<String, String>>();

	    RelOptTable table;
	    ElasticsearchTable elasticsearchTable;

	    public void add(String findOp, String aggOp) {
	      list.add(Pair.of(findOp, aggOp));
	    }

	    public void visitChild(int ordinal, RelNode input) {
	      assert ordinal == 0;
	      ((ElasticsearchRel) input).implement(this);
	    }
	}
}
