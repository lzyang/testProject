package com.sysnote.core.cluster;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-12.
 */
public class ZooNodes {
    private BasicDBList records = new BasicDBList();  //所有的data节点数据集合
    private HashMap<String, BasicDBObject> recordsById = new HashMap<String, BasicDBObject>();  //以ID为key的节点数据集合
    private HashMap<String, BasicDBList> recordsByTag = new HashMap<String, BasicDBList>();  //以Tag为key的节点数据集合
    private HashSet<String> tags = new HashSet<String>();
    private ConcurrentHashMap<String, AtomicLong> counter = new ConcurrentHashMap<String, AtomicLong>();


    /**
     * @param record zookeeper 节点Data数据
     */
    public synchronized void add(BasicDBObject record) {
        String id = record.getString("id");
        int index = this.findIndex(id, records);
        if (index == -1) {
            records.add(record);
        } else {
            records.set(index, record);
        }
        recordsById.put(id, record);
        if (!record.containsField("tags")) {
            return;
        }
        BasicDBList recordTags = (BasicDBList) record.get("tags");
        for (int i = 0; recordTags != null && i < recordTags.size(); i++) {
            String tag = recordTags.get(i).toString();
            tags.add(tag);
            BasicDBList items = recordsByTag.get(tag);
            if (items == null) {
                items = new BasicDBList();
                recordsByTag.put(tag, items);
            }
            index = this.findIndex(id, items);
            if (index == -1) {
                items.add(record);
            } else {
                items.set(index, record);
            }
        }
    }

    /**
     * 查找包含id的Json对象，返回在BasicDBList中的index
     * @param id
     * @param items
     * @return
     */
    public synchronized int findIndex(String id, BasicDBList items) {
        int result = -1;
        for (int i = 0; i < items.size(); i++) {
            BasicDBObject oItem = (BasicDBObject) items.get(i);
            if (oItem.getString("id").equalsIgnoreCase(id)) {
                result = i;
            }
        }
        return result;
    }

    public synchronized BasicDBObject byId(String id) {
        return recordsById.get(id);
    }
}
