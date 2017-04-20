package base;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by root on 15-6-12.
 */
public class JavaBase {
    public static Logger logger = LoggerFactory.getLogger(JavaBase.class);

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

    @Test
    public void test(){
        System.out.println(this.getClass().getSimpleName());
        logger.error("122415222");
    }

    @Test
    public void testAssert(){
        System.out.println("code start");
        assert 1 > 2;  //   程序终止，抛出assertionError异常
        System.out.println("code finish");
    }
}
