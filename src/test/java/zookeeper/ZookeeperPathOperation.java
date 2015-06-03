package zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 15-6-3.
 */
public class ZookeeperPathOperation {

    public CuratorFramework create() {
        String zkServers = "10.58.50.103:19750,10.58.50.104:19750";
        RetryNTimes retryNTimes = new RetryNTimes(5, 3000);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zkServers)
                .retryPolicy(retryNTimes)
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(15000)
                .build();
        return client;
    }

    public boolean checkExists(CuratorFramework client, String path) {
        try {
            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getChildren(CuratorFramework client, String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<String>();
        }
    }

    public String getData(CuratorFramework clent, String path) {
        byte[] data = null;
        try {
            data = clent.getData().forPath(path);
            if (data != null) {
                return new String(data, "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void printAllNodes(CuratorFramework client, String path) {
        if (!checkExists(client, path)) return;
        List<String> children = getChildren(client, path);
        String data = getData(client, path);
        if (children.size() == 0) {
            System.out.print(path + ">>>");
            System.out.println(data);
        }
        for (Iterator it = children.iterator(); it.hasNext(); ) {
            String childPath = (String) it.next();
            if (path != "/") {
                printAllNodes(client, path + "/" + childPath);
            } else {
                printAllNodes(client, path + childPath);
            }
        }
    }

    @Test
    public void getAllNodes() {
        CuratorFramework client = create();
        client.start();
        printAllNodes(client, "/");
        client.close();
    }
}
