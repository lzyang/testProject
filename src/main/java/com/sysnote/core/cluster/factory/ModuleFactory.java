package com.sysnote.core.cluster.factory;

import com.mongodb.BasicDBList;
import com.sysnote.core.cluster.conf.CoreConf;
import com.sysnote.utils.EmptyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-17.
 */
public class ModuleFactory {
    public static final Logger logger = LoggerFactory.getLogger(ModuleFactory.class);

    private static HashMap<String, ClassInfo> moduleInfos = new HashMap<String, ClassInfo>();

    public static BasicDBList loadedModules = new BasicDBList();

    /**
     * 支持模块注册
     *
     * @param moduleName
     * @param moduleClass
     */
    public static void registerModule(String moduleName, Class<?> moduleClass) {
        moduleInfos.put(moduleNameBy(moduleName), new ClassInfo(moduleClass));
    }

    /**
     * 支持模块注册
     *
     * @param moduleName
     * @param moduleClass
     * @param params      　　模块初始化参数
     */
    public static void registerModule(String moduleName, Class<?> moduleClass, Object[] params) {
        moduleInfos.put(moduleNameBy(moduleName), new ClassInfo(moduleClass, params, true));
    }

    /**
     * 模块名注册
     *
     * @param name
     * @return
     */
    public static String moduleNameBy(String name) {
        if (CoreConf.DevelopMode == true) {
            return name.toLowerCase() + "@" + CoreConf.localName;
        } else {
            return name.toLowerCase();
        }
    }

    /**
     * 注册load模块列表
     *
     * @param module
     */
    public static void registerLoadModule(String module) {
        registerLoadModule(module, new String[0]);
    }

    /**
     * 注册load模块列表
     *
     * @param module
     * @param ips    　　启动模块ip
     */
    public static void registerLoadModule(String module, String[] ips) {
        if (EmptyUtils.isEmpty(ips) && containsModule(module)) {
            return;
        }
        loadedModules.remove(moduleNameBy(module));
        if (!EmptyUtils.isEmpty(ips)) {
            for (String ip : ips) {
                if (CoreConf.localIP.equalsIgnoreCase(ip)) {
                    logger.info("module [{}] register ips [{}]", new String[]{module, ips.toString()});
                    loadedModules.add(moduleNameBy(module));
                }
            }
            return;
        }
        loadedModules.add(moduleNameBy(module));
    }

    /**
     * 加载模块是否包含此module
     * @param module
     *
     * @return
     */
    public static boolean containsModule(String module) {
        return loadedModules.contains(moduleNameBy(module));
    }

    public static ModuleIntf moduleCreate(String moduleName) {
        ClassInfo info = moduleInfos.get(moduleName);
        return info != null ? info.createModule() : null;
    }

    /**
     * 加载load模块
     */
    public static void loadModules() {
        for (int i = 0; i < loadedModules.size(); i++) {
            String moduleName = String.valueOf(loadedModules.get(i));
            ModuleIntf instance = ModuleFactory.moduleCreate(moduleName);
            if (instance == null) {
                logger.error("module[{}] not be registered.", new String[]{moduleName});
                System.exit(-1);
            }
            logger.info("===>Load Module[{}] Start.", new String[]{moduleName});
            if (instance.init(false) == false) {
                logger.error("module[{}]  init fail", new String[]{moduleName});
                System.exit(-1);
            }
            instance.start(false);
            if (instance.isAlive() == false) {
                logger.error("module[{}]  start fail", new String[]{moduleName});
                System.exit(-1);
            }
            logger.info("===>Load module[{}] Ok.", new String[]{moduleName});
        }
    }
}
