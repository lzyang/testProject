package com.sysnote.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Morningsun(515190653@qq.com) on 15-7-22.
 */
public class LogUtil {

    private static Logger testLog = LoggerFactory.getLogger("TestLog");

    public static void printLog(String content){
        StringBuffer sbf = new StringBuffer();
        sbf.append(content);
        testLog.info(sbf.toString());
    }
}
