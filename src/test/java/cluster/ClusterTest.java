package cluster;

import com.mongodb.BasicDBObject;
import com.sysnote.core.cluster.zoo.ClusterDic;
import com.sysnote.core.cluster.conf.CoreConf;
import com.sysnote.utils.StringUtil;
import com.sysnote.utils.ZooUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-17.
 */
public class ClusterTest {
    @Test
    public void appStart(){
        bootstrap("app5");
    }

    public void bootstrap(String nodeId){
        CuratorFramework framework = ClusterDic.self.zooClient();
        BasicDBObject node = new BasicDBObject();
        node.append("id",nodeId);
        node.append("ip", StringUtil.getLocalIP());
        node.append("s",StringUtil.currentTime());
        ZooUtil.setPath(framework, CoreConf.appNodesPrefix + "/" + nodeId, node.toString(), CreateMode.EPHEMERAL);
        while (true){

        }
    }
}
