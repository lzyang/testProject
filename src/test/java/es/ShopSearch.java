package es;

import com.mongodb.BasicDBObject;
import com.sysnote.utils.StringUtil;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filters.Filters;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import javax.management.Query;
import java.util.List;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-5.
 */
public class ShopSearch {

    @Test
    public void searchTest(){
        SearchRequestBuilder srb = getSearchBuilder();

        srb.setQuery(testBaseQuery());
        srb.addAggregation(testAgg());
        //srb.addSort(testSort());
        SearchResponse response = srb.get();
        srb.setExplain(true);
        System.out.println(srb);
        System.out.println("=========================================");
//        System.out.println(response);

        handleResult(response);
    }

    public String parseQuery(String question){
        StringBuffer query = new StringBuffer();
        if(StringUtil.isEmpty(question)){
            query.append("*:*");
        }else{
            query.append("title:(");
            List<Term> terms = IndexAnalysis.parse(question);
            int i = 0;
            for(Term term:terms){
                query.append(term.getName());
                i++;
                if(i<terms.size())query.append(" OR ");
            }
            query.append(" )");
        }
        return query.toString();
    }

    public AggregationBuilder testAgg(){
        return AggregationBuilders.terms("agg").field("brands");
    }

    public SortBuilder testSort(){
        return SortBuilders.fieldSort("servscore").order(SortOrder.DESC);
    }

    public QueryBuilder testBaseQuery(){

        QueryBuilder query = QueryBuilders.matchAllQuery();
        BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().cache(true);
        boolFilter.must(FilterBuilders.termFilter("type", "3"));

        String question = "";

        query = QueryBuilders.filteredQuery(QueryBuilders.queryString(parseQuery(question)), boolFilter);
//        query = QueryBuilders.simpleQueryString(question);
//        query = QueryBuilders.termsQuery("type","1","3");
        BoolQueryBuilder bool =  QueryBuilders.boolQuery();
        bool.must(QueryBuilders.termsQuery("type", "1", "3"));
        bool.must(QueryBuilders.simpleQueryString("title:品胜"));
        query = bool;

        //query = QueryBuilders.functionScoreQuery(FilterBuilders.termFilter("brands","pinshegn"), ScoreFunctionBuilders.f)

        return query;
    }

    public QueryBuilder testFunctionScoreQuery(){
        QueryBuilder query = QueryBuilders.matchAllQuery();

        query = QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.scriptFunction(""));
        System.out.println(query);
        return query;
    }

    public void handleResult(SearchResponse response){
        SearchHits hits = response.getHits();
        for(SearchHit hit : hits){
            System.out.println(new BasicDBObject(hit.getSource()));
        }
        /////////////////////agg
        Terms agg = response.getAggregations().get("agg");

        List<Terms.Bucket> items = agg.getBuckets();
        for(Terms.Bucket item:items){
            System.out.print(item.getKey());
            System.out.print("[" + item.getDocCount() + "]  ");
        }
    }

    public void handleAgg(SearchResponse response){

    }


    public SearchRequestBuilder getSearchBuilder(){
        Client client = ESClientUtils.getTranClient("10.144.33.211",9300,"lzyESTest");
        SearchRequestBuilder srb = client.prepareSearch()
                .setPreference("_local")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setIndices("shop")
                .setTypes("shopType")
                .setFrom(0).setSize(10);
        return srb;
    }
}
