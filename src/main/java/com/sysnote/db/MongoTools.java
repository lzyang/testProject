package com.sysnote.db;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.sysnote.utils.StringUtil;

/**
 * Created by root on 15-2-6.
 */
public class MongoTools {

    //mongo链接集和
    private static ConcurrentHashMap<String, DBCollection> mongoConns = new ConcurrentHashMap<String, DBCollection>();
    private static Builder builder = null;
    protected static MongoClientOptions mongoClientOptions = null;
    private static Hashtable<String, DB> mongoDBs = new Hashtable<String, DB>();
    private static Hashtable<String, MongoClient> mongoClients = new Hashtable<String, MongoClient>();


    static {
        builder = MongoClientOptions.builder();
        builder.autoConnectRetry(true);
        builder.connectionsPerHost(100);
        builder.threadsAllowedToBlockForConnectionMultiplier(50);
        builder.maxWaitTime(10000);
        builder.connectTimeout(3000);
        mongoClientOptions = builder.build();
    }

    /**
     * 通过ip获取mongo信息
     */
    public static DBCollection getCollectionByFullParam(String serverList,String dbName,String uname,String upass,String collName) {
        if(StringUtil.isEmpty(serverList)||StringUtil.isEmpty(dbName)||StringUtil.isEmpty(uname)||StringUtil.isEmpty(upass)){
            return null;
        }
        DB db = getMongoDBByIp(serverList,dbName,uname,upass);
        if (db == null) {
            return null;
        }
        return db.getCollection(collName);
    }

    /**
     * 通过各参数获取mongoDB
     * @param dbName
     * @param uname
     * @param upass
     * @return
     */
    public static synchronized DB getMongoDBByIp(String serverList, String dbName,String uname,String upass) {
        String key = "db_" + uname + "@" + dbName;
        DB db = mongoDBs.get(key);
        if (db != null && db.getStats().ok()) {
            return db;
        }
        MongoClient mongoClient = getClientNamePass(serverList,uname,upass,dbName);
        if (mongoClient == null) {
            return null;
        }
        db = mongoClient.getDB(dbName);
        if (db != null) {
            mongoDBs.put(key, db);
        }
        return db;
    }

    /**
     * 通过各参数获取mongoClient
     * @param uname
     * @param upass
     * @return
     */
    public static synchronized MongoClient getClientNamePass(String serverList,String uname,String upass,String dbName) {
        MongoClient mongoClient = mongoClients.get("client_"+uname+"@"+dbName);
        if (mongoClient != null) {
            return mongoClient;
        }
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();

        String [] servers = serverList.split("\\|");
        String ip = "";
        int port = -1;
        try {
            for(String server:servers){
                String [] item = server.split(":");
                ip = item[0];
                port = Integer.parseInt(item[1]);
                seeds.add(new ServerAddress(ip, port));
                credentialsList.add(MongoCredential.createMongoCRCredential(uname, dbName, upass.toCharArray()));
            }
            mongoClient = new MongoClient(seeds, credentialsList);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (mongoClient != null) {
            mongoClients.put(ip, mongoClient);
        }
        return mongoClient;
    }


    /**
     * 获取mongo链接
     * @param host 数据库服务器IP
     * @param port  数据库端口号
     * @param dbname  数据库名
     * @param collectionName  集和名
     * @param uname  用户名
     * @param upwd   密码
     * @return
     */
    public static DBCollection getMongoConn(String host,int port,String dbname,String collectionName,String uname,String upwd){
        DBCollection conn = null;
        String connKey = collectionName + "_" + dbname + "@"+host;
        if(mongoConns.get(connKey)!=null){
            return mongoConns.get(connKey);
        }

        MongoClient mc = null;
        try {
            mc = new MongoClient(host, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mc.getDB(dbname);
        char[] passwd = upwd.toCharArray();
        db.authenticateCommand(uname, passwd);
        conn = db.getCollection(collectionName);

        mongoConns.put(connKey, conn);

        return conn;
    }

    /**
     * 查询Mongo库
     * @param conn 数据库链接
     * @param searchQuery 查询对象
     * @return 查询结果BasicDBList
     */
    public static BasicDBList query(DBCollection conn,BasicDBObject searchQuery,int n){
        BasicDBList resultList = new BasicDBList();

        DBCursor cursor = conn.find(searchQuery).limit(n);
        while(cursor.hasNext()){
            DBObject dbo = cursor.next();
            resultList.add(dbo);
        }
        return resultList;
    }

    /**
     * 查询Mongo库
     * @param conn 数据库链接
     * @param searchQuery 查询对象
     * @return 查询结果BasicDBList
     */
    public static BasicDBList query(DBCollection conn,BasicDBObject searchQuery,BasicDBObject sortQuery,int pageNo,int pageSize){
        BasicDBList resultList = new BasicDBList();

        DBCursor cursor = conn.find(searchQuery).sort(sortQuery).skip((pageNo-1)*pageSize).limit(pageSize);
        while(cursor.hasNext()){
            DBObject dbo = cursor.next();

            resultList.add(dbo);
        }
        return resultList;
    }
}
