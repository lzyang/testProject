package current;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by root on 16-6-10.
 */
public class TestShutDown {

    private class worker implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(new Random(System.currentTimeMillis()).nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void testRun(){
        BlockingDeque quene = new LinkedBlockingDeque<worker>();
        ExecutorService executor = new ThreadPoolExecutor(5,5,0L,TimeUnit.MILLISECONDS,quene);
//        ExecutorService executor = Executors.newFixedThreadPool(2);

        int num = 1000;

        while (--num>0){
            executor.execute(new worker());
        }

        executor.shutdown();
        while (!executor.isTerminated()){
            try {
                Thread.sleep(1000);
                System.out.println("size:" + quene.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TestShutDown tsd = new TestShutDown();
        tsd.testRun();
    }
}
