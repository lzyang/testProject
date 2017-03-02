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
            if(sb.indexOf(t.getName())>0) continue;
            if(count++>0)
                sb.append(t.getName()).append(" ");
            else{
                sb.append(t.getName());
            }
        }
        return sb.toString();
    }
}
