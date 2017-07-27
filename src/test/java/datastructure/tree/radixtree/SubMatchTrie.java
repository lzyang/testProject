package datastructure.tree.radixtree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by root on 17-7-27.
 */
public class SubMatchTrie {

    private Node[] baseindex = null;
    private int[] checkindex = null;
    private static final String basename = "base.idx";
    private static final String checkname = "check.idx";
    private static final String sizename = "basesize.idx";
    private  String datadir;

    private HashMap<String, Set<Node>> hsinverts = new HashMap<String, Set<Node>>();   //每个字符串字串的下一个字符，当最后一个字符时存放字符串的类型（物品词，品牌词）  如手机壳   <手机，[{4142,0}]>  <手机壳，[{4142,2}]>
    private List<Set<String>> wordFromIndex = new ArrayList<Set<String>>();   //1.[[手],[苹],[三]]   2.[[手机],[苹果],[三星]]

    private static int cursor = 0;
    private static int MAXPREFIXLENGTH = 10;
    private static int BASESIZE = 0;

    public Set<String> dictset = new HashSet<String>();   //有序的关键字集和


    public class Node implements Comparable<Node>{
        public int base;  //字符的unicode
        public int type;
        public Node(int b, int t){
            base = b;
            type = t;
        }
        @Override
        public int compareTo(Node o) {
            // TODO Auto-generated method stub
            if(this.base<o.base){
                return 1;
            }
            if(this.base==o.base){
                if(this.type==o.type){
                    return 0;
                }
                return 1;
            }
            return -1;
        }
    }

    public class SegResult{
        public String word = null;
        public int type = 0;
        public SegResult(String w, int t){
            word = w;
            type = t;
        }
    }

    public class MatchInfo{
        public int pos = 0;
        public int type = 0;
        public MatchInfo(int p, int t){
            pos = p;
            type = t;
        }

    }

    /**
     * 初始化双数组数据结构
     */
    public void initWordIndex(){
        checkindex = new int[1000000];
        baseindex = new Node[1000000];
        for(int i=0; i<MAXPREFIXLENGTH; ++i){
            Set<String> invert = new TreeSet<String>();
            wordFromIndex.add(invert);
        }
    }

    public boolean isEmpty(String phrase){
        if(phrase!=null && phrase.trim().length()!=0){
            return false;
        }
        return true;
    }

    public String full2Half(String fullstr) {
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

    public static boolean isNumEn(char c){
        if(c>='0'&&c<='9' || c>='a'&&c<='z' || c==32){
            return true;
        }
        return false;
    }

    /**
     * 去除空格
     * @param phrase
     * @return
     */
    public String removeSpaceEx(String phrase){
        if(isEmpty(phrase)){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean prespace = true;
        for(int i=0; i<phrase.length(); ++i){
            char c = phrase.charAt(i);
            if(!isNumEn(c) && !isChinese(c)){
                continue;
            }
            if(c==32){
                if(!prespace){
                    sb.append(phrase.charAt(i));
                }
                prespace = true;
            }else{
                sb.append(phrase.charAt(i));
                prespace = false;
            }
        }
        return sb.toString().trim();
    }

    public String normalizeString(String phrase){
        if(isEmpty(phrase)){
            return null;
        }
        String halfstr = full2Half(phrase.toLowerCase());
        return removeSpaceEx(halfstr);
    }

    /**
     * 加载词典
     * @param filename
     * @param type  1品牌，2，物品词
     * @return
     */
    public boolean loadDict(String filename, int type){
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8") );
            String line = null;
            String query = null;

            while ( (line = reader.readLine()) != null){
                query = normalizeString(line.toLowerCase().trim());
                if(query==null || query.length()==0){
                    continue;
                }
                if(dictset.contains(query)){
                    continue;
                }
                dictset.add(query);
                insertQuery(query, type);
            }
            System.out.println("max header:" + cursor);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(null != reader){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    /**
     * 构建生成双数组中间数据
     * @param query
     * @param type
     */
    public void insertQuery(String query, int type){
        int t = query.charAt(0);
        if(t>cursor){
            cursor = t+1;
        }
        String subword = "";
        int len = query.length();
        for(int i=0; i<len && i<MAXPREFIXLENGTH; ++i){
            subword += query.charAt(i);
            int j = i+1;
            if(j<len-1){   //j小于最后一次字符的时候，存放下一个字符的unicode和0
                if(hsinverts.containsKey(subword)){
                    boolean exist = false;
                    Set<Node> set = hsinverts.get(subword);
                    for(Node n : set){
                        if(n.base==query.charAt(j)){
                            exist = true;
                            break;
                        }
                    }
                    if(!exist){
                        hsinverts.get(subword).add(new Node(query.charAt(j), 0));
                    }
                }else{
                    Set<Node> set = new TreeSet<Node>();
                    set.add(new Node(query.charAt(j), 0));
                    hsinverts.put(subword, set);
                }
            }
            if(j==len-1){   //j等于最后一个字符的时候,存放字符串的类型，物品词，品牌词
                if(hsinverts.containsKey(subword)){
                    hsinverts.get(subword).remove(new Node(query.charAt(j), 0));
                    hsinverts.get(subword).add(new Node(query.charAt(j), type));
                }else{
                    Set<Node> set = new TreeSet<Node>();
                    set.add(new Node(query.charAt(j), type));
                    hsinverts.put(subword, set);
                }
            }
            if(wordFromIndex.get(i).contains(subword)){
                continue;
            }
            wordFromIndex.get(i).add(subword);
        }
    }

    /**
     * 插入首字符，和第二个字符
     * @param query
     * @param set
     * @return
     */
    public int insertHeader(String query, Set<Node> set){
        int t = query.charAt(0);
        int id = t;
        if(set==null){
            return 0;
        }
        int base = cursor;
        int i = cursor;
        boolean isExist = checkExist(base, set);
        if(isExist){
            resetBase(t,  set);
        }else{
            baseindex[t] = new Node(cursor, 0);
            for(Node c : set){
                int n = base + c.base;
                if(checkindex[n]!=0 || baseindex[n]!=null||baseindex[i+1]!=null){
                    System.out.println("reset-------------"+(char)c.base +":prefixbase:" + base + ":check:" + checkindex[n]);
                }
                baseindex[n] = new Node(++i, c.type);
                checkindex[n] = base;
            }

            while(baseindex[i]!=null){
                i++;
            }
            cursor = i;
        }
        return id;
    }

    public void insertHeader(){
        Set<String> wordset = wordFromIndex.get(0);
        for(String key : wordset){
            insertHeader(key, hsinverts.get(key));
        }
    }

    public int getPrefixBaseId(String query){
        int t = query.charAt(0);
        int base = baseindex[t].base;
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
            if(checkindex[t]!=base){
                System.out.println(query + " base==0");
                return -1;
            }
            base = baseindex[t].base;
            if(base==0 && i==len-1){
                System.out.println(query + " base==0");
                return -1;
            }
        }
        return t;
    }


    public void resetBase(int t,  Set<Node> set){
        int i = cursor+1;
        int base = cursor+1;
        boolean isbreak = true;
        while(i<baseindex.length){
            for(Node c : set){
                int n = base + c.base;
                checkSize(n);
                if(baseindex[++i]==null && baseindex[n]==null && checkindex[n]==0){   //未冲突
                    continue;
                }
                isbreak = false;
                break;
            }
            if(isbreak){   //如果下个字符全都不冲突
                break;
            }
            isbreak = true;
            while(baseindex[i]!=null){
                i++;
            }
            base = i;
        }
        i = base;
        int type = 0;
        if(baseindex[t]!=null){
            type = baseindex[t].type;
        }
        baseindex[t] = new Node(base, type);
        for(Node c : set){
            int n = base + c.base;
            if(checkindex[n]!=0 || baseindex[n]!=null || baseindex[i+1]!=null){
                System.out.println("reset-------------"+(char)c.base +":prefixbase:" + base + ":check:" + checkindex[n]);
            }

            baseindex[n] = new Node(++i, c.type);
            checkindex[n] = base;
        }
        while(baseindex[i]!=null){
            i++;
        }
        cursor = i;
    }

    public void resize(){
        baseindex = Arrays.copyOf(baseindex, baseindex.length*2);
        checkindex = Arrays.copyOf(checkindex, checkindex.length*2);
    }

    public void checkSize(int t){
        if(t>=baseindex.length){
            resize();
        }
    }

    public boolean checkExist(int base, Set<Node> set){
        boolean isExist = false;
        int i = base;
        for(Node c : set){
            int t = base + c.base;
            checkSize(t);
            if(baseindex[++i]!=null || baseindex[t]!=null || checkindex[t]!=base ){  //刚开始checkindex[t]!=base 肯定冲突？？
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    public int  insertSuffix(String query, Set<Node> set){
        int t = getPrefixBaseId(query);
        int id = t;
        if(t==-1 || set==null){
            System.out.println(query + "t==-1 base==0");
            return 0;
        }
        int base = baseindex[t].base;
        int i = base;
        boolean isExist = checkExist(base, set);
        if(isExist){
            resetBase(t, set);
        }else{
            for(Node c : set){
                int n = base + c.base;
                if(checkindex[n]!=0 || baseindex[n]!=null||baseindex[i+1]!=null){
                    System.out.println("suffix-------------"+(char)c.base +":prefixbase:" + base + ":check:" + checkindex[n]);
                }
                baseindex[n] = new Node(++i, c.type);
                checkindex[n] = base;
            }
            while(baseindex[i]!=null){
                i++;
            }
            cursor = i;
        }
        return id;
    }

    public  void insertSuffix(){
        for(int i=1; i<wordFromIndex.size(); ++i){
            Set<String> wordset = wordFromIndex.get(i);
            for(String key : wordset){
                if(hsinverts.get(key)==null)
                    continue;
                insertSuffix(key, hsinverts.get(key));

            }
        }
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
                    out.write((baseindex.length+"\n").getBytes("utf-8"));
                    break;
                case 2:
                    for(int i=0; i<baseindex.length; ++i){
                        if(baseindex[i]==null){
                            continue;
                        }
                        String tmp = i + "\t" + baseindex[i].base + "\t" + baseindex[i].type + "\n";
                        out.write(tmp.getBytes("utf-8"));
                    }
                    break;
                case 3:
                    for(int i=0; i<checkindex.length; ++i){
                        if(checkindex[i]==0){
                            continue;
                        }
                        String tmp = i + "\t" + checkindex[i] + "\n";
                        out.write(tmp.getBytes("utf-8"));
                    }

                    break;
            }

            out.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void save(){
        save(datadir + sizename, 1);
        save(datadir + basename, 2);
        save(datadir + checkname, 3);
    }

    public void load(String filename, int type){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8") );
            String line;
            while ( (line = reader.readLine()) != null){
                line = line.trim();
                String[] s = line.split("\t");
                switch(type){
                    case 1:
                        BASESIZE = Integer.parseInt(line);
                        checkindex = new int[BASESIZE+1];
                        baseindex = new Node[BASESIZE+1];
                        break;

                    case 2:
                        baseindex[Integer.parseInt(s[0])] = new Node(Integer.parseInt(s[1]),Integer.parseInt(s[2]));
                        break;
                    case 3:
                        checkindex[Integer.parseInt(s[0])] = Integer.parseInt(s[1]);
                        break;

                }
            }
            reader.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 提取物品词加载
     */
    public void load(){
        long start = System.currentTimeMillis();
        load(datadir + sizename, 1);
        load(datadir + basename, 2);
        load(datadir + checkname, 3);
        System.out.println("load cost:" + (System.currentTimeMillis() - start));
    }

    public MatchInfo search(String query){
        MatchInfo mi = null;
        int t = query.charAt(0);
        if(baseindex[t]==null){
            return null;
        }
        int base = baseindex[t].base;
//		System.out.println(base + ":" + t + ":" + checkindex[t]);
        if(base==0){
            return null;
        }
        int len = query.length();
        for(int i=1; i<len; ++i){
            t = base + query.charAt(i);
//			System.out.println(base + ":" + t + ":" + (int)query.charAt(i) + ":" + checkindex[t]);s
            if(t>=baseindex.length){
                break;
            }
            if(baseindex[t]==null){
                break;
            }
            if(checkindex[t]!=base || checkindex[t]==0){
                break;
            }
            if(baseindex[t].type!=0){
                mi = new MatchInfo(i, baseindex[t].type);
            }
            base = baseindex[t].base;
            if(base==0){
                break;
            }
        }
        return mi;
    }

    public List<SegResult> match(String query){
        if(query==null || query.length()==0){
            return null;
        }
        List<SegResult> list = new ArrayList<SegResult>();
        int i = 0;
        int len = query.length();
        while(i<len){
            String subquery = query.substring(i, len);
            System.out.print(subquery + ">>");
            MatchInfo mi = search(subquery);
            if(mi==null){
                ++i;
                continue;
            }
            list.add(new SegResult(subquery.substring(0, mi.pos+1), mi.type));
            i += mi.pos+1;
        }
        return list;
    }

    private void checkDirExits(String path){
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdir();
            }
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {

        SubMatchTrie da = new SubMatchTrie();
        da.datadir = "/mdata/code/queryparse/subMatch/";
        da.checkDirExits(da.datadir);
        da.initWordIndex();

        da.loadDict("/mdata/code/queryparse/brandData/brand.dat", 1);  //组装中间数据
        da.loadDict("/mdata/code/queryparse/brandData/category.dat", 2);  //组装中间数据
        da.loadDict("/mdata/code/queryparse/brandData/color.dat", 3);  //组装中间数据
        da.build();
        da.save();

        da.load();
        String str = "致林sfd手机黑富浅灰地玫瑰金方";
        List<SubMatchTrie.SegResult> seg = da.match(str.toLowerCase());
        List<String> brand = new ArrayList<String>();
        System.out.println("====================================");
        for(SubMatchTrie.SegResult s : seg){
            if(s.type==1){
                brand.add(s.word);
            }

//            System.out.println(bd.readBrand(s.word.toLowerCase()));
            System.out.println(s.word+"\t"+s.type);
        }
    }
}
