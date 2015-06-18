package com.sysnote.core.app;

import com.sysnote.core.cluster.factory.ModuleFactory;
import com.sysnote.core.cluster.modules.ModuleDic;
import com.sysnote.core.cluster.modules.ModuleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-17.
 */
public class App {

    public static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void init(){
        ModuleFactory.registerModule("clusterDict", ModuleDic.class);
        ModuleFactory.registerModule("queue", ModuleQueue.class);
    }

    public static void start() {
        init();
        long start = System.currentTimeMillis();
        logger.info("java.library.path={}", new Object[] { System.getProperty("java.library.path") });

        ModuleFactory.registerLoadModule("queue");
        ModuleFactory.registerLoadModule("clusterDict");
        ModuleFactory.loadModules();
        logger.info("All module started success in {} ms.", System.currentTimeMillis() - start);
    }

    public static void main(String[] args) {

        if (args == null || args.length == 0 || args[0].equals("start")) {
            start();
        } else if (args[0].equals("stop")) {
            //TODO stop All module
            System.exit(-1);
        }
    }
}
