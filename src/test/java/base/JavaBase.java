package base;

import org.junit.Test;

/**
 * Created by root on 15-6-12.
 */
public class JavaBase {

    @Test
    public void testSysProp(){
        //获取全部properties
        //System.out.println(System.getProperties());
        //JDK version
        System.out.println(System.getProperty("java.runtime.version"));
        //java version
        System.out.println(System.getProperty("java.version"));
        //项目目录
        System.out.println(System.getProperty("user.dir"));
        System.out.println(System.getProperty("os.name"));
    }
}
