package com.sysnote.utils;

import com.mongodb.BasicDBObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
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

    /**
     * 获取请求地址
     * @param request
     * @return
     */
    public static String getRemoteIP(HttpServletRequest request) {
        Integer NginxIP = 0;
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_client_ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip != null) {
            if (ip.indexOf(",") != -1) {
                String[] values = ip.split("\\,");
                ip = values[(NginxIP).intValue()].trim();
            } else if (ip.indexOf(":") != -1) {
                ip = "127.0.0.1";
            }
        }
        return ip;
    }

    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, "utf-8");
        } catch (Exception e) {
            return new String(bytes);
        }
    }

    public static byte[] toBytes(String content) {
        try {
            return content.getBytes("utf-8");
        } catch (Exception e) {
            return content.getBytes();
        }
    }

    public static String getLocalName() {
        try {
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec("hostname");
            StringWriter writer = new StringWriter();
            IOUtils.copy(proc.getInputStream(), writer, "utf-8");
            String name = StringUtil.trim(writer.toString());
            return name;
        } catch (Exception e) {
            return "unknow";
        }
    }

    /**
     * 替换所有空格回车等字符
     * @param text
     * @return
     */
    public static String trim(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        final StringBuilder buffer = new StringBuilder(text.length());
        for (final char ch : text.toCharArray()) {
            if (ch != (char) 160 && ch != '\t' && ch != '\n' && ch != '\r' && ch != ' ') {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }
}
