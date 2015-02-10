package current;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 拿到信号量的线程可以进入代码，否则就等待。通过acquire()和release()获取和释放访问许可。
 * 下面的例子只允许5个线程同时进入执行acquire()和release()之间的代码
 * @author root
 *
 */
public class SemaphoreTest {
	public static void main(String[] args){
		ExecutorService exec = Executors.newCachedThreadPool();
		
		final Semaphore semp = new Semaphore(5);
		for(int index=0;index<20;index++){
			final int NO = index;
			Runnable run = new Runnable() {
				@Override
				public void run() {
					try {
						semp.acquire();
						System.out.println("access succeed! No." + NO);
						Thread.sleep(new Random().nextInt(10000));
						semp.release();
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
