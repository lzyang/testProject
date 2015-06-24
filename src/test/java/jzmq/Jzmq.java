package jzmq;

import org.junit.Test;
import org.zeromq.ZMQ;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-24.
 */
public class Jzmq {

    @Test
    public void Response(){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        socket.bind ("tcp://*:5555");
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
        socket.connect("tcp://127.0.0.1:5555");
        for (int i = 0; i < 100000; i++) {
            String request = "hello";
            socket.send(request);
            byte[] response = socket.recv();
            System.out.println("receive : " + new String(response));
        }
        socket.close();
        context.term();
    }
}
