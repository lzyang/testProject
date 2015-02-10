package current;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 倒计时锁测试
 * 模拟了100米赛跑，当所有人都到达终点时，比赛结束。
 * @author root
 *
 */
public class CountDownLatchTest {

	public static void main(String[] args){
		//开始
		final CountDownLatch begin = new CountDownLatch(1);
		//结束
		final CountDownLatch end = new CountDownLatch(10);
		
		final ExecutorService exec = Executors.newFixedThreadPool(10);
		
		for(int index = 0;index<10;index++){
			final int No = index + 1;
			Runnable run = new Runnable() {
				@Override
				public void run() {
					try {
						begin.await();  //比赛为开始等待
						Thread.sleep(new Random().nextInt(10000));
						System.out.println("No." + No + " arrived!");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}finally{
						end.countDown();  //未到终点人数减1
					}
				}
			}; 
			
			exec.submit(run);
		}
		
		System.out.println("Gome start!");
		begin.countDown(); //比赛开始
		try {
			end.await();  //等待所有人到终点
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Gome end!");
		exec.shutdown();
	}
}
