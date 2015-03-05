import com.mongodb.BasicDBObject;
import io.netty.buffer.UnpooledHeapByteBuf;
import org.junit.Test;

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
}
