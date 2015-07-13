package thread;

import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Morningsun(515190653@qq.com) on 15-7-13.
 */
public class TestThread {
    private ConcurrentHashMap hashMap = new ConcurrentHashMap();

    @Test
    public void mainThread(){
        new Thread(new subThread()).start();
        long num = 0;
        while (true){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hashMap.put("key"+(num++),num);
            System.out.println("key added"+num);
        }
    }

    private class subThread implements Runnable{

        @Override
        public void run() {
            int start = 0;
            try {
                while (true){
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                    int size = hashMap.size();
                    if(size - start >10){  //HashMap时，必须sleep才能进入if语句，为什么？
                        start = size;
                        System.out.println("hashMap Size:"+hashMap.size());
                    }
                }
            }finally {
                System.out.println("sub Thread end");
            }
        }
    }
}
