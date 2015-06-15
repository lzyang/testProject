package com.sysnote.core.cluster;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.sysnote.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sysnote.core.cluster.ClusterDic.NodeAction;
import com.sysnote.core.cluster.ClusterDic.NodeType;

import org.apache.zookeeper.Watcher.Event.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morningsun on 15-6-12.
 */
public class ZooWatcher implements Watcher {
    protected static Logger logger = LoggerFactory.getLogger(ZooWatcher.class);
    protected NodeType nodeType = null;
    protected ZooClient zooClient = null;
    protected String prefixPath = "";
    protected Integer processLock = 0;
    protected ZooNodes nodes = new ZooNodes();
    protected ArrayList<ZooKeeperWatchIntf> watchers = new ArrayList<ZooKeeperWatchIntf>();

    public ZooWatcher(NodeType nodeType, String prefixPath, ZooClient zooClient) {
        this.prefixPath = prefixPath;
        this.zooClient = zooClient;
        this.nodeType = nodeType;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event == null) {
            return;
        }
        String path = event.getPath();
        if ((path == null) || !path.startsWith(prefixPath)) {
            return;
        }
        synchronized (processLock) {
            String[] values = StringUtils.split(path, '/');
            String id = values.length >= 2 ? values[1] : "";
            BasicDBObject oServer = null;
            NodeAction action = null;
            if (event.getType() == EventType.NodeCreated) {
                action = NodeAction.add;
                this.addRecord(id);
                oServer = this.byId(id);
            } else if (event.getType() == EventType.NodeDeleted) {
                action = NodeAction.delete;
                oServer = this.byId(id);
                this.removeRecord(id);
            } else if (event.getType() == EventType.NodeDataChanged) {
                action = NodeAction.datachange;
                this.addRecord(id);
                oServer = this.byId(id);
            } else if (event.getType() == EventType.NodeChildrenChanged) {
                action = NodeAction.childchange;
                this.reload();
                this.fireNodeChange(this.nodeType, action, id, "");
            }
            if (oServer != null) {
                this.fireNodeChange(this.nodeType, action, id, oServer.getString("ip"));
            }
        }
    }

    public void addRecord(String id) {
        if (zooClient.isConnected() == false) {
            return;
        }
        try {
            String c = zooClient.getData(this.prefixPath + "/" + id);
            if (c == null) {
                return;
            }
            BasicDBObject record = (BasicDBObject) JSON.parse(c);
            nodes.add(record);
        } catch (Exception e) {
            logger.error("ServerNodeListener", e);
        }
    }

    public BasicDBObject byId(String id) {
        return nodes.byId(id);
    }

    public void removeRecord(String id) {
        this.nodes.remove(id);
    }

    public void reload() {
        if (zooClient.isConnected() == false) return;
        if (zooClient.exists(prefixPath) == false) return;

        try {
            String timestamp = StringUtil.currentTime();
            List<String> items = zooClient.getChildrens(prefixPath);
            for (int i = 0; items != null && i < items.size(); i++) {
                String id = items.get(i);
                String c = zooClient.getData(this.prefixPath + "/" + id);
                if (c == null) {
                    continue;
                }
                BasicDBObject record = (BasicDBObject) JSON.parse(c);
                record.append("timestamp", timestamp);
                nodes.add(record);
            }
            int index = 0;
            while (index < nodes.records().size()) {
                BasicDBObject record = (BasicDBObject) nodes.records().get(index);
                if (record.getString("timestamp", "").equalsIgnoreCase(timestamp)) {
                    index++;
                    continue;
                }
                if (nodes.remove(record.getString("id")) == false) {
                    index++;
                }
            }
        } catch (Exception e) {
            logger.error("ZooWatcher", e);
        }
    }

    public void fireNodeChange(NodeType type, NodeAction action, String id, String ip) {
        for (int i = 0; i < watchers.size(); i++) {
            watchers.get(i).zooNodeChange(type, action, id, ip);
        }
    }
}
