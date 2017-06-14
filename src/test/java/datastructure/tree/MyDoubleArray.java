package datastructure.tree;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.sysnote.utils.MFileUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by root on 17-6-14.
 */
public class MyDoubleArray {

    private final String ENCODE = "utf-8";

    private int maxPrefixMatch = 10;   //最大匹配长度
    private int resultCount = 15;   //最大匹配长度
    private DANode[] nodeindex = null;

    private List<HashMap<String, Set<LineInfo>>> prefixLineDic = new ArrayList<HashMap<String, Set<LineInfo>>>();//[<手,["手机","手机壳"]>,<手机,[手机壳,手机套]>      <手机壳,[手机壳]>],[<手,["手机","手机壳"]>,<手机,[三星手机壳,手机套]>,<手机壳,[三星手机壳]>]
    private HashMap<String, Set<Character>> prefix2NextChar = new HashMap<String, Set<Character>>();//每个搜索字符的后面一个字符<手,[机,链,套...]>

    private int[] baseindex = null;
    private int[] checkindex = null;
    private int cursor = 0;  //语料中最大的unicode码,以及双数组构建游标


    private List<LineInfo> inverttable = new ArrayList<LineInfo>();
    private HashMap<Integer, DANode> indexDic = new HashMap<Integer, DANode>();  //记录base下标所对应词信息在inverttable中的开始与结束位置

    private class DANode {
        public int start = 0;
        public int end = 0;
        public DANode(int s, int e){
            start = s;
            end = e;
        }
    }

    /**
     * 组装中间结果，原始line，生成初始化wordFromIndex,hsinverts
     * @param trieLine
     * @param li
     */
    public void insertQuery(String trieLine, LineInfo li){
        int t = trieLine.charAt(0);
        if(t>cursor){   //查找字符最大的unicode
            cursor = t+1;
        }
        String subword = "";
        for(int i=0; i<trieLine.length() && i<maxPrefixMatch; ++i){
            subword += trieLine.charAt(i);
            int j = i+1;
            if(j<trieLine.length()){
                if(prefix2NextChar.containsKey(subword)){    //<"手机",[壳,卡]>
                    prefix2NextChar.get(subword).add(trieLine.charAt(j));
                }else{
                    Set<Character> set = new TreeSet<Character>();
                    set.add(trieLine.charAt(j));
                    prefix2NextChar.put(subword, set);
                }
            }
            if(prefixLineDic.get(i).containsKey(subword)){
                if(prefixLineDic.get(i).get(subword).size()<resultCount){//限定结果词数，基于cleandata已经根据搜索次数排序的时候
                    prefixLineDic.get(i).get(subword).add(li);
                }
                continue;
            }
            Set<LineInfo> set = new TreeSet<LineInfo>();
            set.add(li);
            prefixLineDic.get(i).put(subword, set);
        }
    }

    /**
     * 加载数据并生成中间数据
     * @param filename
     * @return
     */
    public boolean loadCleanData(String filename){
        for(int i=0; i<maxPrefixMatch; ++i){  //初始化前缀词典
            HashMap<String, Set<LineInfo>> invert = new HashMap<String, Set<LineInfo>>();
            prefixLineDic.add(invert);
        }

        checkindex = new int[1000000];
        baseindex = new int[1000000];

        HashSet<String> handledLine = new HashSet<String>();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8") );
            String lineSource = null;
            String line = null;

            while ( (lineSource = reader.readLine()) != null){
                lineSource = lineSource.trim();
                String[] s = lineSource.split(":");
                //TODO    过滤非法字符
                line = s[0];
                if(line==null || line.length()==0){
                    continue;
                }
                LineInfo li = new LineInfo(line, Integer.parseInt(s[1]));
                handledLine.add(line);
                insertQuery(line, li);		//组装前缀数据   prefixLineDic  prefix2NextChar

                for(int i=1; i<line.length(); ++i){
                    if(handledLine.contains(line.substring(i, line.length()))){   //中缀   如果query后缀成词
                        insertQuery(line.substring(i, line.length()), li);
                    }
                }
            }
            System.out.println("max header:" + cursor);
        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 检查将要存的字符超出了数组范围
     * @param t
     */
    public void checkSize(int t){
        if(t>=baseindex.length){
            baseindex = Arrays.copyOf(baseindex, baseindex.length*2);
            checkindex = Arrays.copyOf(checkindex, checkindex.length*2);
        }
    }

    public boolean checkExist(int base, Set<Character> set){
        boolean isExist = false;
        int i = base;
        for(int c : set){
            int t = base + c;  //?
            checkSize(t);
            if(baseindex[++i]!=0 || baseindex[t]!=0 || checkindex[t]!=base){
                isExist = true;
                break;
            }
        }
        return isExist;
    }


    public void resetBase(int t, Set<Character> set){
        int i = cursor+1;
        int base = cursor+1;
        boolean isbreak = true;
        while(i<baseindex.length){
            for(int c : set){  //当base为cursor+1时,检查set字符是否冲突
                int n = base + c;
                checkSize(n);
                if(baseindex[++i]==0 && baseindex[n]==0 && checkindex[n]==0){  //cursor后第二位为可用??
                    continue;
                }
                isbreak = false;  //如果有一个字符冲突
                break;
            }
            if(isbreak){
                break;
            }
            isbreak = true;
            while(baseindex[i]!=0){  //寻找新的base位
                i++;
            }
            base = i;
        }
        baseindex[t] = base;   //base[t]的最小偏移量为cursor?为什么
        i = base;
        for(int c : set){
            baseindex[base+c] = ++i;
            checkindex[t] = base;
        }
        while(baseindex[i]!=0){
            i++;
        }
        cursor = i;
    }

    /**
     * 插入首字符和第二个字符
     * @param query
     * @param set
     * @return  返回当前字符的unicodeId
     */
    public int insertHeader(String query, Set<Character> set){
        int t = query.charAt(0);
        int id = t;  //当前字符unicode
        if(set==null){
            return 0;
        }
        int base = cursor;
        baseindex[t] = cursor;
        int i = cursor;
        boolean isExist = checkExist(base, set);  //set中和当前base arr中有冲突
        if(isExist){   //当冲突时
            resetBase(t, set);
        }else{     //不冲突时
            for(int c : set){
                t = base + c;
                baseindex[t] = ++i;
                checkindex[t] = base;
            }
            while(baseindex[i]!=0){
                i++;
            }
            cursor = i;
        }
        return id;
    }

    /**
     * 插入双数组开头
     */
    public void insertHeader(){
        int id = 0;
        HashMap<String, Set<LineInfo>> hash = prefixLineDic.get(0);  //获取所有词的首字符为key的queryInfo
        for(Map.Entry<String,Set<LineInfo>> entry : hash.entrySet()){
            String key = entry.getKey();
            id = insertHeader(key, prefix2NextChar.get(key));  //将首字符和次字符插入双数组
            Set<LineInfo> set = hash.get(key);
            int invertpos = inverttable.size();
            for(LineInfo s : set){
                inverttable.add(s);
            }
            indexDic.put(id, new DANode(invertpos, inverttable.size()));
        }
    }

    public int getPrefix(String query){
        int t = query.charAt(0);
        int base = baseindex[t];
        if(base==0){
            System.out.println(query + ":[" + 0 +   "]  " + query.charAt(0) + " not exist!");
            return -1;
        }
        int len = query.length();
        for(int i=1; i<len; ++i){
            t = base + query.charAt(i);
            if(t>=baseindex.length){
                System.out.println(query + " base==0");
                return -1;
            }
            base = baseindex[t];
            if(base==0 && i==len-1){
                System.out.println(query + "  > base==0");
                return -1;
            }
        }
        return t;
    }

    public int  insertSuffix(String query, Set<Character> set){
        int t = getPrefix(query);
        int id = t;
        if(t==-1 || set==null){
            System.out.println(query + "t==-1 base==0");
            return 0;
        }
        int base = baseindex[t];
        int i = base;
        boolean isExist = checkExist(base, set);
        if(isExist){
            resetBase(t, set);
        }else{
            for(int c : set){
                t = base + c;
                baseindex[t] = ++i;
                checkindex[t] = base;
            }
            while(baseindex[i]!=0){
                i++;
            }
            cursor = i;
        }
        return id;
    }

    /**
     * 插入双数组后续
     */
    public  void insertSuffix(){
        int id = 0;
        for(int i=1; i<prefixLineDic.size(); ++i){
            HashMap<String, Set<LineInfo>> hash = prefixLineDic.get(i);
            for(Map.Entry<String,Set<LineInfo>> entry : hash.entrySet()){
                String key = entry.getKey();
                if(prefix2NextChar.get(key)==null){  //当前前缀即为完整的搜索词，或者拼音等
                    id = getPrefix(key);
                }else{
                    id = insertSuffix(key, prefix2NextChar.get(key));
                }
                if(key.equals("长裙")){
                    System.out.println("长裙" + ":id:" + id);
                }
                Set<LineInfo> set = hash.get(key);
                int invertpos = inverttable.size();
                for(LineInfo s : set){
                    inverttable.add(s);
                }
                indexDic.put(id, new DANode(invertpos, inverttable.size()));
            }
        }
        System.out.println("cursor:" + cursor);
        System.out.println("invert.size:" + inverttable.size());
        System.out.println("baseindex.size:" + baseindex.length);
    }

    public void build(){
        insertHeader();
        insertSuffix();
    }


    public void save(String filename, int type){
        try{
            FileOutputStream out = new FileOutputStream(filename);
            switch(type){
                case 1:
                    out.write((baseindex.length+"\n").getBytes(ENCODE));
                    break;
                case 2:
                    for(int i=0; i<baseindex.length; ++i){
                        if(baseindex[i]==0){
                            continue;
                        }
                        String tmp = i + "\t" + baseindex[i] + "\n";
                        out.write(tmp.getBytes(ENCODE));
                    }
                    break;
                case 3:
                    for(int i=0; i<checkindex.length; ++i){
                        if(checkindex[i]==0){
                            continue;
                        }
                        String tmp = i + "\t" + checkindex[i] + "\n";
                        out.write(tmp.getBytes(ENCODE));
                    }

                    break;
                case 4:
                    for(Map.Entry<Integer, DANode> entry : indexDic.entrySet()){
                        int i = entry.getKey();
                        DANode da = entry.getValue();
                        String tmp = i + "\t" + da.start  + "\t" + da.end + "\n";
                        out.write(tmp.getBytes(ENCODE));
                    }
                    break;
                case 5:
                    for(LineInfo r : inverttable){
                        String tmp = r.line+":"+r.weight;
                        tmp += "\n";
                        out.write(tmp.getBytes(ENCODE));
                    }
                    break;
            }

            out.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void save(String dir){
        save(dir + "size.dat", 1);
        save(dir + "base.dat", 2);
        save(dir + "check.dat", 3);
        save(dir + "node.dat", 4);
        save(dir + "invert.dat", 5);
    }


    public void load(String filename, int type){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), ENCODE) );
            String line;
            while ( (line = reader.readLine()) != null){
                line = line.trim();
                String[] s = line.split("\t");
                switch(type){
                    case 1:
                        int baseSize = Integer.parseInt(line);
                        checkindex = new int[baseSize+1];
                        baseindex = new int[baseSize+1];
                        nodeindex = new DANode[baseSize+1];
                        break;

                    case 2:
                        baseindex[Integer.parseInt(s[0])] = Integer.parseInt(s[1]);
                        break;
                    case 3:
                        checkindex[Integer.parseInt(s[0])] = Integer.parseInt(s[1]);
                        break;
                    case 4:
                        nodeindex[Integer.parseInt(s[0])] = new DANode(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                        break;
                    case 5:
                        String[] cs = s[0].split(":");
                        LineInfo li = new LineInfo(cs[0], Integer.parseInt(cs[1]));
                        inverttable.add(li);
                        break;
                }
            }
            reader.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void load(String dir){
        long start = System.currentTimeMillis();
        load(dir +"size.dat", 1);
        load(dir + "base.dat", 2);
        load(dir + "check.dat", 3);
        load(dir + "node.dat", 4);
        load(dir + "invert.dat", 5);
        System.out.println("load cost:" + (System.currentTimeMillis()-start));
    }

    /**
     * 查询二叉树
     * @param query
     * @return
     */
    public BasicDBList search(String query){
        if(query==null || query.length()==0){
            return null;
        }
        int id = getPrefix(query);
        if(id==-1){
            return null;
        }
        DANode da = nodeindex[id];
        if(da==null){
            return null;
        }
        int start = da.start;
        int end = da.end;
        int cnt = 0;

        BasicDBList result = new BasicDBList();
        for(int i=start; i<end && ++cnt<10; ++i){
            LineInfo qi = inverttable.get(i);
            String word = qi.line;
            BasicDBList list = new BasicDBList();
            list.add(word);
            list.add(qi.weight);
            result.add(list);
        }
        return result;
    }

    public static void main(String[] args) {
        String dataFile = "/server/extendsearch/myDoubleArray/";
        MFileUtil.checkDir(dataFile);
        MyDoubleArray mda = new MyDoubleArray();
        mda.loadCleanData("/server/extendsearch/t.data");
        mda.build();
        mda.save(dataFile);
        mda.load(dataFile);
        System.out.println(mda.search("1"));
    }
}
