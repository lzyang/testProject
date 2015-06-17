package com.sysnote.core.cluster.factory;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-17.
 */
public interface ModuleIntf {
    public boolean init(boolean isReload);

    public void afterCreate(Object[] params);

    public void start(boolean isReload);

    public void stop();

    public boolean isAlive();

    public String getId();
}
