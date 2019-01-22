package es;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.sysnote.db.MongoTools;
import com.sysnote.utils.StringUtil;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.apache.lucene.search.Explanation;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filters.Filters;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import javax.management.Query;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-5.
 */
public class ShopSearch {

    @Test
    public void searchTest(){
        SearchRequestBuilder srb = getSearchBuilder();

        srb.setQuery(testBaseQuery());
        srb.addAggregation(testAgg());
        //srb.addSort(testSort());
        srb.setExplain(true);
        SearchResponse response = srb.get();
        System.out.println(srb);
        System.out.println("========================================="+response.getHits().getTotalHits());
//        System.out.println(response);

        handleResult(response);
    }

    public String parseQuery(String question){
        StringBuffer query = new StringBuffer();
        if(StringUtil.isEmpty(question)){
            query.append("*:*");
        }else{
            query.append("title:(");
            List<Term> terms = IndexAnalysis.parse(question).getTerms();
            int i = 0;
            for(Term term:terms){
                query.append(term.getName());
                i++;
                if(i<terms.size())query.append(" OR ");
            }
            query.append(")");
        }
        return query.toString();
    }

    public AggregationBuilder testAgg(){
        return AggregationBuilders.terms("agg").field("brands").size(0);
    }

    public SortBuilder testSort(){
        return SortBuilders.fieldSort("servscore").order(SortOrder.DESC);
    }

    public QueryBuilder testBaseQuery(){

        QueryBuilder query = QueryBuilders.matchAllQuery();
        BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().cache(true);
        boolFilter.must(FilterBuilders.termFilter("type", "3"));

        String question = "史宁博";

//        query = QueryBuilders.filteredQuery(QueryBuilders.queryString(parseQuery(question)), boolFilter);
//        query = QueryBuilders.simpleQueryString(parseQuery(question));
//        query = QueryBuilders.termsQuery("type","1","3");
        BoolQueryBuilder bool =  QueryBuilders.boolQuery();
//        bool.must(QueryBuilders.termsQuery("type", "1", "3"));
        bool.must(QueryBuilders.queryString(parseQuery(question)).boost(100));
//        query = bool;
        String queryStr = "("+parseQuery(question)+")^20";  //query设置boost一样
        //query = QueryBuilders.functionScoreQuery(QueryBuilders.filteredQuery(QueryBuilders.queryString(parseQuery(question)),boolFilter).boost(20))
        query = QueryBuilders.functionScoreQuery(QueryBuilders.filteredQuery(QueryBuilders.queryString(queryStr),boolFilter))
                .add(ScoreFunctionBuilders.scriptFunction("doc['score'].value"))
                .boostMode("sum");

        return query;
    }

    public QueryBuilder testFunctionScoreQuery(){
        QueryBuilder query = QueryBuilders.matchAllQuery();

        query = QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.scriptFunction("_score"));
        System.out.println(query);
        return query;
    }

    public void handleResult(SearchResponse response){
        SearchHits hits = response.getHits();
        int i = 0;
        for(SearchHit hit : hits){
            System.out.println("No."+(++i) + " >>>" + new BasicDBObject(hit.getSource()));
            System.out.println(hit.getExplanation());

        }
        /////////////////////agg
        Terms agg = response.getAggregations().get("agg");

        List<Terms.Bucket> items = agg.getBuckets();
        for(Terms.Bucket item:items){
            System.out.print(item.getKey());
            System.out.print("[" + item.getDocCount() + "]  ");
        }
    }

    public void handleAgg(SearchResponse response){

    }


    public SearchRequestBuilder getSearchBuilder(){
        Client client = ESClientUtils.getTranClient("10.58.56.183",9300,"t3-uat");
        SearchRequestBuilder srb = client.prepareSearch()
                .setPreference("_local")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setIndices("shopnew")
                .setTypes("shopType")
                .setFrom(0).setSize(5);
        return srb;
    }

    @Test
    public void testSearch(){
        SearchRequestBuilder  searchBuilder = getSearchBuilder();
        QueryBuilder query = null;
        String weightFunction = "2*sqrt(2*pow(doc['match'].value,2) + pow(doc['speed'].value,2) + pow(doc['serv'].value,2) + pow(doc['score_type'].value,2)) + doc['score_type'].value*5";
        query = QueryBuilders.functionScoreQuery(QueryBuilders.filteredQuery(QueryBuilders.queryStringQuery("title:(+爱空间)^1.5 corewords:(+爱空间)").boost(1),null))
                .add(ScoreFunctionBuilders.scriptFunction(weightFunction))
                .boostMode("sum");
        searchBuilder.setQuery(query);
        searchBuilder.setExplain(true);
        System.out.println(searchBuilder);
        System.out.println("========================");
        SearchResponse response = searchBuilder.get();
//        System.out.println(response);
        long count = response.getHits().getTotalHits();
        BasicDBList list = new BasicDBList();
        System.out.println("searchCount:"+count);
        if(count!=0){
            SearchHits hits = response.getHits();
            for(SearchHit hit:hits){
                BasicDBObject item = new BasicDBObject(hit.sourceAsMap());
                DecimalFormat df = new DecimalFormat(".#");
                double level = Double.parseDouble(item.getString("level", "4.5"));
                item.append("level", df.format(level));
                double speed = Double.parseDouble(item.getString("speed", "4.5"));
                item.append("speed", df.format(speed));
                double match = Double.parseDouble(item.getString("match", "4.5"));
                item.append("match", df.format(match));
                double serv = Double.parseDouble(item.getString("serv", "4.5"));
                item.append("serv", df.format(serv));
                list.add(item);
//                System.out.println(item);
                System.out.println(hit.getScore() + "__" + item.getString("name") + "__" + item.get("id") + "__" + level + "\r\n\t" + item.getString("title") + "=====" + item.getString("corewords"));
//                System.out.println(hit.getExplanation());
                System.out.println("====================================");
            }
        }

    }

    public int handleResp(int totalCount,SearchResponse srp,Client client,DBCollection coll){
        int count = srp.getHits().getHits().length;
        if(count != 0) {
            BulkRequestBuilder bulkReq = client.prepareBulk();

            Iterator<SearchHit> hits = srp.getHits().iterator();
            while (hits.hasNext()){
                SearchHit hit = hits.next();
                Map<String,Object> doc = hit.getSource();
                String shopId = doc.get("id").toString();
                String corewords = dealShop(coll,shopId);
                doc.put("corewords",corewords);
                String name = doc.get("name").toString();
                String type = doc.get("type").toString();

                if("旗舰店".equals(type)&&name.contains("官方旗舰店")){
                    doc.put("score_type",3);
                }else if("旗舰店".equals(type)){
                    doc.put("score_type",2);
                }else{
                    doc.put("score_type",1);
                }

                doc.put("set_level",0);

                doc.put("serv",Double.parseDouble(doc.get("serv").toString()));
                doc.put("level", Double.parseDouble(doc.get("level").toString()));
                doc.put("speed",Double.parseDouble(doc.get("speed").toString()));
                doc.put("match", Double.parseDouble(doc.get("match").toString()));


                List<Term> parse = IndexAnalysis.parse(name).getTerms();
                String indexName = "";
                for(int i = 0;i<parse.size();i++){
                    Term t = parse.get(i);
                    if(indexName.length()>0) indexName = indexName + " ";
                    indexName = indexName + t.getName();
                }
                doc.put("title",indexName);
                bulkReq.add(client.prepareIndex("shop_index", "shopType").setId(shopId).setSource(doc));
                ++totalCount;
                if(corewords.length()==0){
                    System.out.println("no coreWords!\t" + hit.getSourceAsString());
                }
            }

            BulkResponse bresp = bulkReq.get();
            if(bresp.hasFailures()){
                System.out.println("bulk hasError!!");
            }
        }
        return count;
    }

    public String dealShop(DBCollection coll,String shopId){
        BasicDBObject cursor = (BasicDBObject)coll.findOne(new BasicDBObject("shopId", shopId));
        if(cursor==null){
            System.out.println("null shopId:"+shopId);
            return "";
        }
        BasicDBList words = (BasicDBList)cursor.get("words");
        BasicDBObject brandfacets = (BasicDBObject)cursor.get("brandfacets");
        StringBuffer sb = new StringBuffer();

        Set<String> brandSet = new HashSet<String>();

        if(brandfacets!=null){
            for(String key:brandfacets.keySet()){
                handleBrandStr(brandSet, brandfacets.get(key).toString());
            }
        }

        for(String brand : brandSet){
            if(sb.length()>0) sb.append(" ");
            sb.append(brand);
        }

        if(words!=null){
            for(int i=0;i<words.size();i++){
                if(sb.length()>0)sb.append(" ");
                sb.append(words.get(i));
            }
        }

        return sb.toString();
    }

    public void handleBrandStr(Set<String> set,String brand){
        brand = brand.toLowerCase().replace(")","").replace("）","");
        String[] brandItem = brand.split("\\(");
        String[] brandItem1 = brand.split("\\（");
        if(brandItem.length == 2){
            set.add(brandItem[0].trim());
            set.add(brandItem[1].trim());
        }else if(brandItem1.length == 2){
            set.add(brandItem1[0].trim());
            set.add(brandItem1[1].trim());
        }else {
            set.add(brand.trim());
        }
    }

    @Test
    public void testUpdate(){
        Client targetClient = ESClientUtils.getTranClient("10.58.56.183",9300,"t3-uat");
        UpdateRequestBuilder urb = targetClient.prepareUpdate("shopnew", "shopType", "80010735");
        try {
            urb.setDoc(XContentFactory.jsonBuilder().startObject().field("set_level",8).endObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
        urb.execute().actionGet();
    }

    @Test
    public void transNewIndex(){
        int totalCount = 0;
//        Client targetClient = ESClientUtils.getTranClient("10.58.44.46",9900,"pre_t3");
        Client targetClient = ESClientUtils.getTranClient("10.112.173.2",9900,"g3t3");
//        Client targetClient = ESClientUtils.getTranClient("10.58.56.183",9300,"t3-uat");
        Client client = ESClientUtils.getTranClient("10.58.69.31",9900,"product_optimize");
        DBCollection coll = MongoTools.getConn("10.58.50.24", 27027, "product_info", "extshopinfo");
        SearchResponse srp = client.prepareSearch("shop").setTypes("shopType")
                .setQuery(QueryBuilders.matchAllQuery())
                .setScroll(new TimeValue(20000))
                .setSize(100).execute().actionGet();
        totalCount += handleResp(totalCount,srp,client,coll);
        while (true){
            srp = client.prepareSearchScroll(srp.getScrollId()).setScroll(new TimeValue(20000))
                    .execute().actionGet();
            int count = handleResp(totalCount,srp,targetClient,coll);
            totalCount += count;
            if(count == 0) break;
        }
        System.out.println("totalCount=" + totalCount);
    }
}
