package org.apache.calcite.adapter.elasticsearch;

import java.util.List;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.type.RelDataType;

public class ElasticsearchTableScan extends TableScan implements ElasticsearchRel {
	  final ElasticsearchTable elasticsearchTable;
	  final RelDataType projectRowType;
	  
	  protected ElasticsearchTableScan(RelOptCluster cluster, RelTraitSet traitSet,
		      RelOptTable table, ElasticsearchTable elasticsearchTable, RelDataType projectRowType) {
		    super(cluster, traitSet, table);
		    this.elasticsearchTable = elasticsearchTable;
		    this.projectRowType = projectRowType;

		    assert elasticsearchTable != null;
		    assert getConvention() == ElasticsearchRel.CONVENTION;
		  }

		  @Override public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
		    assert inputs.isEmpty();
		    return this;
		  }

		  @Override public RelDataType deriveRowType() {
		    return projectRowType != null ? projectRowType : super.deriveRowType();
		  }

		  @Override public RelOptCost computeSelfCost(RelOptPlanner planner) {
		    // scans with a small project list are cheaper
		    final float f = projectRowType == null ? 1f
		        : (float) projectRowType.getFieldCount() / 100f;
		    return super.computeSelfCost(planner).multiplyBy(.1 * f);
		  }

		  @Override public void register(RelOptPlanner planner) {

		  }

		  public void implement(Implementor implementor) {
		    implementor.elasticsearchTable = elasticsearchTable;
		    implementor.table = table;
		  }
}
