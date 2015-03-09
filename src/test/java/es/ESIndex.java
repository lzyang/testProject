package es;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.util.Random;

/**
 * Created by root on 15-1-30.
 */
public class ESIndex {

    public void indexData(BasicDBList docs){
        Client client = ESClientUtils.getTranClient("127.0.0.1", 9300, "lzyESTest");
        BulkRequestBuilder bulkReq = client.prepareBulk();
        try{
            for(int i=0;i<docs.size();i++){
                BasicDBObject o = (BasicDBObject)docs.get(i);

                XContentBuilder doc = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("datatime", o.getLong("startDate", 0))
                        .field("id", o.getString("productId", ""))
                        .field("num",new Random().nextInt(5)+1)
                        .field("pname", o.getString("title", ""))
                        .field("price", o.getDouble("price", 0.00))
                        .field("userid", o.getString("skuNo", "").substring(0, 7))
                        .endObject();

                bulkReq.add(client.prepareIndex("order_v1", "orderType",o.getString("skuId"))
                                .setSource(doc)
                );
            }
            bulkReq.execute().actionGet();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
