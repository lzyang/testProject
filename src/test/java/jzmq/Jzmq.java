package jzmq;

import com.sysnote.utils.StringUtil;
import org.junit.Test;
import org.zeromq.ZMQ;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-24.
 */
public class Jzmq {

    //========================================req  res==========================
    @Test
    public void Response(){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        socket.bind ("tcp://127.0.0.1:5501");
        while (!Thread.currentThread().isInterrupted()){
            byte[] request = socket.recv();
            System.out.println(new String(request));
            String respText = "response text returned";
            socket.send(respText);
        }
        socket.close();
        context.term();
    }

    @Test
    public void Request(){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        socket.connect("tcp://127.0.0.1:5501");
        for (int i = 0; i < 100000; i++) {
            String request = "hello";
            socket.send(request);
            byte[] response = socket.recv();
            System.out.println("receive : " + new String(response));
        }
        socket.close();
        context.term();
    }

    //=========================================pub  sub======================================
    @Test
    public void publisher(){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://127.0.0.1:5502");
        while (!Thread.currentThread ().isInterrupted ()) {
            String message = "pub Msg @"+ StringUtil.currentFullTime();  //pub 订阅topic?
            publisher.send(message);
            long t = new Random().nextInt(5000);
            System.out.println(message + "  #" + t);
            try {
                Thread.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        publisher.close();
        context.term();
    }

    @Test
    public void subscriber(){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect("tcp://127.0.0.1:5502");
        subscriber.subscribe("pub".getBytes());
        while (!Thread.currentThread().isInterrupted()){
            byte[] message = subscriber.recv();
            System.out.println("receive : " + new String(message));
        }
        subscriber.close();
        context.term();
    }

    //===========================================push pull=====================================
    @Test
    public void push(){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket push  = context.socket(ZMQ.PUSH);
        push.bind("ipc://fjs");

        for (int i = 0; i < 10000000; i++) {
            push.send("hello pull");
        }
        push.close();
        context.term();
    }

    @Test
    public void pull(){
        final AtomicInteger number = new AtomicInteger(0);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable(){
                private int here = 0;
                public void run(){
                    ZMQ.Context context = ZMQ.context(1);
                    ZMQ.Socket pull = context.socket(ZMQ.PULL);
                    pull.connect("ipc://fjs");
                    while (true) {
                        String message = new String(pull.recv());
                        int now = number.incrementAndGet();
                        here++;
                        if (now % 1000000 == 0) {
                            System.out.println(now + "  here is : " + here);
                        }
                    }
                }
            }).start();
        }
    }

}
