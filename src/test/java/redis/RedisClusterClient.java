package redis;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import javax.sound.midi.Soundbank;
import java.util.*;

/**
 * Created by Morningsun(515190653@qq.com) on 15-7-27.
 */
public class RedisClusterClient {
    protected static Logger logger = LoggerFactory.getLogger(RedisClusterClient.class);
    private static Map<String,RedisClusterClient> clusters = new HashMap<String, RedisClusterClient>();
    private  JedisCluster jedisCluster = null;

    private enum redisModule{es,def;};
    static {
        init();
    }

    private static void init(){
        String ips = "10.144.33.211:7000,10.144.33.211:7001,10.144.33.211:7002,10.144.33.211:7003,10.144.33.211:7004,10.144.33.211:7005";
        String [] ipPorts = ips.split(",");
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        for(String item:ipPorts){
            String[] addrItem = item.split(":");
            nodes.add(new HostAndPort(addrItem[0],Integer.parseInt(addrItem[1])));
        }

        JedisCluster es = new JedisCluster(nodes,5000,poolConfig(redisModule.es));
        JedisCluster def = new JedisCluster(nodes,5000,poolConfig(redisModule.def));
        clusters.put("es",new RedisClusterClient(es));
        clusters.put("default", new RedisClusterClient(def));
    }

    RedisClusterClient(JedisCluster jedisCluster){
        this.jedisCluster = jedisCluster;
    }

    private static JedisPoolConfig poolConfig(redisModule module){
        JedisPoolConfig conf = new JedisPoolConfig();
        switch (module){
            case es:
                conf.setMaxWaitMillis(10000);
                conf.setMaxIdle(60000);
                conf.setMaxTotal(6000);
            case def:
                conf.setMaxWaitMillis(10000);
                conf.setMaxIdle(60000);
                conf.setMaxTotal(6000);
        }
        return conf;
    }

    public static RedisClusterClient es(){
        return clusters.get("es");
    }

    public static RedisClusterClient def(){
        return clusters.get("default");
    }

    //--------------------------------存取方法--------------------------------------

    /**
     * 获取指定key的值
     * @param key
     * @return
     */
    public String get(String key){
        return jedisCluster.get(key);
    }

    /**
     * 移除指定key
     * @param key
     */
    public void remove(String key){
        jedisCluster.del(key);
    }

    /**
     * 设置指定key的过期时间
     * @param key
     * @param seconds
     */
    public void expire(String key,int seconds){
        jedisCluster.expire(key,seconds);
    }

    /**
     * 获取value为hash类型指定key的值
     * @param key
     * @param field
     * @return
     */
    public String hget(String key,String field){
        return jedisCluster.hget(key,field);
    }

    /**
     * 获取value为hash的整个hash值
     * @return
     */
    public Map<String,String> hgetAll(String key){
        return jedisCluster.hgetAll(key);
    }

    /**
     * 插入值类型为list的头部
     * @param key
     * @param value
     * @return  插入后，list中元素的总个数
     */
    public Long lpush(String key,String... value){
        return jedisCluster.lpush(key,value);
    }

    /**
     * 插入值类型为list的尾部
     * @param key
     * @param value
     * @return  插入后,list元素的总个数
     */
    public Long rpush(String key,String... value){
        return jedisCluster.rpush(key,value);
    }

    /**
     * 移除list类型中首个元素
     * @param key
     * @return  返回移除后整个list
     */
    public String lpop(String key){
        return jedisCluster.lpop(key);
    }

    /**
     * 移除list类型末端元素
     * @param key
     * @return  返回移除后整个list
     */
    public String rpop(String key){
        return jedisCluster.rpop(key);
    }

    /**
     * 移除list中和value匹配的值，移除个数通过cout来控制，
     * @param key
     * @param count 为负表示从末端开始匹配，为正表示从前端开始匹配
     * @param value
     * @return  删除元素的数量
     */
    public Long lrem(String key,int count,String value){
        return jedisCluster.lrem(key,count,value);
    }


    /**
     * 返回指定范围的list
     * @param key
     * @param start  开始位置，其中0代表第一个,1代表第二个，-1代表最后一个，-2代表倒数第二个
     * @param end  结束位置，同start
     * @return
     */
    public List<String> lrange(String key,int start,int end){
        return jedisCluster.lrange(key,start,end);
    }

    /**
     * 返回list的长度
     * @param key
     * @return
     */
    public long llen(String key){
        return jedisCluster.llen(key);
    }

    /**
     * 设置值为String的key
     * @param key
     * @param value
     * @return
     */
    public String set(String key,String value){
        return jedisCluster.set(key,value);
    }

    /**
     * 设置hash值，如果key不存在则创建，如果存在则插入
     * @param key
     * @param hash
     * @return
     */
    public String hmset(String key,Map<String,String> hash){
        return jedisCluster.hmset(key,hash);
    }

    /**
     * 设置hash值，如果field不存在则转创建，如果key不存在则创建，如果field存在,则覆盖对应的value
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hset(String key,String field,String value){
        return jedisCluster.hset(key,field,value);
    }

    /**
     * 删除指定的field，如果field不存在则返回0,如果存在则返回1,如果key不存在，则创建一个空的map
     * @param key
     * @param field
     */
    public void hdelete(String key,String field){
        jedisCluster.hdel(key,field);
    }

    /**
     * 设置有顺序的set，通过指定分数来完成，如果成员不存在,则加入，如果存在则更新分数并执行重新插入到对应的位置，以保证排序
     * 如果key已经存在，但是不是排序类型的set，则返回错误
     * @param key
     * @param score
     * @param member
     * @return 1,表示插入，0表示更新分数
     */
    public Long zadd(String key,double score,String member){
        return jedisCluster.zadd(key,score,member);
    }

    /**
     * 批量插入，3.0新功能，返回值Jedis暂时木有文档
     * @param key
     * @param scoreMember
     * @return
     */
    public Long zadd(String key,Map<String,Double> scoreMember){
        return jedisCluster.zadd(key,scoreMember);
    }

    /**
     * 自增排序set的score，如果key或member不存在则创建，如果key已存在但是不是排序类型，则报错
     * @param key
     * @param score
     * @param member
     */
    public void zincrby(String key,double score,String member){
        jedisCluster.zincrby(key,score,member);
    }

    /**
     * 返回指定分数范围内的元素，包含下限//TODO test
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrevrange(String key,long start,long end){
        return jedisCluster.zrevrange(key,start,end);
    }

    /**
     * 自增整形value，如果value非整形，则报错，如果不存在则设置为0
     * @param key
     * @param integer
     */
    public void incrBy(String key,long integer){
        jedisCluster.incrBy(key,integer);
    }

    /**
     * 判断field是否在存储的hash中存在
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(String key,String field){
        return jedisCluster.hexists(key,field);
    }

    /**
     * 判断是否存在对应的key
     * @param key
     * @return
     */
    public boolean exists(String key){
        return jedisCluster.exists(key);
    }


    public Set<String> keys(String key){
        
        return null;
    }
    public static void main(String[] args){
        int module = 1;
        RedisClusterClient jc = RedisClusterClient.es();
        switch (module){
            case 0:
                jc.set("simset", "tvalue");
                System.out.println(jc.get("simset"));
                break;
            case 1:
                jc.remove("mp_t1");
                System.out.println(jc.hgetAll("mp_t1"));

                jc.hset("mp_t1","key1","k1");
                jc.hset("mp_t1","key2","k2");
                System.out.println(jc.hget("mp_t1","key1"));
                Map<String,String> tm = new HashMap<String, String>();
                tm.put("key3","k3");
                tm.put("key4","k4");
                System.out.println(jc.hmset("mp_t1", tm));
                System.out.println(jc.hget("mp_t1", "key3"));
                jc.hset("mp_t1", "key5", "k5");
                jc.hdelete("mp_t1", "key2");
                System.out.println(jc.hgetAll("mp_t1"));

                jc.expire("mp_t1",5);
                try {
                    Thread.sleep(4000);
                    System.out.println(jc.hgetAll("mp_t1"));
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(jc.exists("mp_t1"));
                System.out.println(jc.hgetAll("mp_t1"));
                break;
            case 2:
                break;
        }
    }
}
