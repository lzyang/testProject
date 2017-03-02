package com.sysnote.utils;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.dat.Item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 17-2-17.
 */
public class QueryParser {

    private static String dealSpecial(String query){
        return query.replaceAll("[)(-/）（:【】，*！、《》]"," ");
    }

    public static String parse(String query){
        query = dealSpecial(query);
//        List<Term> parse = ToAnalysis.parse(query);
        List<Term> parse = IndexAnalysis.parse(query);
        Iterator<Term> it = parse.iterator();
        StringBuffer sb = new StringBuffer();
        int count = 0;
        while (it.hasNext()){
            Term t = it.next();
            String token = t.getName().trim();
            if(sb.lastIndexOf(token)>=0||token.length()==0) continue;
            if(count++>0)
                sb.append(" ").append(token);
            else{
                sb.append(token);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(parse("三星  samsung 原装 充电器 通用 三星 头 数据 线 三星 插头 三星 三星 手机 直 "));
    }
}
