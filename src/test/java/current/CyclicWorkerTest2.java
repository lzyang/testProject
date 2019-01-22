package current;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicWorkerTest2 {

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println("Action .... Go,again!");
            }
        });
        for(int i=0;i<5;i++){
            Thread t = new Thread(new CyclicWork(barrier));
            t.start();
        }
    }

    private static class CyclicWork implements Runnable {
        private CyclicBarrier barrier;
        public CyclicWork(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.println(Thread.currentThread().getName() + "  Executed!");
                    barrier.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
