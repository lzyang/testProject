package datahandle;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.sysnote.db.MongoTools;
import org.junit.Test;

import java.io.FileWriter;

public class MongoData {

      @Test
      public void saveMongoData() throws Exception{
            DBCollection conn = MongoTools.getConn("Xx.XX.XX.XX",27017,"product_info","simple_json");
            long allDataSize = conn.count();
            System.out.println("Total data count:" + allDataSize);
            System.out.println(conn.findOne());
            DBCursor cursor = conn.find();

            String filePath = "/Users/morningsun/data/source/simple_json.txt";
            FileWriter writer = new FileWriter(filePath, true);
            long totalCount = 0;
            long start = System.currentTimeMillis();
            while (cursor.hasNext()){
                  String content = cursor.next().toString();
                  writer.write(content+"\r\n");
                  if((++totalCount)%10000==0){
                        long now = System.currentTimeMillis();
                        System.out.println(allDataSize + " of No." + totalCount + " time use:" + (now-start));
                  }
//                  if(totalCount>100) break;
            }

            writer.close();
      }
}
