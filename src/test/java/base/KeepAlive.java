package base;


import java.util.concurrent.CountDownLatch;

/**
 * Created by root on 17-3-14.
 */
public class KeepAlive {

    private static volatile Thread keepAliveThread;
    private static volatile CountDownLatch keepAliveLatch;

    public static void main(String[] args) {
        keepAliveLatch = new CountDownLatch(1);
        // keep this thread alive (non daemon thread) until we shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                keepAliveLatch.countDown();
            }
        });

        keepAliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    keepAliveLatch.await();
                } catch (InterruptedException e) {
                    // bail out
                }
            }
        }, "elasticsearch[keepAlive/lzy test]");
        keepAliveThread.setDaemon(false);
        keepAliveThread.start();

        System.out.println("Thread keep alive...");

        for(int i=0;i<10;i++){
            try {
                Thread.sleep(1000);
                System.out.println("t." + (10-i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        keepAliveLatch.countDown();
    }
}
