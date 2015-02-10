package current;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorTest {
	
	public static void main(String[] args){
		//BlockingQueue<Runnable> queue = new LinkedBlockingDeque<Runnable>();
		SynchronousQueue<Runnable> queue = new SynchronousQueue<Runnable>();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 20, 1, TimeUnit.SECONDS, queue);
		for(int i=0;i<20;i++){
			final int index = i;
			executor.execute(new Runnable() {
				@Override
				public void run() {
					int sleepTime = new Random().nextInt(10000);
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(String.format(Thread.currentThread().getName() + " num: %d finished! sleepTime " + sleepTime, index));
				}
			});
		}
		executor.shutdown();
	}
}
