package org.apache.calcite.adapter.elasticsearch;

import com.google.common.collect.ImmutableMap;
import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.enumerable.EnumerableRelImplementor;
import org.apache.calcite.adapter.enumerable.PhysType;
import org.apache.calcite.adapter.enumerable.PhysTypeImpl;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.prepare.CalcitePrepareImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterImpl;
import org.apache.calcite.runtime.Hook;

import java.util.Map;

/**
 * Created by bylee on 12/4/15.
 */
public class ElasticsearchToEnumerableConverter extends ConverterImpl
        implements EnumerableRel {
    protected ElasticsearchToEnumerableConverter(
            RelOptCluster cluster,
            RelTraitSet traits,
            RelNode input) {
        super(cluster, ConventionTraitDef.INSTANCE, traits, input);
    }

    public Result implement(EnumerableRelImplementor implementor, Prefer pref) {
        final ElasticsearchRel.Implementor elasticsearchImplementor = new ElasticsearchRel.Implementor();
        elasticsearchImplementor.visitChild(0, getInput());
        Map map = ImmutableMap.builder()
                .put("fields", "name")
                .build();
        if (CalcitePrepareImpl.DEBUG) {
            System.out.println("Elasticsearch: " + map);
        }
        Hook.QUERY_PLAN.run(map);

        final PhysType physType =
                PhysTypeImpl.of(
                        implementor.getTypeFactory(),
                        getRowType(),
                        pref.preferCustom());
        final BlockBuilder builder = new BlockBuilder();
        final Expression table =
                builder.append("table",
                elasticsearchImplementor.table.getExpression(
                        ElasticsearchTable.ElasticsearchQueryable.class));
        builder.add(
                Expressions.return_(null, table));
        return implementor.result(physType, builder.toBlock());
    }
}
