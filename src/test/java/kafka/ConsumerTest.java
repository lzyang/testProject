package kafka;


import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by root on 16-4-14.
 */
public class ConsumerTest {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        Random rnd = new Random();
        int events = 100;

//        Properties props = new Properties();
//        props.put("metadata.broker.list","127.0.0.1:9092");
//        props.put("serializer.class","kafka.serializer.StringEncoder");
//        // key.serializer.class默认为serializer.class
//        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
//        // 可选配置，如果不配置，则使用默认的partitioner
//        props.put("partitioner.class", "com.catt.kafka.demo.PartitionerDemo");
//        // 触发acknowledgement机制，否则是fire and forget，可能会引起数据丢失
//        // 值为0,1,-1,可以参考
//        // http://kafka.apache.org/08/configuration.html
//        props.put("request.required.acks", "1");
//        Producer<String,String> producer = new Producer<String, String>(props);


        Properties props = new Properties();
//        props.put("zk.connect", "127.0.0.1:2181");
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("batch.size",0);
        props.put("send.buffer.bytes",1);
        props.put("buffer.memory",200);

//        props.put("serializer.class", "kafka.serializer.StringEncoder");
//        props.put("partitioner.class", "com.catt.kafka.demo.PartitionerDemo");
        Producer producer = new KafkaProducer<String, String>(props);

        for(int i=0;i<5;i++){
            ProducerRecord<byte[],byte[]> pr = new ProducerRecord<byte[],byte[]>("test","1111".getBytes(),(i +".No send xxxxxxxxxxxfrom java").getBytes());
            producer.send(pr, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if(e!=null) e.printStackTrace();
                    System.out.println("finish");
                    System.out.println(".....No.");
                }
            });
        }
    }
}
