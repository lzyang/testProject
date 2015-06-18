package com.sysnote.core.cluster.factory;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-17.
 */
public interface ModuleIntf {
    /**
     * 初始化模块
     * @param isReload　是否是首次加载
     * @return
     */
    public boolean init(boolean isReload);

    public void afterCreate(Object[] params);

    public void start(boolean isReload);

    public void stop();

    public boolean isAlive();

    public String getId();
}
