package datastructure.tree;

import java.io.*;
import java.util.*;

/**
 * Created by Morningsun(515190653@qq.com) on 15-11-18.
 */
public class DoubleArrayTrieTest {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("/server/dev/data/trieData/small.dic"));
        String line;
        List<String> words = new ArrayList<String>();  //词条列表
        Set<Character> charset = new HashSet<Character>();  //所有词条的字符集合
        while ((line = reader.readLine()) != null) {
            words.add(line);
            // 制作一份码表debug
            for (char c : line.toCharArray()) {
                charset.add(c);
            }
        }
        reader.close();
        // 这个字典如果要加入新词必须按字典序，参考下面的代码
//        Collections.sort(words);
//        BufferedWriter writer = new BufferedWriter(new FileWriter("/server/dev/data/trieData/sorted.dic", false));
//        for (String w : words)
//        {
//            writer.write(w);
//            writer.newLine();
//        }
        System.out.println("字典词条：" + words.size());

        {
            String infoCharsetValue = "";
            String infoCharsetCode = "";
            for (Character c : charset) {
                infoCharsetValue += c.charValue() + "     ";
                infoCharsetCode += (int) c.charValue() + " ";
            }
            infoCharsetValue += '\n';
            infoCharsetCode += '\n';
            System.out.print(infoCharsetValue);
            System.out.print(infoCharsetCode);
        }

        DoubleArrayTrie dat = new DoubleArrayTrie();
        System.out.println("是否错误: " + dat.build(words));
        System.out.println(dat);
        List<Integer> integerList = dat.commonPrefixSearch("一举成名天下知");
        for (int index : integerList) {
            System.out.println(words.get(index));
        }
    }
}
