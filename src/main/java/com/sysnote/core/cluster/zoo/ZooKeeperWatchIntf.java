package com.sysnote.core.cluster.zoo;

import com.sysnote.core.cluster.zoo.ClusterDic.NodeAction;
import com.sysnote.core.cluster.zoo.ClusterDic.NodeType;
/**
 * Created by Morningsun(515190653@qq.com) on 15-6-15.
 */
public interface ZooKeeperWatchIntf {
    public void zooNodeChange(NodeType type, NodeAction action, String id, String ip);
    public void reconnect();
}
