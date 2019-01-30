//package es;
//
//import com.mongodb.BasicDBList;
//import com.mongodb.BasicDBObject;
//import com.mongodb.util.JSON;
//import com.sysnote.utils.MFileUtil;
//import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.bulk.BulkRequestBuilder;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.cluster.metadata.IndexMetaData;
//import org.elasticsearch.common.xcontent.XContentBuilder;
//import org.elasticsearch.common.xcontent.XContentFactory;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.Random;
//
///**
// * Created by root on 15-3-5.
// */
//public class GeoIndex {
//
//    @Test
//    public void geoIndex(){
//        String content = MFileUtil.readTxt("/server/dev/data/pin.txt",MFileUtil.TXT_COMMON_DELSTR,MFileUtil.CHARSET_GBK);
//        try {
//            content = new String(content.getBytes("UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        String clusterName = "lzyESTest";
//        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, clusterName);
//
//        BulkRequestBuilder bulkReq = client.prepareBulk();
//
//        BasicDBList source_docs = (BasicDBList)JSON.parse(content);
//        for(int i=0;i<source_docs.size();i++){
//            BasicDBObject source_doc = (BasicDBObject)source_docs.get(i);
//
//            try {
//                XContentBuilder doc = XContentFactory.jsonBuilder()
//                        .startObject()
//                            .field("location", new BasicDBObject().append("lat", source_doc.getDouble("latitude", 0.0)).append("lon", source_doc.getDouble("longitude", 0.0)))
//                            .field("tags", source_doc.getString("tags", ""))
//                            .field("name", source_doc.getString("address", ""))
//                            .field("num", new Random().nextInt(100) + 100)
//                            .field("tm", System.currentTimeMillis())
//                        .endObject();
//                bulkReq.add(client.prepareIndex("geotest","geoType","_id"+System.currentTimeMillis()).setSource(doc));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            bulkReq.execute().actionGet();
//        }
//    }
//
//    @Test
//    public void geoMapping(){
//        String clusterName = "lzyESTest";
//
//        ImmutableSettings.Builder builder = ImmutableSettings.builder();
//        builder.put(IndexMetaData.SETTING_NUMBER_OF_SHARDS,2);
//        builder.put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS,1);
//
//        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, clusterName);
//        CreateIndexRequestBuilder cReqBuilder = client.admin().indices().prepareCreate("geotest").setSettings(builder.build());
//
//        try {
//            cReqBuilder.addMapping("geoType", XContentFactory.jsonBuilder()
//                            .startObject()
//                                .startObject("geoType")
//                                    .startObject("properties")
//                                        .startObject("location")
//                                            .field("type", "geo_point")
//                                        .endObject()
//                                        .startObject("name")
//                                            .field("type","string").field("store", true)
//                                        .startObject("tags")
//                                            .field("type","string").field("store",true).field("analyzer", "whitespace")
//                                        .startObject("num")
//                                            .field("type", "integer").field("store",true)
//                                        .startObject("tm")
//                                        .field("type", "long").field("store",true)
//                                        .endObject()
//                                    .endObject()
//                                .endObject()
//                            .endObject()
//            ).get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
