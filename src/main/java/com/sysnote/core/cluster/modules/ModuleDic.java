package com.sysnote.core.cluster.modules;

import com.sysnote.core.cluster.factory.ModuleIntf;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-17.
 */
public class ModuleDic implements ModuleIntf{

    @Override
    public boolean init(boolean isReload) {
        return false;
    }

    @Override
    public void afterCreate(Object[] params) {

    }

    @Override
    public void start(boolean isReload) {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public String getId() {
        return null;
    }
}
