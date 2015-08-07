package es;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.replication.ReplicationType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.script.ScriptService;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by Morningsun(515190653@qq.com) on 15-8-5.
 */
public class ShopIndex {
    private String ip = "10.144.33.211";
    private int port = 9300;
    private String clusterName = "lzyESTest";

    private String indexName = "shop_v1";
    private String indexType = "shopType";

    private Client client = ESClientUtils.getTranClient(ip,port,clusterName);

    private BasicDBList getData(){
        BasicDBList data = new BasicDBList();
        data.add(new BasicDBObject().append("id","80000001").append("name","飞利浦车品官方旗舰店").append("type","1").append("brands","飞利浦")
        .append("logo","/logo/flp.png").append("addr","天津市天津之眼").append("score",23).append("delvspeed",6).append("servscore",2).append("prodcount",20));

        data.add(new BasicDBObject().append("id","80000002").append("name","普朗克旗舰店").append("type","2").append("brands","普朗克")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",12).append("delvspeed",2).append("servscore",6).append("prodcount",120));

        data.add(new BasicDBObject().append("id","80000004").append("name","康帕斯专营店").append("type","3").append("brands","卡康帕斯")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",54).append("delvspeed",5).append("servscore",3).append("prodcount",10));

        data.add(new BasicDBObject().append("id","80000005").append("name","奥卡索专营店").append("type","3").append("brands","奥卡索")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",27).append("delvspeed",8).append("servscore",7).append("prodcount",30));

        data.add(new BasicDBObject().append("id","80000006").append("name","小米官方旗舰店").append("type","1").append("brands","小米")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",95).append("delvspeed",2).append("servscore",9).append("prodcount",5));

        data.add(new BasicDBObject().append("id","80000007").append("name","品胜数码旗舰店").append("type","2").append("brands","品胜")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",54).append("delvspeed",7).append("servscore",10).append("prodcount",32));

        data.add(new BasicDBObject().append("id","80000008").append("name","四季美蒙精品店").append("type","4").append("brands","sjm")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",78).append("delvspeed",8).append("servscore",2).append("prodcount",546));

        data.add(new BasicDBObject().append("id","80000009").append("name","爱华仕旗舰店").append("type","2").append("brands","爱华仕")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",07).append("delvspeed",3).append("servscore",4).append("prodcount",56));

        data.add(new BasicDBObject().append("id","80000010").append("name","百通手机配件批发").append("type","4").append("brands","品胜")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",45).append("delvspeed",7).append("servscore",6).append("prodcount",300));

        data.add(new BasicDBObject().append("id","80000011").append("name","品胜三人行专卖店").append("type","3").append("brands","品胜")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",34).append("delvspeed",9).append("servscore",5).append("prodcount",56));

        data.add(new BasicDBObject().append("id","80000012").append("name","昆山安卓数码专营店").append("type","3").append("brands","三星")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",88).append("delvspeed",5).append("servscore",2).append("prodcount",234));

        data.add(new BasicDBObject().append("id","80000013").append("name","未来时空数码专营店").append("type","3").append("brands","可口可乐")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",54).append("delvspeed",3).append("servscore",4).append("prodcount",655));

        data.add(new BasicDBObject().append("id","80000014").append("name","索尼官方旗舰店").append("type","1").append("brands","索尼")
                .append("logo","/logo/flp.png").append("addr","广东省深圳市").append("score",77).append("delvspeed",1).append("servscore",1).append("prodcount",14));
        return data;
    }

    private String segToString(List<Term> segs){
        StringBuffer str = new StringBuffer();
        int i = 0;
        for(Term term:segs){
            i++;
            if(term.getName().trim().length()==0)continue;
            str.append(term.getName().trim());
            if(i<segs.size()){
                str.append(" ");
            }
        }
        return str.toString();
    }

    private List<BasicDBObject> dealDocs(){
        List<BasicDBObject> result = new LinkedList<BasicDBObject>();
        BasicDBList data = getData();
        for(int i=0;i<data.size();i++){
            BasicDBObject item = (BasicDBObject)data.get(i);
            StringBuilder btitle = new StringBuilder();
            btitle.append(item.get("name")).append(" ");
            btitle.append(item.get("brands"));
            List<Term> titleTerms = IndexAnalysis.parse(btitle.toString());
            item.append("title",segToString(titleTerms));
//            List<Term> titleTerms = BaseAnalysis.parse(btitle.toString());
//            List<Term> titleTerms = ToAnalysis.parse(btitle.toString());
//            System.out.println(segToString(titleTerms));
            result.add(item);
        }
        return result;
    }

    public void addDocs(List<BasicDBObject> docs){

        BulkRequestBuilder bulk = client.prepareBulk();
        for(BasicDBObject docData:docs){
            XContentBuilder xcb = null;
            try {
                xcb = XContentFactory.jsonBuilder().prettyPrint().startObject();
                for(Map.Entry<String,Object> entry:docData.entrySet()){
                    xcb.field(entry.getKey(),entry.getValue());
                }
                xcb.endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(xcb!=null){
                bulk.add(client.prepareIndex("shop_v1","shopType",docData.getString("id")).setSource(xcb));
            }
        }
        //TODO find out
        bulk.setReplicationType(ReplicationType.SYNC)
                .setConsistencyLevel(WriteConsistencyLevel.ALL)
                .execute().actionGet();
//        bulk.execute(new ActionListener<BulkResponse>() {
//            @Override
//            public void onResponse(BulkResponse bulkItemResponses) {
//                System.out.println(bulkItemResponses.getItems().toString());
//            }
//
//            @Override
//            public void onFailure(Throwable e) {
//                e.printStackTrace();
//            }
//        });
    }

    public void updateFieldById(String id,String field,Object value){
        StringBuffer script = new StringBuffer("ctx._source.").append(field).append("=");
        if(value instanceof String){
            script.append("'").append(value).append("'");
        }else{
            script.append(value);
        }
        client.prepareUpdate(indexName,indexType,id).setScript(script.toString(), ScriptService.ScriptType.INLINE)
                .setRetryOnConflict(3).setReplicationType(ReplicationType.SYNC)
                .setConsistencyLevel(WriteConsistencyLevel.ALL).execute();
    }

    @Test
    public void test(){
        //addDocs(dealDocs());
        updateFieldById("80000001","prodcount",15);
    }
}
