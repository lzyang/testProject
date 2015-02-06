package com.sysnote.utils;

import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by root on 15-2-6.
 */
public class StringUtil {
    public static boolean isEmpty(String v) {
        if (StringUtils.isEmpty(v)) {
            return true;
        }
        if (v.equalsIgnoreCase("null") || v.equalsIgnoreCase("undefined")) {
            return true;
        }
        return false;
    }

    /**
     * 获取本机的ip地址
     * @return
     */
    public static String getLocalIP() {
        Enumeration<NetworkInterface> netInterfaces = null;
        String ip = "127.0.0.1";
        ArrayList<BasicDBObject> items = new ArrayList<BasicDBObject>();
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                String name = ni.getName().toLowerCase();
                if (name.startsWith("lo") || name.startsWith("vir") || name.startsWith("vmnet") || name.startsWith("wlan")) {
                    continue;
                }
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ia = ips.nextElement();
                    if (ia instanceof Inet4Address) {
                        if (ia.getHostAddress().toString().startsWith("127")) {
                            continue;
                        } else {
                            ip = ia.getHostAddress();
                            items.add(new BasicDBObject().append("name", name).append("ip", ip));
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        Collections.sort(items, new Comparator<BasicDBObject>() {

            @Override
            public int compare(BasicDBObject o1, BasicDBObject o2) {
                return o1.getString("name").compareToIgnoreCase(o2.getString("name"));
            }
        });
        if (items.size() > 0) {
            ip = items.get(0).getString("ip");
        }
        return ip;
    }

    public static String currentTime(){
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");
        try{
            return df.format(date);
        }catch(Exception e){
            return "";
        }
    }
}
