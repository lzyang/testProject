package com.sysnote.core.cluster.conf;

import com.sysnote.utils.StringUtil;

/**
 * Created by root on 15-6-12.
 */
public class CoreConf {
    public static final int ZooPort = 51111;
    public  static final int ZooTimeout = 60*1000*1000;


    public static final String ZooIP = "127.0.0.1";

    public static final String appNodesPrefix = "/nodes";

    public static final boolean DevelopMode = true;
    public static final String localName = StringUtil.getLocalName();
    public static final String localIP = StringUtil.getLocalIP();
    public static final int LevelDB_BatchUpdate = 90;
    public static final int LevelDB_BatchDelete = 30;
}
