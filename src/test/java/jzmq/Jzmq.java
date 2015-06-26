package jzmq;

import com.sysnote.utils.StringUtil;
import org.junit.Test;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-24.
 */
public class Jzmq {

    //========================================req  res==========================
    @Test
    public void Response() {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        socket.bind("tcp://127.0.0.1:5501");
        while (!Thread.currentThread().isInterrupted()) {
            byte[] request = socket.recv();
            System.out.println(new String(request));
            String respText = "response text returned";
            socket.send(respText);
        }
        socket.close();
        context.term();
    }

    @Test
    public void Request() {
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
    public void publisher() {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://127.0.0.1:5502");
        while (!Thread.currentThread().isInterrupted()) {
            String message = "pub Msg @" + StringUtil.currentFullTime();  //pub 订阅topic?
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
    public void subscriber() {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect("tcp://127.0.0.1:5502");
        subscriber.subscribe("pub".getBytes());
        while (!Thread.currentThread().isInterrupted()) {
            byte[] message = subscriber.recv();
            System.out.println("receive : " + new String(message));
        }
        subscriber.close();
        context.term();
    }

    //===========================================push pull=====================================
    @Test
    public void push() {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket push = context.socket(ZMQ.PUSH);
        push.bind("ipc://push-pull");

        for (int i = 0; i < 10000; i++) {
            push.send("hello pull");
            System.out.println("sss");
        }
        push.close();
        context.term();
    }

    @Test
    public void pull() {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pull = context.socket(ZMQ.PULL);
        pull.connect("ipc://push-pull");
        while (true) {
            String message = new String(pull.recv());
            System.out.println(message);
        }
    }

    //======================================poller======================================
    @Test
    public void poller() {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pull1 = context.socket(ZMQ.PULL);
        pull1.connect("tcp://127.0.0.1:5503");
        ZMQ.Socket pull2 = context.socket(ZMQ.PULL);
        pull2.connect("tcp://127.0.0.1:5503");

        ZMQ.Poller poller = new ZMQ.Poller(2);
        poller.register(pull1, ZMQ.Poller.POLLIN);
        poller.register(pull2, ZMQ.Poller.POLLIN);

        int i = 0;
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll();
            if (poller.pollin(0)) {
                while (null != pull1.recv(ZMQ.NOBLOCK)) {  //这里采用了非阻塞，确保一次性将队列中的数据读取完
                    i++;
                }
            }
            if (poller.pollin(1)) {
                while (null != pull2.recv(ZMQ.NOBLOCK)) {
                    i++;
                }
            }
            if (i % 10000000 == 0) {
                System.out.println(i);
            }
        }
        pull1.close();
        pull2.close();
        context.term();
    }

    //=============================Router  Dealer=============================
    @Test
    public void response() {
        final ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket router = context.socket(ZMQ.ROUTER);
        ZMQ.Socket dealer = context.socket(ZMQ.DEALER);

        router.bind("ipc://fjs1");
        dealer.bind("ipc://fjs2");

        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                public void run() {
                    ZMQ.Socket response = context.socket(ZMQ.REP);
                    response.connect("ipc://fjs2");
                    while (!Thread.currentThread().isInterrupted()) {
                        response.recv();
                        response.send("hello");
                        try {
                            Thread.currentThread().sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    response.close();
                }

            }).start();
        }
        ZMQ.proxy(router, dealer, null);
        router.close();
        dealer.close();
        context.term();
    }

    //=====================================负载均衡====================================
    @Test
    public void client() {
        new Thread(new Runnable() {
            public void run() {
                ZMQ.Context context = ZMQ.context(1);
                ZMQ.Socket socket = context.socket(ZMQ.REQ);
                socket.connect("ipc://front");
                for (int i = 0; i < 1000; i++) {
                    socket.send("hello".getBytes(), 0);
                    String bb = new String(socket.recv());
                    System.out.println(bb);
                }
                socket.close();
                context.term();
            }

        }).start();
    }

    @Test
    public void worker() {
        new Thread(new Runnable() {
            public void run() {
                ZMQ.Context context = ZMQ.context(1);
                ZMQ.Socket socket = context.socket(ZMQ.REQ);

                socket.connect("ipc://back");  //连接，用于获取要处理的请求，并发送回去处理结果
                socket.send("ready");  //发送ready，表示当前可用

                while (!Thread.currentThread().isInterrupted()) {
                    ZMsg msg = ZMsg.recvMsg(socket);  //获取需要处理的请求，其实这里msg最外面的标志frame是router对分配给client的标志frame
                    ZFrame request = msg.removeLast();   //最后一个frame其实保存的就是实际的请求数据，这里将其移除，待会用新的frame代替
                    ZFrame frame = new ZFrame("hello fjs".getBytes());
                    msg.addLast(frame);  //将刚刚创建的frame放到msg的最后，worker将会收到
                    msg.send(socket);  //将数据发送回去
                }
                socket.close();
                context.term();
            }
        }).start();
    }

    @Test
    public void middle() {
        LinkedList<ZFrame> workers = new LinkedList<ZFrame>();
        LinkedList<ZMsg> requests = new LinkedList<ZMsg>();
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Poller poller = new ZMQ.Poller(2);

        ZMQ.Socket fronted = context.socket(ZMQ.ROUTER);  //创建一个router，用于接收client发送过来的请求，以及向client发送处理结果
        ZMQ.Socket backend = context.socket(ZMQ.ROUTER);  //创建一个router，用于向后面的worker发送数据，然后接收处理的结果

        fronted.bind("ipc://front");  //监听，等待client的连接
        backend.bind("ipc://back");  //监听，等待worker连接

        //创建pollItem
        ZMQ.PollItem fitem = new ZMQ.PollItem(fronted, ZMQ.Poller.POLLIN);
        ZMQ.PollItem bitem = new ZMQ.PollItem(backend, ZMQ.Poller.POLLIN);

        poller.register(fitem);  //注册pollItem
        poller.register(bitem);

        while (!Thread.currentThread().isInterrupted()) {
            poller.poll();
            if (fitem.isReadable()) {  //表示前面有请求发过来了
                ZMsg msg = ZMsg.recvMsg(fitem.getSocket());  //获取client发送过来的请求，这里router会在实际请求上面套一个连接的标志frame
                requests.addLast(msg);   //将其挂到请求队列
            }
            if (bitem.isReadable()) {  //这里表示worker发送数据过来了
                ZMsg msg = ZMsg.recvMsg(bitem.getSocket());  //获取msg，这里也会在实际发送的数据前面包装一个连接的标志frame
                //这里需要注意，这里返回的是最外面的那个frame，另外它还会将后面的接着的空的标志frame都去掉
                ZFrame workerID = msg.unwrap();  //把外面那层包装取下来，也就是router对连接的标志frame
                workers.addLast(workerID);  //将当前的worker的标志frame放到worker队列里面，表示这个worker可以用了
                ZFrame readyOrAddress = msg.getFirst(); //这里获取标志frame后面的数据，如果worker刚刚启动，那么应该是发送过来的ready，

                if (new String(readyOrAddress.getData()).equals("ready")) {  //表示是worker刚刚启动，发过来的ready
                    msg.destroy();
                } else {
                    msg.send(fronted);  //表示是worker处理完的返回结果，那么返回给客户端
                }
            }

            while (workers.size() > 0 && requests.size() > 0) {
                ZMsg request = requests.removeFirst();
                ZFrame worker = workers.removeFirst();

                request.wrap(worker);  //在request前面包装一层，把可以用的worker的标志frame包装上，这样router就会发给相应的worker的连接
                request.send(backend);  //将这个包装过的消息发送出去
            }
        }
        fronted.close();
        backend.close();
        context.term();
    }
}
