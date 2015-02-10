package current;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierTest {
	
	class Runner implements Runnable{
		private CyclicBarrier barrier = null;
		private String name = "";
		
		public Runner(CyclicBarrier barrier,String name) {
			this.barrier = barrier;
			this.name = name;
		}
		@Override
		public void run() {
			try {
				Thread.sleep(new Random().nextInt(10000));
				System.out.println(name + " ready!");
				barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
			System.out.println(name + " Go!!");
		}
	}
	
	public Runner getRunner(CyclicBarrier barrier,String name){
		return new Runner(barrier, name);
	}
	
	/**
	 * 每个线程代表一个跑步运动员，当运动员都准备好后，才一起出发，只要有一个人没有准备好，大家都等待.
	 * @param args
	 */
	public static void main(String[] args){
		CyclicBarrier barrier = new CyclicBarrier(5);
		ExecutorService executor = Executors.newFixedThreadPool(5);
		CyclicBarrierTest cbt = new CyclicBarrierTest();
		executor.submit(cbt.new Runner(barrier, "孙一"));
		executor.submit(cbt.getRunner(barrier, "张三"));
		executor.submit(cbt.getRunner(barrier, "李四"));
		executor.submit(cbt.getRunner(barrier, "王五"));
		executor.submit(cbt.getRunner(barrier, "赵六"));
		executor.shutdown();
	}
}
