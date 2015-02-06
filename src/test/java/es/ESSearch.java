package es;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.nlpcn.commons.lang.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.search.aggregations.AggregationBuilders.*;
import static java.lang.System.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by root on 15-1-30.
 */
public class ESSearch {

    @Test
    public void esBasicSearch() {
        String indexName = "order_v1";
        String indexType = "orderType";
        String clusterName = "lzyESTest1_4";
        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, clusterName);
        TermQueryBuilder qb = QueryBuilders.termQuery("productTag", 1);
        try {
            SearchResponse resp = client.prepareSearch(indexName).setTypes(indexType).setQuery(qb).setFrom(0).setSize(10)
                    //.addSort("skuId", SortOrder.DESC)
                    //.addAggregation(AggregationBuilders.terms("小米").field("facet"))
                    .addAggregation(AggregationBuilders.max("agg").field("salesVolume"))
                    .execute().actionGet();
            SearchHits hits = resp.getHits();

            Max agg = resp.getAggregations().get("agg");
            System.out.println(agg.getValue());

            BasicDBList resultList = new BasicDBList();
            for (SearchHit hit : hits.hits()) {
                BasicDBObject item = new BasicDBObject(hit.getSource());
                System.out.println(item);
                resultList.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void esIndex() {
        String indexName = "order_v1";
        String indexType = "orderType";
        String clusterName = "lzyESTest";
        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, clusterName);
        try {
            XContentBuilder doc = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("datatime", System.currentTimeMillis())
                    .field("id", "100000001")
                    .field("num", "2")
                    .field("pname", "龙迪　加湿器　p4190")
                    .field("price", 255.10)
                    .field("userid", "51226560")
                    .endObject();

            client.prepareIndex(indexName, indexType).setId("4521662").setSource(doc).setRefresh(true).execute().actionGet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void filterTest() {
        String indexName = "product_local";
        String indexType = "productType";
        String clusterName = "lzyESTest";

        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, clusterName);

//        TermsBuilder agg_tb = AggregationBuilders.terms("ps").field("categoryBrand").size(100);
        TermsBuilder agg_tb = AggregationBuilders.terms("ps").field("categoryBrand").size(21).shardSize(6); //当设置为０时默认返回Integer.MAX_VALUE

        SearchRequestBuilder srb = client.prepareSearch().setPreference("_local").setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        srb.setIndices(indexName).setTypes(indexType).addAggregation(agg_tb);
        srb.setFrom(0).setSize(2);

        BoolFilterBuilder andFilterBuilder = FilterBuilders.boolFilter().cache(true);

        //andFilterBuilder.must(FilterBuilders.termFilter("skuNo", "1000400543"));
        andFilterBuilder.must(FilterBuilders.rangeFilter("price").from(500)
                .to(2100).includeLower(false).includeUpper(false));

//        String sql = "title:手机 OR 中兴 OR 联想^5  AND productTag:1";
        String sql = "title:手机 AND productTag:1";
        srb.setQuery(QueryBuilders.filteredQuery(QueryBuilders.queryString(sql), andFilterBuilder));

        System.out.println(srb);

        SearchResponse response = srb.get();

        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        for (SearchHit hit : hits) {
            System.out.println(new BasicDBObject(hit.getSource()));
        }

        //////////////////////获取agg
        Terms pss = response.getAggregations().get("ps");

        List<Terms.Bucket> bs = pss.getBuckets();
        System.out.println("sum_other_doc_count:" + pss.getSumOfOtherDocCounts());
        System.out.println("doc_count_error_upper_bound:" + pss.getDocCountError());
        for (Terms.Bucket b : bs) {
            System.out.print(b.getKey());
            System.out.println("  >>" + b.getDocCount());
        }
    }


    @Test
    public void testNormal() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        System.out.println(sdf.format(new Date(1422460740000l)));
//        System.out.println(sdf.format(new Date(1391097540000l)));
        System.out.println("0:".split(":")[0]);
    }

    @Test
    public void testTimeout(){
        String indexName = "product_local";
        String indexType = "productType";
        String clusterName = "lzyESTest";

        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, clusterName);

        SearchRequestBuilder  builder= client.prepareSearch("product_local")
                .setTimeout("10ms")
//                .setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.scriptFilter("Thread.sleep(5000); return true;")));
                .setQuery(QueryBuilders.matchAllQuery());
        System.out.println(builder.toString());
        SearchResponse searchResponse = builder.execute().actionGet();

        System.out.println(searchResponse.getTookInMillis());
        System.out.println(searchResponse.isTimedOut());
    }


}
