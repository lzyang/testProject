package com.sysnote.core.cluster.factory;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-17.
 */
class ClassInfo {
    public Class<?> cls = null;
    public Object instance = null;
    public Object[] params = null;
    public boolean keepInstance = true;

    public ClassInfo(Class<?> cls) {
        this(cls, null, true);
    }

    public ClassInfo(Class<?> cls, Object[] params, boolean keepInstance) {
        this.cls = cls;
        this.params = params;
        this.keepInstance = keepInstance;
    }

    public ModuleIntf createModule() {
        try {
            instance = cls.newInstance();
            if (params != null) {
                ((ModuleIntf) instance).afterCreate(params);
            }
        } catch (Exception e) {
            ModuleFactory.logger.error("create Module", e);
            instance = null;
        }
        return (ModuleIntf) instance;
    }
}
