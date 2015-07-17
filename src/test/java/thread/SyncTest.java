package thread;

import com.sysnote.utils.StringUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Morningsun(515190653@qq.com) on 15-7-17.
 */
public class SyncTest {

    public synchronized void a(){
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(StringUtil.currentFullTime() + ":  a finish!");
    }

    public synchronized  void b(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(StringUtil.currentFullTime() + ":  b exec");
    }

    public synchronized  void c(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(StringUtil.currentFullTime() + ":  c exec");
    }

    public void d(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(StringUtil.currentFullTime() + ":  d exec");
    }

    public void e(){
        synchronized (this){
            try {
                Thread.sleep(6000);
                System.out.println(StringUtil.currentFullTime() + ":  e exec");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 2015-07-17 11:43:33:661:  test start...
     2015-07-17 11:43:34:220:  d exec
     2015-07-17 11:43:43:696:  a finish!
     2015-07-17 11:43:45:697:  c exec
     2015-07-17 11:43:48:697:  b exec
     2015-07-17 11:43:48:698:  test end!!
     */
    @Test
    public void test(){
        final SyncTest st = new SyncTest();
        final ExecutorService exec = Executors.newFixedThreadPool(5);
        final CountDownLatch cdl = new CountDownLatch(5);
        System.out.println(StringUtil.currentFullTime() + ":  test start...");
                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        st.a();
                        cdl.countDown();
                    }
                });

        exec.submit(new Runnable() {
            @Override
            public void run() {
                st.b();
                cdl.countDown();
            }
        });

        exec.submit(new Runnable() {
            @Override
            public void run() {
                st.c();
                cdl.countDown();
            }
        });

        exec.submit(new Runnable() {
            @Override
            public void run() {
                st.d();
                cdl.countDown();
            }
        });

        exec.submit(new Runnable() {
            @Override
            public void run() {
                st.e();
                cdl.countDown();
            }
        });

        try {
            cdl.await();
            System.out.println(StringUtil.currentFullTime() + ":  test end!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            exec.shutdown();
        }
    }
}
