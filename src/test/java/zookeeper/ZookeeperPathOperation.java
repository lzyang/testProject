package zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 15-6-3.
 */
public class ZookeeperPathOperation {

    public CuratorFramework create() {
        String zkServers = "127.0.0.1:51111";
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

    /**
     * path>>>value
     */
    public void importZooData(){
        CuratorFramework client = create();
        client.start();
        startSet(client,"/workspace/dev/envbak/139_zoo111.text");
        client.close();
    }

    private String[] startSet(CuratorFramework client,String filePath){
        String [] conf = new String[2];
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(filePath),"utf-8");
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            while ((line = br.readLine()) != null){
                conf = line.split(">>>");
                if(conf.length==2){
                    //System.out.println(conf[0]+"____"+conf[1]);
                    createPath(client,conf[0],conf[1],CreateMode.PERSISTENT);
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conf;
    }

    private void createPath(CuratorFramework client,String path,String data,CreateMode mode){
        try {
            if(client.checkExists().forPath(path)==null){
                client.create().withMode(mode).forPath(path,data.getBytes("utf-8"));
            }else{
                client.setData().forPath(path,data.getBytes("utf-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTest(){
        CuratorFramework client = create();
        client.start();
        deletePath(client, "/schedulerLeader");
        client.close();
    }

    private void deletePath(CuratorFramework client,String path){
        try {
            client.delete().guaranteed().forPath(path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
