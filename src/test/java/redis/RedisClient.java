package redis;

import org.junit.Test;
import redis.clients.jedis.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by Morningsun(515190653@qq.com) on 15-7-23.
 */
public class RedisClient {


    /**
     * 分布式异步调用
     * TODO  old
     */
    @Test
    public void jedisShardpipeline(){
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("10.144.33.211",7000),
                new JedisShardInfo("10.144.33.211",7001),
                new JedisShardInfo("10.144.33.211",7002),
                new JedisShardInfo("10.144.33.211",7003),
                new JedisShardInfo("10.144.33.211",7004),
                new JedisShardInfo("10.144.33.211",7005)
        );

        ShardedJedis sharding = new ShardedJedis(shards);

        System.out.println(sharding.getAllShardInfo());

//        ShardedJedisPipeline pipeline = sharding.pipelined();
//
//        pipeline.set("","");
//
//        List<Object> result = pipeline.syncAndReturnAll();
//
//        sharding.disconnect();
    }

    /**
     * 链接池同步调用
     * TODO old
     */
    public void jedisShardSimplePool(){
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("10.144.33.211",7000),
                new JedisShardInfo("10.144.33.211",7001),
                new JedisShardInfo("10.144.33.211",7002),
                new JedisShardInfo("10.144.33.211",7003),
                new JedisShardInfo("10.144.33.211",7004),
                new JedisShardInfo("10.144.33.211",7005)
        );

        ShardedJedisPool pool = new ShardedJedisPool(new JedisPoolConfig(),shards);
        ShardedJedis one = pool.getResource();

        one.set("key","value");

        pool.returnResource(one);

        pool.destroy();
    }

    /**
     *连接池异步调用
     * TODO old
     */
    public void jedisShardPipelinePool(){
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("10.144.33.211",7000),
                new JedisShardInfo("10.144.33.211",7001),
                new JedisShardInfo("10.144.33.211",7002),
                new JedisShardInfo("10.144.33.211",7003),
                new JedisShardInfo("10.144.33.211",7004),
                new JedisShardInfo("10.144.33.211",7005)
        );

        ShardedJedisPool pool = new ShardedJedisPool(new JedisPoolConfig(),shards);

        ShardedJedis one = pool.getResource();
        ShardedJedisPipeline pipeline = one.pipelined();

        pipeline.set("key","value");

        List<Object> result = pipeline.syncAndReturnAll();
        pool.returnResource(one);

        pool.destroy();
    }


    @Test
    public void jedisPoolTest(){
        JedisPoolConfig conf = new JedisPoolConfig();
        JedisPool pool = new JedisPool(conf,"10.144.33.211",7000);
        Jedis jedis = pool.getResource();
        System.out.println("==========jedis.clusterInfo()============ \n"+jedis.clusterInfo());
        System.out.println("==========jedis.clusterNodes()============ \n"+jedis.clusterNodes());
        System.out.println("==========jedis.clusterSaveConfig()============ \n"+jedis.clusterSaveConfig());
        System.out.println("==========jedis.clusterSlots()============ \n"+jedis.clusterSlots());


    }
}
