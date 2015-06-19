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

    public static void init() {
        ModuleFactory.registerModule("appDict", ModuleDic.class);
        ModuleFactory.registerModule("queue", ModuleQueue.class);
    }

    public static void start() {
        init();
        logger.info("java.library.path={}", new Object[]{System.getProperty("java.library.path")});
        ModuleFactory.registerLoadModule("appDict");
        ModuleFactory.registerLoadModule("queue");
        ModuleFactory.loadModules();
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        if (args == null || args.length == 0 || args[0].equals("start")) {
            start();
            ModuleDic dic = (ModuleDic) ModuleFactory.dic();
            dic.registerNode();
            logger.info("All module started success in {} ms.", System.currentTimeMillis() - start);
        } else if (args[0].equals("stop")) {
            //TODO stop All module
            System.exit(-1);
        }
    }
}
