package com.sysnote.core.cluster.modules;

import com.mongodb.BasicDBObject;
import com.sysnote.core.cluster.conf.CoreConf;
import com.sysnote.core.cluster.factory.ModuleFactory;
import com.sysnote.core.cluster.factory.ModuleIntf;
import com.sysnote.core.cluster.zoo.ClusterDic;
import com.sysnote.core.cluster.zoo.ZooKeeperWatchIntf;
import com.sysnote.utils.StringUtil;
import com.sysnote.utils.ZooUtil;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-17.
 */
public class ModuleDic implements ModuleIntf, ZooKeeperWatchIntf {

    private static Logger logger = LoggerFactory.getLogger(ClusterDic.class);

    protected BasicDBObject serverNode = new BasicDBObject();
    private String name = null;


    @Override
    public boolean init(boolean isReload) {
        if (!isReload) {
            name = CoreConf.localName + "@App";
            serverNode.append("id", name + "##" + StringUtil.currentTime());
            serverNode.append("name", name);
            serverNode.append("ip", CoreConf.localIP);
            ClusterDic.self.appWatcher.addWather(this);
        }
        return true;
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
        return true;
    }

    @Override
    public String getId() {
        return serverNode.getString("id");
    }

    @Override
    public void zooNodeChange(ClusterDic.NodeType type, ClusterDic.NodeAction action, String id, String ip) {
        if (this.isAlive()) return;
        //TODO 处理关注节点变化
    }

    @Override
    public void reconnect() {
        registerNode(); //断线重新注册
        logger.info("zoo connection reconnected!");
    }

    public void registerNode() {
        serverNode.append("services", ModuleFactory.loadedModules);
        ZooUtil.setPath(ClusterDic.self.zooClient(), CoreConf.appNodesPrefix +"/"+ this.getId(), serverNode.toString(), CreateMode.EPHEMERAL);
    }
}
