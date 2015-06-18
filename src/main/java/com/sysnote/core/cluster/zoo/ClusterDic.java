package com.sysnote.core.cluster.zoo;

import com.mongodb.BasicDBList;
import com.sysnote.core.cluster.conf.CoreConf;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by root on 15-6-12.
 */
public class ClusterDic {

    public static enum NodeAction {
        add, delete, datachange, childchange
    };

    public static enum NodeType {
        app
    };

    public static ClusterDic self = null;
    private static Logger logger = LoggerFactory.getLogger(ClusterDic.class);
    private ZooClient zooClient = null;
    public ZooWatcher appWatcher = null;

    static {
        self = new ClusterDic();
    }

    private ClusterDic() {
        init();
    }

    private boolean init() {
        logger.info("ClusterDict init start...");
        zooClient = new ZooClient();

        if(!zooClient.init()){
            return false;
        }

        appWatcher = new ZooWatcher(NodeType.app, CoreConf.appNodesPrefix, zooClient);
        zooClient.add(appWatcher);
        appWatcher.reload();

        logger.info("ClusterDict init success.");
        return true;
    }


    public BasicDBList appNodes(){
        return appWatcher.nodes.records();
    }

    public CuratorFramework getClient(){
        return this.zooClient.framework;
    }
}
