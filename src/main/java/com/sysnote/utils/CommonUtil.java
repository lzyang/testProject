package com.sysnote.utils;

/**
 * Created by root on 15-3-9.
 */
public class CommonUtil {

    public static double getPointDistance(double x1,double y1,double x2,double y2){
        double distance = Double.MAX_VALUE;

        distance = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));

        return distance;
    }
}
