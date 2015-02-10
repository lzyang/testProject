package current;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {
	
	public static void main(String[] args){
		ExecutorService execService = Executors.newFixedThreadPool(3);
		for(int i=0;i<300;i++){
			Runnable run = new Runnable() {
				@Override
				public void run() {
					System.out.println("run " + Thread.currentThread().getName());
				}
			};
			execService.execute(run);
		}
		execService.shutdown();
		try {
			execService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("complate!");
	}
}
