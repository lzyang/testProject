package es;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import org.junit.Test;

/**
 * Created by root on 17-2-18.
 */
public class ProdQueryTest {
    private String indexName = "prod_v2";
    private String indexType = "product";

    private String ip = "10.69.2.203";
    private int transPort = 9300;
    private String clusterName = "es151-src-lzy";

    private int from = 0;
    private int size = 10;


    private  SearchRequestBuilder queryBuilder(QueryBuilder qb){
        Client client = ESClientUtils.getTranClient(ip, transPort, clusterName);
        SearchRequestBuilder srb = client.prepareSearch(indexName).setTypes(indexType).setQuery(qb).setFrom(from).setSize(size);
        srb.setSearchType(SearchType.QUERY_THEN_FETCH);
        srb.setPreference("_local");
        srb.setExplain(true);
        System.out.println(">>>>>>>>>start query>>>>>>>>>>");
        System.out.println(srb);
        System.out.println(">>>>>>>>end query>>>>>>>>>>>");
        return srb;
    }

    private void searchAndPrint(SearchRequestBuilder srb){
        SearchResponse resp = srb.execute().actionGet();
        SearchHits hits = resp.getHits();
        BasicDBList resultList = new BasicDBList();
        System.out.println("=========start rst==========");
        for (SearchHit hit : hits.hits()) {
            BasicDBObject item = new BasicDBObject(hit.getSource());
            BasicDBObject nItem = new BasicDBObject();
            nItem.append("id",item.getString("id"));
            nItem.append("title",item.getString("title"));
            System.out.println(nItem);
            System.out.println(hit.explanation());
            resultList.add(item);
        }
        System.out.println("=========end rst==========");
    }

    @Test
    public void simpleQuery(){
        QueryBuilder qb = QueryBuilders.queryString("title:(手机 三星)");
        searchAndPrint(queryBuilder(qb));
    }
}
