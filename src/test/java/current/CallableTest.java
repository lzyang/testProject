package current;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CallableTest {
	
	public static AtomicInteger num = new AtomicInteger(1);
	
	/**
	 * 阻塞主线程执行直到子线程执行结束
	 * @param args
	 */
	public static void main(String[] args){
		System.out.println("start main thread!");
		final ExecutorService exec = Executors.newFixedThreadPool(5);
		
		List<Future<String>> tasks = new ArrayList<Future<String>>();
		for(int i=0;i<10;i++){
			Callable<String> call = new Callable<String>(){
				@Override
				public String call() throws Exception {
					System.out.println("start new Thread name:" + Thread.currentThread().getName());
					Thread.sleep(new Random().nextInt(10000));
					System.out.println("Thread end name:" + Thread.currentThread().getName());
					return "No." + CallableTest.num.getAndIncrement();
				}
			};
			tasks.add(exec.submit(call));
		}
		
		try {
			for(Future<String> task:tasks){
				System.out.println(">>>return "+task.get());
			}
			exec.shutdown();
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			System.out.println("end main Thread!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}
}
