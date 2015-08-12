package guava;

import com.google.common.collect.MapMaker;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-12.
 */
public class MapTest {

    /**
     * 测试结果,-xmn均匀分配egen,map如果插入相同的key,如果map不能整个释放,只有在fullgc的时候才能释放
     */
    @Test
    public void mapMemoryTest(){
//        ConcurrentMap<String,byte[]> map = new MapMaker().makeMap();
        ConcurrentMap<String,byte[]> map = new ConcurrentHashMap<String, byte[]>();
        byte [] v = null;
        for(int i=0;i<100;i++){
            v = new byte[1024*1024];
            String key = "key"+(i%10);
//            if(!map.containsKey(key)){
//                map.put(key,v);
//            }
            map.put(key,v);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("times>>>"+i + "  " + "key"+(i%10));
        }
    }
}
