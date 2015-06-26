package com.sysnote.core.cluster.zmq;

import com.sysnote.core.cluster.factory.ModuleIntf;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-24.
 */
public class ModuleZeroMq implements ModuleIntf{

    private boolean isAlive = false;

    private boolean initSocket(){

        return true;
    }

    @Override
    public boolean init(boolean isReload) {
        if(!initSocket()) return false;
        return false;
    }

    @Override
    public void afterCreate(Object[] params) {

    }

    @Override
    public void start(boolean isReload) {
        isAlive = true;
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
        return null;
    }
}
