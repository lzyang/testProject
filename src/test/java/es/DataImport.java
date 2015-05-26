package es;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by root on 15-5-26.
 */
public class DataImport {

    private Client getSourceClient(){
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff",false)
                .put("cluster.name","clusterName_source").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("10.255.210.23",9300));
        return client;
    }
    private Client getTargetClient(){
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", false)
                .put("cluster.name", "clusterName_target").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("10.255.210.24", 9300));
        return client;
    }

    @Test
    public void importData(){
        Client sourceClient = getSourceClient();
        Client targetClient = getTargetClient();
        SearchResponse scrollResp = sourceClient.prepareSearch("sourceIndex")
                .setTypes("sourceType")
                .setQuery(QueryBuilders.matchAllQuery())
                .setScroll(new TimeValue(20000))
                .setSize(2500).execute().actionGet();
        System.out.print(scrollResp.getHits().getTotalHits());
        System.out.print(scrollResp.getHits().hits().length);

        int sum = 0;
        while (true){
            scrollResp = sourceClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(20000)).execute().actionGet();
            Long total = scrollResp.getHits().getTotalHits();
            int count = scrollResp.getHits().getHits().length;
            sum += count;
            System.out.print(sum+"/"+total);
            System.out.print(scrollResp.getHits().hits().length);

            if(count==0)break;
            BulkRequestBuilder bulkReq = targetClient.prepareBulk();
            if(count!=0){
                Iterator<SearchHit> hits = scrollResp.getHits().iterator();
                while (hits.hasNext()){
                    SearchHit hit = (SearchHit)hits.next();
                    Map<String,Object> doc = hit.getSource();
                    bulkReq.add(targetClient.prepareIndex("targetIndexName", "targetIndexType").setId(doc.get("id").toString()).setSource(doc));
                }
            }
            BulkResponse resp = bulkReq.get();
            if(resp.hasFailures()){
                System.out.print("error");
            }
        }
    }
}
