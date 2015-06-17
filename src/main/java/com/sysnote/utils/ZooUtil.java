package com.sysnote.utils;

import com.sysnote.core.cluster.CoreConf;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by root on 15-6-12.
 */
public class ZooUtil {

    protected static Logger logger = LoggerFactory.getLogger(ZooUtil.class);

    public static CuratorFramework create() {
        StringBuilder buffer = new StringBuilder();
        String[] values = CoreConf.ZooIP.split("\\,");
        for (int i = 0; i < values.length; i++) {
            String ip = values[i];
            if (StringUtils.isEmpty(ip)) {
                continue;
            }
            if (buffer.length() > 0) {
                buffer.append(",");
            }
            if (ip.indexOf(":") <= 0) {
                buffer.append(ip);
                buffer.append(":");
                buffer.append(CoreConf.ZooPort);
            } else {
                buffer.append(ip);
            }
        }
        RetryNTimes retryPolicy = new RetryNTimes(5, 5000);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(buffer.toString()).retryPolicy(retryPolicy)
                .connectionTimeoutMs(CoreConf.ZooTimeout).sessionTimeoutMs(CoreConf.ZooTimeout * 3).build();
        return client;
    }

    public static String getData(CuratorFramework client, String path) {
        return getData(client, path, null);
    }

    public static String getData(CuratorFramework client, String path, CuratorWatcher watcher) {
        try {
            if(client.checkExists().forPath(path) == null){
                return null;
            }
            if (watcher != null) {
                return StringUtil.toString(client.getData().usingWatcher(watcher).forPath(path));
            } else {
                return StringUtil.toString(client.getData().forPath(path));
            }
        } catch (Exception e) {
            logger.error("getData", e);
            return null;
        }
    }

    public static boolean exists(CuratorFramework client, String path, CuratorWatcher watcher) {
        try {
            if (watcher != null) {
                return client.checkExists().usingWatcher(watcher).forPath(path) != null;
            } else {
                return client.checkExists().forPath(path) != null;
            }
        } catch (Exception e) {
            logger.error("exists", e);
            return false;
        }
    }

    public static boolean exists(CuratorFramework client, String path) {
        return exists(client, path, null);
    }

    public static List<String> getChilds(CuratorFramework client, String path) {
        return getChilds(client, path, null);
    }

    public static List<String> getChilds(CuratorFramework client, String path, CuratorWatcher watcher) {
        try {
            if (watcher != null) {
                return client.getChildren().usingWatcher(watcher).forPath(path);
            } else {
                return client.getChildren().forPath(path);
            }
        } catch (Exception e) {
            logger.error("getChilds", e);
            return null;
        }
    }

    public static void setPath(CuratorFramework client, String path, String content, CreateMode mode) {
        try {
            if (client.checkExists().forPath(path) == null) {
                client.create().withMode(mode).forPath(path, StringUtil.toBytes(content));
            } else {
                client.setData().forPath(path, StringUtil.toBytes(content));
            }
        } catch (Exception e) {
            logger.error("setPath", e);
        }
    }
}
