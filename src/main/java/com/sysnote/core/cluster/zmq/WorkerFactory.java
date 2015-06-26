package com.sysnote.core.cluster.zmq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-26.
 */
public class WorkerFactory implements Runnable{

    private ExecutorService threadPool = null;

    public WorkerFactory(){
        //threadPool = new ThreadPoolExecutor(ZmqConf.balance_worker_pool_min,ZmqConf.balance_worker_pool_max,10, TimeUnit.SECONDS,);
    }

    @Override
    public void run() {

    }
}
