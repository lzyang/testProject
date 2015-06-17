package com.sysnote.core.cluster.factory;

import com.sysnote.core.cluster.conf.CoreConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-17.
 */
public class ModuleFactory {
    public static final Logger logger = LoggerFactory.getLogger(ModuleFactory.class);

    private static HashMap<String, ClassInfo> moduleInfos = new HashMap<String, ClassInfo>();

    public static void registerModule(String moduleName, Class<?> moduleClass) {
        moduleInfos.put(moduleNameBy(moduleName), new ClassInfo(moduleClass));
    }

    public static String moduleNameBy(String name) {
        if (CoreConf.DevelopMode == true) {
            return name.toLowerCase() + "@" + CoreConf.localName;
        } else {
            return name.toLowerCase();
        }
    }
}
