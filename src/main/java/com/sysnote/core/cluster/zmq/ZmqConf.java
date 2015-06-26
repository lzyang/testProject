package com.sysnote.core.cluster.zmq;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-26.
 */
public class ZmqConf {
    public static final String balance_req_router = "ipc://req";
    public static final String balance_worker_router = "ipc://worker";

    public static final int balance_worker_pool_min = 1;
    public static final int balance_worker_pool_max = 3;
}
