package jvm;

import org.junit.Test;

/**
 * Created by root on 15-6-1.
 */
public class TestGC {

    /**
     * -ea -Xms100m -Xmx100m -Xmn40m -XX:+PrintGCDetails -XX:PermSize=3m -XX:MaxPermSize=3m
     */
    @Test
    public void testPermGC(){
        for(int i=0;i<Long.MAX_VALUE;i++){
            String test = String.valueOf(i).intern();
        }
    }

}
