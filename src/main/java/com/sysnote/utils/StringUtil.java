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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String currentFullTime(){
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
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

    public static String append(Object... strs) {
        if (strs == null || strs.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object str : strs) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static String readSpecial(String str) {
        StringBuilder buder = new StringBuilder();
        String regEx = "[A-Za-z0-9 \\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            buder.append(m.group(0));
        }
        return buder.toString();
    }

    /**
     * 去除特殊字符
     * @param phrase
     * @return
     */
    public static String normalizeString(String phrase){
        if(isEmpty(phrase)){
            return null;
        }
        String halfstr = full2Half(phrase.toLowerCase());
        return removeSpaceEx(halfstr);
    }

    public static String removeSpaceEx(String phrase){
        if(isEmpty(phrase)){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean prespace = true;
        for(int i=0; i<phrase.length(); ++i){
            char c = phrase.charAt(i);
            if(!isNumEn(c) && !isChinese(c)){
                c=32;
            }
            if(c==32){
                if(!prespace){
                    sb.append(c);
                }
                prespace = true;
            }else{
                sb.append(c);
                prespace = false;
            }
        }
        return sb.toString().trim();
    }

    public static boolean isNumEn(char c){
        if(c>='0'&&c<='9' || c>='a'&&c<='z' || c==32){
            return true;
        }
        return false;
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
//            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
//            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                ) {
            return true;
        }
        return false;
    }

    public static  String full2Half(String fullstr) {
        if(isEmpty(fullstr)){
            return null;
        }
        char[] c = fullstr.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] >= 65281 && c[i] <= 65374) {
                c[i] = (char) (c[i] - 65248);
            } else if (c[i] == 12288) {
                c[i] = (char) 32;
            }
        }
        return new String(c);
    }

    public static void main(String[] args) {
        System.out.println(readSpecial("23fsdfds23^&*^%5sdfds"));
    }
}
