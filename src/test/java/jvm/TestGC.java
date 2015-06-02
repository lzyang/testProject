package jvm;

import com.sun.corba.se.impl.encoding.CodeSetConversion;
import org.junit.Test;

import java.util.Vector;

/**
 * Created by root on 15-6-1.
 */
public class TestGC {

    /**
     * -ea -Xms100m -Xmx100m -Xmn40m -XX:+PrintGCDetails -XX:PermSize=3m -XX:MaxPermSize=3m
     */
    @Test
    public void testPermGC() {
        for (int i = 0; i < Long.MAX_VALUE; i++) {
            String test = String.valueOf(i).intern();
        }
    }

    /**
     * -ea -Xms100m -Xmx100m -Xmn40m -XX:+PrintGCDetails
     */
    @Test
    public void testXmx() {
        try {
            Vector<byte[]> cache = new Vector<byte[]>();
            for (int i = 1; i < Long.MAX_VALUE; i++) {
                byte[] a = new byte[1024 * 1024];
                cache.add(a);
                System.out.println("cache" + i + "M");
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Max memory:" + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "M.");
        }
    }
}
