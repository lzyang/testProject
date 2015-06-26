package com.sysnote.core.cluster.zmq;

import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.LinkedList;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-26.
 */
public class Balance {
    private LinkedList<ZFrame> workers;
    private LinkedList<ZMsg> requests;
    private ZMQ.Context context;
    private ZMQ.Poller poller;

    public static Balance self = null;

    static {
        self = new Balance();
    }

    private Balance() {
        this.workers = new LinkedList<ZFrame>();
        this.requests = new LinkedList<ZMsg>();
        this.context = ZMQ.context(1);
        this.poller = new ZMQ.Poller(2);
    }

    public boolean start(){
        ZMQ.Socket reqRouter = this.context.socket(ZMQ.ROUTER);
        ZMQ.Socket workerRouter = this.context.socket(ZMQ.ROUTER);
        reqRouter.bind(ZmqConf.balance_req_router);
        workerRouter.bind(ZmqConf.balance_worker_router);

        ZMQ.PollItem reqItem = new ZMQ.PollItem(reqRouter, ZMQ.Poller.POLLIN);
        ZMQ.PollItem workerItem = new ZMQ.PollItem(workerRouter, ZMQ.Poller.POLLIN);

        poller.register(reqItem);
        poller.register(workerItem);

        while (!Thread.currentThread().isInterrupted()){
            poller.poll();
            if(reqItem.isReadable()){
                ZMsg msg = ZMsg.recvMsg(reqItem.getSocket());
                requests.add(msg);
            }
            if(workerItem.isReadable()){
                ZMsg msg = ZMsg.recvMsg(workerItem.getSocket());
                ZFrame workerID = msg.unwrap();
                workers.add(workerID);
                ZFrame readyOrAddress = msg.getFirst();

                if(new String(readyOrAddress.getData()).equals("ready")){
                    msg.destroy();
                }else{
                    msg.send(reqRouter);
                }
            }

            while (workers.size()>0&&requests.size()>0){
                ZMsg request = requests.removeFirst();
                ZFrame worker = workers.removeFirst();

                request.wrap(worker);
                request.send(workerRouter);
            }
        }
        reqRouter.close();
        workerRouter.close();
        context.term();
        return true;
    }
}
