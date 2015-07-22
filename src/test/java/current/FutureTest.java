package current;

import com.sysnote.utils.LogUtil;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * Created by Morningsun(515190653@qq.com) on 15-7-17.
 */
public class FutureTest {

    /**
     * 2015-07-22 14:49:59:296 [main:0]-[INFO] executing...
     2015-07-22 14:50:07:151 [pool-1-thread-1:7855]-[INFO] task exec finish!
     2015-07-22 14:50:07:151 [main:7855]-[INFO] result:task exec success!!
     */
    @Test
    public void testFuture(){
        ExecutorService exec = Executors.newSingleThreadExecutor();
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(8000);
                LogUtil.printLog("task exec finish!");
                return "task exec success!!";
            }
        });

        exec.execute(futureTask);
        LogUtil.printLog("executing...");
        try {
            String result = futureTask.get(10000, TimeUnit.MILLISECONDS);
            LogUtil.printLog("result:" + result);
        } catch (InterruptedException e) {
            futureTask.cancel(true);
            e.printStackTrace();
        } catch (ExecutionException e) {
            futureTask.cancel(true);
            e.printStackTrace();
        } catch (TimeoutException e) {
            futureTask.cancel(true);
            e.printStackTrace();
        }finally {
            exec.shutdown();
        }
    }
}
