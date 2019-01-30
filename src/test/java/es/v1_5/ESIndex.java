//package es;
//
//import com.mongodb.BasicDBList;
//import com.mongodb.BasicDBObject;
//import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse;
//import org.elasticsearch.action.bulk.BulkRequestBuilder;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
//import org.elasticsearch.common.xcontent.XContentBuilder;
//import org.elasticsearch.common.xcontent.XContentFactory;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Random;
//
///**
// * Created by root on 15-1-30.
// */
//public class ESIndex {
//
//    public void indexData(BasicDBList docs){
//        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, "lzyCluster");
//        BulkRequestBuilder bulkReq = client.prepareBulk();
//        try{
//            for(int i=0;i<docs.size();i++){
//                BasicDBObject o = (BasicDBObject)docs.get(i);
//
//                XContentBuilder doc = XContentFactory.jsonBuilder()
//                        .startObject()
//                        .field("datatime", o.getLong("startDate", 0))
//                        .field("id", o.getString("productId", ""))
//                        .field("num",new Random().nextInt(5)+1)
//                        .field("pname", o.getString("title", ""))
//                        .field("price", o.getDouble("price", 0.00))
//                        .field("userid", o.getString("skuNo", "").substring(0, 7))
//                        .endObject();
//
//                bulkReq.add(client.prepareIndex("order_v1", "orderType",o.getString("skuId"))
//                                .setSource(doc)
//                );
//            }
//            bulkReq.execute().actionGet();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void templateView(){
//        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, "lzyCluster");
//        GetIndexTemplatesResponse response = client.admin().indices().prepareGetTemplates().get();
//        List<IndexTemplateMetaData> temps = response.getIndexTemplates();
//        for(IndexTemplateMetaData temp : temps){
//            System.out.println("=========================================");
//            System.out.println("template:"+temp.template());
//            System.out.println("Name:"+temp.getName());
//            System.out.println("Order:"+temp.getOrder());
//            System.out.println("Settings:"+temp.getSettings().getAsMap());
//            System.out.println("Mappings:"+temp.getMappings());
//        }
//    }
//
//    @Test
//    public void templateSet(){
//        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, "lzyCluster");
//        try {
//            client.admin().indices().preparePutTemplate("template_1")
//                    .setTemplate("te*")
//                    .setOrder(0)
//                    .addMapping("type1", XContentFactory.jsonBuilder().startObject().startObject("type1").startObject("properties")
//                            .startObject("field1").field("type", "string").field("store", "yes").endObject()
//                            .startObject("field2").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
//                            .endObject().endObject().endObject())
//                    .get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
