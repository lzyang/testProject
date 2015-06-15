package com.sysnote.core.cluster;

import com.sysnote.utils.ZooUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morningsun on 15-6-12.
 */
public class ZooClient implements CuratorWatcher, CuratorListener, ConnectionStateListener {

    protected static Logger logger = LoggerFactory.getLogger(ZooClient.class);
    public CuratorFramework framework = null;
    public ArrayList<ZooWatcher> watchers = new ArrayList<ZooWatcher>();

    public ZooClient() {
        this.framework = ZooUtil.create();
    }

    public boolean init() {
        try {
            this.framework.getConnectionStateListenable().addListener(this); //ConnectionStateListener
            this.framework.getCuratorListenable().addListener(this); //CuratorListener
            this.framework.start();
            this.framework.getZookeeperClient().blockUntilConnectedOrTimedOut();
            return true;
        } catch (Exception e) {
            logger.error("start",e);
            return false;
        }
    }

    //implements CuratorWatcher
    @Override
    public void process(WatchedEvent event) throws Exception {
        if (event == null || event.getPath() == null) {
            return;
        }
        fireEvents(event);
    }

    //implements CuratorListener
    @Override
    public void eventReceived(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {

    }

    //implements ConnectionStateListener
    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

    }

    private void fireEvents(WatchedEvent e) {
        for (int i = 0; i < watchers.size(); i++) {
            ZooWatcher w = watchers.get(i);
            w.process(e);
        }
    }

    public boolean isConnected() {
        return this.framework.getZookeeperClient().isConnected();
    }

    public String getData(String path) throws Exception {
        return ZooUtil.getData(framework, path, this);
    }

    public boolean exists(String path){
        return ZooUtil.exists(framework,path,this);
    }

    public List<String> getChildrens(String path) throws Exception {
        if (ZooUtil.exists(framework, path, this) == false) {
            return null;
        }
        return ZooUtil.getChilds(framework, path, this);
    }
}
