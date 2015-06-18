package com.sysnote.core.cluster.modules;

import com.mongodb.BasicDBObject;
import com.sysnote.core.cluster.factory.ModuleIntf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-18.
 */
public class ModuleQueue implements ModuleIntf,Runnable{

    protected static Logger logger = LoggerFactory.getLogger(ModuleQueue.class);
    private boolean isAlive = false;
    public ArrayBlockingQueue<BasicDBObject> queue = new ArrayBlockingQueue<BasicDBObject>(1000);
    private Thread t = null;
    private ExecutorService threadPool = null;

    @Override
    public boolean init(boolean isReload) {
        threadPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
        return true;
    }

    @Override
    public void afterCreate(Object[] params) {

    }

    @Override
    public void start(boolean isReload) {
        isAlive = true;
        t = new Thread(this);
        t.start();
        logger.info("ModuleQueue start success!");
    }

    @Override
    public void stop() {
        isAlive = false;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public String getId() {
        return "queue";
    }

    @Override
    public void run() {
        while (!t.isInterrupted()&&isAlive){
            if(queue.size()==0) continue;

            BasicDBObject msg = queue.poll();
            int msgType = msg.getInt("type");
            switch (msgType){
                case 1:
                    threadPool.execute(new Dealer());
                    break;
                case 2:
                    threadPool.execute(new Dealer());
                    break;
            }
        }
    }


    class Dealer implements Runnable{

        @Override
        public void run() {
            while (true){

            }
        }
    }
}
