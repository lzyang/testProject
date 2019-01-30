//package es;
//
//import com.mongodb.BasicDBObject;
//import com.sysnote.utils.CommonUtil;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.common.geo.GeoDistance;
//import org.elasticsearch.common.unit.DistanceUnit;
//import org.elasticsearch.index.query.FilterBuilders;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.TermQueryBuilder;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.sort.SortBuilder;
//import org.elasticsearch.search.sort.SortBuilders;
//import org.elasticsearch.search.sort.SortOrder;
//import org.junit.Test;
//
//import java.util.HashMap;
//
///**
// * Created by root on 15-3-5.
// */
//public class GeoSearch {
//
//    @Test
//    public void geoSearchOrder(){
//        Client client = getClient();
//        TermQueryBuilder qb = QueryBuilders.termQuery("tags","手机");
//
//        SortBuilder sort = SortBuilders.geoDistanceSort("location")
//                .point(39, 110.5).unit(DistanceUnit.KILOMETERS);
//        SearchResponse resp = client.prepareSearch("geotest").setTypes("geoType").setQuery(qb).setFrom(0).setSize(100)
//                .addSort(SortBuilders.fieldSort("_score").order(SortOrder.DESC))
//                .addSort(sort)
//                .execute().actionGet();
//        SearchHits hits = resp.getHits();
//
//        for(SearchHit hit:hits.hits()){
//            System.out.println(hit.getScore());
//            BasicDBObject item = new BasicDBObject(hit.getSource());
//            BasicDBObject location = new BasicDBObject((HashMap)item.get("location"));
//            double distance = CommonUtil.getPointDistance(39, 110.5,location.getDouble("lat",0.0),location.getDouble("lon",0.0));
//            System.out.println(distance);
//            System.out.println(item);
//        }
//    }
//
//    @Test
//    public void geoSearchFilter(){
//        Client client = getClient();
//        TermQueryBuilder qb = QueryBuilders.termQuery("tags","手机");
//        SortBuilder sort = SortBuilders.geoDistanceSort("location")
//                .point(39, 110.5);
//        SearchResponse resp = client.prepareSearch("geotest").setTypes("geoType").setQuery(qb).setFrom(0).setSize(100)
//                .addSort(SortBuilders.fieldSort("_score").order(SortOrder.DESC))
//                .addSort(sort)
//                .setPostFilter(FilterBuilders.geoDistanceFilter("location")
//                            .geoDistance(GeoDistance.ARC)
//                            .lat(39).lon(110.5)
//                            .distance("1000km"))
//                .execute().actionGet();
//        SearchHits hits = resp.getHits();
//
//        for(SearchHit hit:hits.hits()){
//            System.out.println(hit.getScore());
//            BasicDBObject item = new BasicDBObject(hit.getSource());
//            BasicDBObject location = new BasicDBObject((HashMap)item.get("location"));
//            double distance = CommonUtil.getPointDistance(39, 110.5,location.getDouble("lat",0.0),location.getDouble("lon",0.0));
//            System.out.println(distance);
//            System.out.println(item);
//        }
//    }
//
//    public Client getClient(){
//        String clusterName = "lzyESTest";
//        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, clusterName);
//        return client;
//    }
//}
