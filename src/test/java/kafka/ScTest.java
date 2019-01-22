package kafka;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Properties;

public class ScTest {

    public static void main(String[] args) {
        Properties props = new Properties();

        //神测
        props.put("bootstrap.servers", "10.115.4.144:9092,10.115.4.182:9092,10.115.4.183:9092");
//        props.put("bootstrap.servers", "data02.gome.sa:9092");
        //大数据中心
//        props.put("bootstrap.servers", "10.115.4.114:9092,10.115.4.115:9092,10.115.4.119:9092");
//        props.put("bootstrap.servers", "10.58.222.108:9092");
//        props.put("bootstrap.servers","10.115.4.144:9092");
        //搜索
//        props.put("bootstrap.servers","10.115.1.62:9092,10.115.1.63:9092,10.115.1.64:9092");
        //每个消费者分配独立的组号
        props.put("group.id", "search_test_932");

        //如果value合法，则自动提交偏移量
        props.put("enable.auto.commit", "true");

        //设置多久一次更新被消费消息的偏移量
        props.put("auto.commit.interval.ms", "1000");
        // 从何处开始消费,latest 表示消费最新消息,earliest 表示从头开始消费,none表示抛出异常,默认latest
        props.put("auto.offset.reset","latest");
//        props.put("auto.offset.reset","latest");

        //设置会话响应的时间，超过这个时间kafka可以选择放弃消费或者消费下一条消息
        props.put("session.timeout.ms", "30000");

        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String,String>(props);

        consumer.subscribe(Collections.singletonList("event_topic"));
//        consumer.subscribe(Collections.singletonList("wc-input"));
        String path="/server/sence/shencedata.log";


       /* Collection<PartitionInfo> partitionInfos = consumer.partitionsFor("event_topic");
        Collection<TopicPartition> topicPartitionCollections = new ArrayList<>();
        for (PartitionInfo partitionInfos1 :partitionInfos) {
            TopicPartition topicPart = new TopicPartition("event_topic", partitionInfos1.partition());
            topicPartitionCollections.add(topicPart);
        }
        consumer.seekToBeginning(topicPartitionCollections);*/

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records){
                // print the offset,key and value for the consumer records.
//                System.out.printf("offset = %d, key = %s, value = %s\n",
//                        record.offset(), record.key(), record.value());

                BasicDBObject item = (BasicDBObject) JSON.parse(record.value());
                String event = item.getString("event");
                BasicDBObject prop = (BasicDBObject) item.get("properties");
                String screen_name = prop.getString("$screen_name");
                BasicDBObject lib = (BasicDBObject) item.get("lib");
                String sdk = lib.getString("$lib");
                String url = prop.getString("$url");
                String os = prop.getString("$os");
                String title = prop.getString("$title");

                System.out.printf("os= %s, sdk= %s, event= %s, title= %s, url= %s, screenName= %s, \n",
                            os, sdk, event, title, url,screen_name);
                if((event!=null&&!event.contains("$")) || os == null){
                    System.out.println("####" + record.value());
                }

//                StringBuffer sb=new StringBuffer();
//                sb.append(record.value()+"\r\n");
//                System.out.println(sb.toString());
//                writeLog(path,sb.toString());
            }
        }
    }

    public static void writeLog(String fileName, String content) {
        RandomAccessFile randomFile = null;
        try {
            // 打开一个随机访问文件流，按读写方式
            randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.write(content.getBytes("utf-8"));
//            randomFile.writeBytes(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(randomFile != null){
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
