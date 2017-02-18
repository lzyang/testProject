package es;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.kafka.common.metrics.stats.Count;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregator;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.junit.Test;

import java.util.List;

/**
 * Created by root on 17-2-16.
 */
public class ProdSearch {


    @Test
    public void basicSearch(){
        String indexName = "prod_v1";
        String indexType = "product";
        String clusterName = "datagate";
        Client client = ESClientUtils.getTranClient("127.0.0.1", 9900, clusterName);
        TermQueryBuilder qb = QueryBuilders.termQuery("cname", "DIY创意");
        try {

            SearchRequestBuilder srb = client.prepareSearch(indexName).setTypes(indexType).setQuery(qb).setFrom(0).setSize(10)
                    //.addSort("skuId", SortOrder.DESC)
                    //.addAggregation(AggregationBuilders.terms("小米").field("facet"))
                    .addAggregation(AggregationBuilders.terms("terms").field("f.2"))
                    .addAggregation(AggregationBuilders.count("counts").field("f.2"))
                    .addAggregation(AggregationBuilders.range("priceRange").field("price")
                            .addRange(0,100)
                            .addRange(100,500)
                            .addRange(500,10000));


            System.out.println(srb.toString());
            System.out.println("=============================");
            SearchResponse resp = srb.execute().actionGet();
            SearchHits hits = resp.getHits();

            System.out.println("==============rst start===============");
           // System.out.println(resp.toString());
            System.out.println("==============rst end===============");
            List<Aggregation> aggs = resp.getAggregations().asList();

            StringTerms agg1 = (StringTerms)aggs.get(0);
            ValueCount agg2 = (ValueCount)aggs.get(1);
            Range agg3 = (Range)aggs.get(2);

            System.out.println("agg1:"+agg1.getName());
            System.out.println("\tagg1 buket key:"+agg1.getBuckets().get(0).getKey());
            System.out.println("\tagg1 bukey val:"+agg1.getBuckets().get(0).getDocCount());
            System.out.println("agg2:"+agg2.getName());
            System.out.println("\tagg2:"+agg2.toString());
            System.out.println("agg3:"+agg3.getName());
            System.out.println("\tagg3:"+agg3.getBuckets().iterator().next().getDocCount());


            System.out.println(">>>>>" + aggs.size());


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
}
