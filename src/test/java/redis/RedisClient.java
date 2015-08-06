package redis;

import org.junit.Test;
import redis.clients.jedis.*;

import java.lang.reflect.Array;
import java.util.*;

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

//        pipeline.set("key","value");
//
        one.set("tshard","213");
//        List<Object> result = pipeline.syncAndReturnAll();
        pool.returnResource(one);

        pool.destroy();
    }


    @Test
    public void monitor(){
        final  Jedis jedis = new Jedis("10.144.33.211",7000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<20;i++){
                    jedis.incr("monitorTest");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        jedis.monitor(new JedisMonitor() {
            @Override
            public void onCommand(String command) {
                System.out.println(command);
            }
        });
    }

    @Test
    public void jedisPoolTest(){
        JedisPoolConfig conf = new JedisPoolConfig();
//        conf.setMaxTotal(8);//active
//        conf.setMaxIdle(4);//idle
//        conf.setTestOnCreate(true);
        JedisPool pool = new JedisPool(conf,"10.144.33.211",7000,5000);
        Jedis jedis = pool.getResource();
//        System.out.println("==========jedis.clusterInfo()============ \n"+jedis.clusterInfo());
        System.out.println("==========jedis.clusterNodes()============ \n"+jedis.clusterNodes());
//        System.out.println("==========jedis.clusterSaveConfig()============ \n"+jedis.clusterSaveConfig());
//        System.out.println("==========jedis.clusterSlots()============ \n"+jedis.clusterSlots());

        jedis.set("server","mongo");
        System.out.println(jedis.get("server"));

        jedis.close();//return resource
        pool.destroy();
    }

    @Test
    public void testConnect(){
        Connection conn = new Connection();
        conn.setHost("10.144.33.211");
        conn.setPort(7000);
        conn.connect();
    }

    @Test
    public void testClusterPool(){
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        nodes.add(new HostAndPort("10.144.33.211",7000));
        nodes.add(new HostAndPort("10.144.33.211",7001));
        nodes.add(new HostAndPort("10.144.33.211",7002));
        nodes.add(new HostAndPort("10.144.33.211",7003));
        nodes.add(new HostAndPort("10.144.33.211",7004));
        nodes.add(new HostAndPort("10.144.33.211",7005));

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(7);
        config.setMaxTotal(10);

        final JedisCluster jc = new JedisCluster(nodes,5000,config);

//        jc.set("t1","v1");
//        System.out.println(jc.get("t1"));
//        System.out.println(jc.get("monitorTest"));


        System.out.println(jc.getClusterNodes());

        Map<String, JedisPool> poolMap = jc.getClusterNodes();



        for(String key:poolMap.keySet()){
            StringBuffer sbf = new StringBuffer();
            sbf.append(key).append("\n    ");
            JedisPool pool = poolMap.get(key);
            Jedis jedis = pool.getResource();
            sbf.append("  NumActive:" + pool.getNumActive());
            sbf.append("  Idle:" + pool.getNumIdle());
            sbf.append("  NumWaiters:" + pool.getNumWaiters());

            jedis.set("t2","v1:"+key);

            System.out.println(sbf);
        }
//        long num = 0;
//        long start = System.currentTimeMillis();
//        while(true){
//            num++;
//            if(num>1000000)break;
//            jc.set("sstc"+num,num+"");
//        }
//        long totalTime = System.currentTimeMillis()-start;
//        System.out.println("totalTime:"+ totalTime);
//        System.out.println("perCircle:"+totalTime/10000);




        jc.close();
    }
}
