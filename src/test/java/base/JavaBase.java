package base;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

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
        assert 1 > 2 : "条件错误";  //   程序终止，抛出assertionError异常
        System.out.println("code finish");
    }

    @Test
    public void testArrayBlockQuene(){
        final ArrayBlockingQueue<Integer> abq = new ArrayBlockingQueue<Integer>(50);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<200;i++){
                    try {
                        Thread.sleep(300);
                        System.out.println("abq.put(" + i +")");
                        abq.put(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        while (true){
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(abq.poll() + "_" + abq.size());
        }
    }

    public void testBase(){
        try {
           CountDownLatch cnl =  new CountDownLatch(10);
           cnl.await();
           cnl.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
