package current;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 拿到信号量的线程可以进入代码，否则就等待。通过acquire()和release()获取和释放访问许可。
 * 下面的例子只允许5个线程同时进入执行acquire()和release()之间的代码
 * @author root
 *
 */
public class SemaphoreTest {
	public static void main(String[] args){
		ExecutorService exec = Executors.newCachedThreadPool();
		
		final Semaphore semap = new Semaphore(5);
        System.out.println("total available=" + semap.availablePermits());
        for(int index=0;index<1000;index++){
			final int NO = index;
			Runnable run = new Runnable() {
				@Override
				public void run() {
					try {
                        int timeOut = new Random().nextInt(50000);
                        boolean flag = semap.tryAcquire(timeOut, TimeUnit.MILLISECONDS);
                        if(!flag) {
                            System.out.println("activeCount"+ Thread.activeCount() + "  try Acquire failed! NO." + NO);
                            return;
                        }
                        //semap.acquire();
                        int ram = new Random().nextInt(10000);
                        System.out.println("activeCount"+ Thread.activeCount() +"  access succeed! No." + NO + "  ram=" + ram + "  semap=" + semap.availablePermits());
                        if(ram%5==0){
                            System.out.println("   >>>crash!! NO." + NO);
                            throw new RuntimeException();
                        }

						Thread.sleep(ram);
                        semap.release();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			exec.submit(run);
		}
		exec.shutdown();
	}
}
