import com.mongodb.BasicDBObject;
import io.netty.buffer.UnpooledHeapByteBuf;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    @Test
    public void testBuf(){

    }

    @Test
    public void convTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date(1427359588502l)));//1423106324413
        System.out.println(sdf.format(new Date(1426238370913l)));//1423106324413

        try {
            Date d = sdf.parse("2015-03-13 17:00:00");
            System.out.println(d.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
