package es;

import com.mongodb.*;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by root on 17-2-16.
 */
public class ProdIndex {

    private DBCollection getClient() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient("10.58.69.41", 27017);
        return mongoClient.getDB("product_info").getCollection("simple_json");
    }

    private XContentBuilder parseDoc(BasicDBObject item) throws IOException {

        String id = item.getString("id", "");
        String name = item.getString("name", "");
        String catName = "";
        double price = 0f;
        BasicDBObject catInfo = (BasicDBObject) item.get("catInfo");
        if (catInfo != null) {
            catName = catInfo.getString("thrCatName", "");
        }
        BasicDBObject sku = null;
        BasicDBList skus = (BasicDBList) item.get("skus");
        if (skus != null && skus.size() > 0) {
            sku = (BasicDBObject) skus.get(0);
            price = sku.getDouble("listPrice", 0f);
        }

        if (sku == null) return null;

        XContentBuilder doc = XContentFactory.jsonBuilder()
                .startObject()
                .field("id", id)
                .field("title", name)
                .field("cname", catName)
                .field("price", price);

        BasicDBList facets = (BasicDBList) sku.get("facets");
        if (facets != null && facets.size() > 0) {
            for (int i = 0; i < facets.size(); i++) {
                BasicDBObject facet = (BasicDBObject) facets.get(i);
                BasicDBObject facetsVal = (BasicDBObject) facet.get("values");
                String facetValStr = (String) facetsVal.entrySet().iterator().next().getValue();
                doc.field("f." + facet.getString("id", ""), facetValStr);
            }
        }

        doc.endObject();
        System.out.println(doc.string());
        return doc;
    }

    @Test
    public void putIndex() throws Exception {
        Client client = ESClientUtils.getTranClient("10.58.69.110", 9900, "datagate");

        DBCursor cursor = getClient().find().limit(10);
        BulkRequestBuilder bulkReq = client.prepareBulk();

        //client.prepareIndex("prod_v1", "product");
        while (cursor.hasNext()) {
            BasicDBObject line = (BasicDBObject) cursor.next();
            IndexRequestBuilder requestBuilder = client.prepareIndex("prod_v1", "product", line.getString("id", ""));
            XContentBuilder doc = null;
            if ((doc = parseDoc(line)) != null) {
                requestBuilder.setSource(doc);
            } else continue;
            bulkReq.add(requestBuilder);
        }

        bulkReq.execute().actionGet();
    }
}
