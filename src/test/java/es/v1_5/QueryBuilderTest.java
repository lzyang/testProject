//package es;
//
//import groovy.sql.Sql;
//import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
//import org.elasticsearch.common.lucene.search.function.ScoreFunction;
//import org.elasticsearch.index.query.BoolFilterBuilder;
//import org.elasticsearch.index.query.FilterBuilders;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
//import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
//import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
//import org.junit.Test;
//
///**
// * Created by Morningsun(515190653@qq.com) on 15-8-8.
// */
//public class QueryBuilderTest {
//
//    @Test
//    public void functionScoreQuery(){
//        QueryBuilder query = QueryBuilders.matchAllQuery();
//
//        query = QueryBuilders.functionScoreQuery(QueryBuilders.multiMatchQuery("mul","title","brands"),
//                ScoreFunctionBuilders.fieldValueFactorFunction("servscore").modifier(FieldValueFactorFunction.Modifier.LOG1P).factor(0.1f))
//        .boostMode("sum")
//        .maxBoost(1.5f);
//
//
//        query = QueryBuilders.functionScoreQuery(FilterBuilders.termFilter("city","name"), ScoreFunctionBuilders.weightFactorFunction(0.5f));
//
//        System.out.println(query);
//    }
//
//    @Test
//    public void filteredQuery(){
//        QueryBuilder query = QueryBuilders.matchAllQuery();
//        BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().cache(true);
//        boolFilter.must(FilterBuilders.termFilter("type", "3"));
//
//        String sql = "";
//
//        query = QueryBuilders.filteredQuery(QueryBuilders.queryString(sql), boolFilter);
//
//        System.out.println(query);
//    }
//
//}
