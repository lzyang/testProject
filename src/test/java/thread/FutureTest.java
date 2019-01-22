package thread;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class FutureTest {

    @Test
    public void testFuture() throws ExecutionException, InterruptedException {
        List<Future<String>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for(int i=0;i<10;i++){
           futures.add(executorService.submit(new SubTask(String.valueOf(i))));
        }

        for(Future<String> f: futures){
            if(!f.isDone()){
                System.out.println("M___");
            }
            System.out.println("M_____"+f.get());
        }
    }

    private class SubTask implements Callable<String> {

        private String param;

        public SubTask(String param) {
            this.param = param;
        }

        @Override
        public String call() throws Exception {
            String threadName = Thread.currentThread().getName() + "____" + param;
            Thread.sleep(new Random().nextInt(10000));
            System.out.println(threadName);
            return threadName;
        }
    }
}
