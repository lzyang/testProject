package datastructure.tree;

import com.sun.media.sound.SoftTuning;
import org.junit.Test;

import javax.xml.bind.SchemaOutputResolver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Morningsun(515190653@qq.com) on 15-11-17.
 */
public class TrieTree {
    private class Node {
        private int words;
        private int prefixes;
        private Node[] edges;

        Node() {
            this.words = 0;
            this.prefixes = 0;
            edges = new Node[26];   //26个字母
            for (int i = 0; i < edges.length; i++) {
                edges[i] = null;
            }
        }
    }

    private Node root = new Node();

    public String info(){
        StringBuilder sbf = new StringBuilder();
        sbf.append("words:");
        sbf.append(root.words);
        sbf.append(" prefixes:");
        sbf.append(root.prefixes);
        return sbf.toString();
    }

    /**
     * 向trie树里面添加节点
     *
     * @param str
     */
    public void addWord(Node node,String str) {
        if (str.length() == 0) {
            node.words++;
        } else {
            node.prefixes++;
            char c = str.charAt(0);
            c = Character.toLowerCase(c);
            int index = c - 'a';
            if(node.edges[index]==null){
                node.edges[index] = new Node();
            }
            addWord(node.edges[index], str.substring(1));
        }
    }

    /**
     * 添加字符串,从根节点开始
     * @param str
     */
    public void addStr(String str){
        addWord(root,str);
    }



    /**
     * 遍历出所有的字符串
     */
    public Set<String> readAllStr(){
        Set<String> allStr = new HashSet<String>();
        readNode(root,allStr,"");
        return allStr;
    }

    /**
     * 读取当前节点,遍历用
     * @param node
     * @param allStr
     * @param preChar
     */
    public void readNode(Node node,Set<String> allStr,String preChar) {
        if(node.words!=0){
            allStr.add(preChar);
        }
        for(int i=0;i<node.edges.length;i++){
            if(node.edges[i]!=null){
                String nStr = new String(preChar);
                nStr += (char)('a' + i);
                readNode(node.edges[i],allStr,nStr);
            }
        }
    }

    /**
     * 获取与输入词最大匹配的词
     * @param prefix
     * @return
     */
    public String getMaxMatchStr(String prefix){
        String s = "";
        String temp = "";//记录最长迪单词
        char[] w = prefix.toCharArray();
        Node node = root;
        for(int i=0;i<w.length;i++){
            char c = w[i];
            c = Character.toLowerCase(c);
            int index = c - 'a';
            if(node.edges[index]==null){  //当前节点无字节点匹配
                if(node.words!=0){  //当前节点成词
                    return s;   //返回已匹配的字符
                }else{         //当前节点不成词
                    return null;
                }
            }else{    //当前节点有字节点匹配
                s+=c;
                if(node.words!=0){  //当前节点成词
                    temp = s;
                    System.out.println("temp:"+temp);
                }
                node = node.edges[index];
            }
        }
        if(node!=null){
            return temp;
        }
        return s;
    }

    /**
     * 获取前缀为输入prefix的词
     * @param prefix
     * @return
     */
    public Set<String> getPrefixStrList(String prefix){
        Set<String> strSet = new HashSet<String>();
        char[] w = prefix.toCharArray();
        Node node = root;
        for(int i=0;i<w.length;i++){
            char c = w[i];
            c = Character.toLowerCase(c);
            int index = c - 'a';
            if(node.edges[index]==null){  //当前节点无字节点匹配
                return strSet;
            }else{    //当前节点有字节点匹配
                node = node.edges[index];
            }
        }

        if(node!=null){
            // 有比当前词更长的词
            readNode(node,strSet,prefix);
        }
        return strSet;
    }

    public static void main(String[] args){
        TrieTree tree = new TrieTree();

        tree.addStr("abcd");
        tree.addStr("cdcd");
        tree.addStr("cdc");
        tree.addStr("cdcdddfsafds");
        tree.addStr("cdcdddfsa");
        tree.addStr("cdcddfsdd");
        tree.addStr("cdcdaaaaaaaaaa");
        tree.addStr("d");

        System.out.println("max:" + tree.getMaxMatchStr("cdcd"));

        System.out.println("tree.readAllStr:"+tree.readAllStr());

        System.out.println("tree.info:" + tree.info());

        System.out.println("tree.getPrefixStrList:" + tree.getPrefixStrList("cd"));

        System.out.println((char)(25105));
        System.out.println('我'-0);
    }

//    @Test
//    public void printCode(){
//        for (int i = 57000; i < 77000; i++) {
//            if (i % 10 == 0)
//                System.out.println(i + "_" + (char) i + " ");
//            else
//                System.out.print(i + "_" + (char) i + " ");
//        }
//    }
}
