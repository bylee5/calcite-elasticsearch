package org.apache.calcite.adapter.elasticsearch;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.apache.calcite.adapter.enumerable.*;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.Blocks;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.prepare.CalcitePrepareImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.util.Util;

public class ElasticsearchTableScan extends TableScan implements EnumerableRel {
    final ElasticsearchTable elasticsearchTable;
//    final RelDataType projectRowType;
    final int[] fields;


    protected ElasticsearchTableScan(RelOptCluster cluster, RelOptTable table,
                                     ElasticsearchTable elasticsearchTable, int[] fields) {
        super(cluster, cluster.traitSetOf(EnumerableConvention.INSTANCE), table);
        this.elasticsearchTable = elasticsearchTable;
        this.fields = fields;
    }


    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        assert inputs.isEmpty();
        return this;
    }

//    @Override
//    public RelDataType deriveRowType() {
//        return projectRowType != null ? projectRowType : super.deriveRowType();
//    }

    @Override public RelDataType deriveRowType() {
        final List<RelDataTypeField> fieldList = table.getRowType().getFieldList();
        final RelDataTypeFactory.FieldInfoBuilder builder =
                getCluster().getTypeFactory().builder();
        for (int field : fields) {
            builder.add(fieldList.get(field));
        }
        return builder.build();
    }

    @Override
    public void register(RelOptPlanner planner) {
        //Add elasticsearch rule
    }

    public Result implement(EnumerableRelImplementor implementor, Prefer pref) {
        final PhysType physType =
                PhysTypeImpl.of(
                        implementor.getTypeFactory(),
                        getRowType(),
                        pref.preferArray());
        return implementor.result(
                physType,
                Blocks.toBlock(
                        Expressions.call(table.getExpression(ElasticsearchTable.class),
                                "project", Expressions.constant(fields))));
    }
}
