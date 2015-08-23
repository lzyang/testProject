import com.mongodb.BasicDBObject;
import io.netty.buffer.UnpooledHeapByteBuf;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by root on 15-2-27.
 */
public class MyTest {

    @Test
    public void mongoField(){
        BasicDBObject o = new BasicDBObject();
        o.append("ss", 255);
        System.out.println(o.get("ss"));
        System.out.println(o.getInt("ss",0));
    }

    //TODO ERROR need debug
    @Test
    public void testWaite(){
        final AtomicInteger t1Tag = new AtomicInteger(0);

        Runnable r1 = new Runnable() {
            public void run() {
                try {
                    while (true){
                        Thread.sleep(1000);
                        System.out.println(t1Tag.addAndGet(1));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t1 = new Thread(r1);
        t1.start();

        try {
            Thread.sleep(5000);
            r1.wait();
            Thread.sleep(3000);
            r1.notify();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void convTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date(1420533755673l)));//1423106324413
        System.out.println(sdf.format(new Date(1426238370913l)));//1423106324413

        try {
            Date d = sdf.parse("2015-03-13 17:00:00");
            System.out.println(d.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
