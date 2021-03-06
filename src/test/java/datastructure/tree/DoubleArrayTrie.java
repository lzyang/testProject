package datastructure.tree;

/**
 * from https://github.com/komiya-atsushi/darts-java/blob/master/src/main/java/darts/DoubleArrayTrie.java
 */

/**
 * DoubleArrayTrie: Java implementation of Darts (Double-ARray Trie System)
 *
 * <p>
 * Copyright(C) 2001-2007 Taku Kudo &lt;taku@chasen.org&gt;<br />
 * Copyright(C) 2009 MURAWAKI Yugo &lt;murawaki@nlp.kuee.kyoto-u.ac.jp&gt;
 * Copyright(C) 2012 KOMIYA Atsushi &lt;komiya.atsushi@gmail.com&gt;
 * </p>
 *
 * <p>
 * The contents of this file may be used under the terms of either of the GNU
 * Lesser General Public License Version 2.1 or later (the "LGPL"), or the BSD
 * License (the "BSD").
 * </p>
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoubleArrayTrie {
    private final static int BUF_SIZE = 16384;
    private final static int UNIT_SIZE = 8; // size of int + int

    private static class Node {
        int code;
        char c;
        int depth;
        int left;
        int right;

        @Override
        public String toString() {
            return "{"+c+","+code+","+depth+","+left+","+right+"}";
        }
    }

    private int check[];
    private int base[];

    private boolean used[];
    private int size;   //数组所占空间大小
    private int allocSize;
    private List<String> key;   //所有key
    private int keySize;      //所有key的个数
    private int length[];
    private int value[];
    private int progress;   //处理完的词数
    private int nextCheckPos;
    // boolean no_delete_;
    int error_;

    // int (*progressfunc_) (size_t, size_t);

    // inline _resize expanded
    private int resize(int newSize) {
        int[] base2 = new int[newSize];
        int[] check2 = new int[newSize];
        boolean used2[] = new boolean[newSize];
        if (allocSize > 0) {
            System.arraycopy(base, 0, base2, 0, allocSize);
            System.arraycopy(check, 0, check2, 0, allocSize);
            System.arraycopy(used2, 0, used2, 0, allocSize);  //？？？TODO del
        }

        base = base2;
        check = check2;
        used = used2;  //???TODO del

        return allocSize = newSize;
    }

    private int fetch(Node parent, List<Node> siblings) {
        if (error_ < 0)
            return 0;

        int prev = 0;

        for (int i = parent.left; i < parent.right; i++) {  //left = 0,right=key.size
            if ((length != null ? length[i] : key.get(i).length()) < parent.depth)  //如果不是初始第一次进入，return
                continue;

            String tmp = key.get(i);  //拿出第i个词条

            int cur = 0;  //tmp code + 1
            if ((length != null ? length[i] : tmp.length()) != parent.depth)
                cur = (int) tmp.charAt(parent.depth) + 1;

            if (prev > cur) {
                error_ = -3;
                return 0;
            }

            if (cur != prev || siblings.size() == 0) {
                Node tmp_node = new Node();
                tmp_node.depth = parent.depth + 1;
                tmp_node.code = cur;
                tmp_node.c = (char)(cur-1);
                tmp_node.left = i;
                if (siblings.size() != 0)
                    siblings.get(siblings.size() - 1).right = i;  //设置前一个sibling的节点的right属性

                siblings.add(tmp_node);
            }

            prev = cur;
        }

        if (siblings.size() != 0)   //设置最后一个节点的right属性
            siblings.get(siblings.size() - 1).right = parent.right;

        System.out.println(parent+">>>>>"+siblings);
        return siblings.size();
    }

    private int insert(List<Node> siblings) {
        if (error_ < 0)
            return 0;

        int begin = 0;   //解决冲突后每组siblings存储的起始位，使得check[begin + a1…an]  == 0，也就是找到了n个空闲空间,a1…an是siblings中的n个节点对应的code。
        //当前位置(当前组可用位置，已解决冲突)
        int pos = ((siblings.get(0).code + 1 > nextCheckPos) ? siblings.get(0).code + 1   //字符unicode + 1
                : nextCheckPos) - 1;
        int nonzero_num = 0;   //冲突次数
        int first = 0;    //标记是否是每组siblings头一个字符

        if (allocSize <= pos)
            resize(pos + 1);

        outer:
        while (true) {
            pos++;

            if (allocSize <= pos)   //检测是否越界
                resize(pos + 1);

            if (check[pos] != 0) {   //如果当前位被占，则位置后移
                nonzero_num++;
                continue;
            } else if (first == 0) {
                nextCheckPos = pos;   //首字符nextcheckpos = 首字符nunicode + 1
                first = 1;
            }

            begin = pos - siblings.get(0).code;
            if (allocSize <= (begin + siblings.get(siblings.size() - 1).code)) {   //控制双数组增长速度
                // progress can be zero
                double l = (1.05 > 1.0 * keySize / (progress + 1)) ? 1.05 : 1.0
                        * keySize / (progress + 1);
                resize((int) (allocSize * l));
            }

            if (used[begin])
                continue;

            for (int i = 1; i < siblings.size(); i++)    //对于每一组siblings  寻找空闲空间check[begin + a1…an]  == 0
                if (check[begin + siblings.get(i).code] != 0)
                    continue outer;

            break;
        }

        // -- Simple heuristics --
        // if the percentage of non-empty contents in check between the
        // index
        // 'next_check_pos' and 'check' is greater than some constant value
        // (e.g. 0.9),
        // new 'next_check_pos' index is written by 'check'.
        if (1.0 * nonzero_num / (pos - nextCheckPos + 1) >= 0.95)
            nextCheckPos = pos;

        used[begin] = true;   //每组begin位置
        size = (size > begin + siblings.get(siblings.size() - 1).code + 1) ? size   //所占空间大小
                : begin + siblings.get(siblings.size() - 1).code + 1;

        for (int i = 0; i < siblings.size(); i++)
            check[begin + siblings.get(i).code] = begin;   //check赋值

        for (int i = 0; i < siblings.size(); i++) {
            List<Node> new_siblings = new ArrayList<Node>();

            if (fetch(siblings.get(i), new_siblings) == 0) {   //字符到达末尾，base值设置为负
                base[begin + siblings.get(i).code] = (value != null) ? (-value[siblings
                        .get(i).left] - 1) : (-siblings.get(i).left - 1);

                if (value != null && (-value[siblings.get(i).left] - 1) >= 0) {
                    error_ = -2;
                    return 0;
                }

                progress++;   //处理完的词数
                // if (progress_func_) (*progress_func_) (progress,
                // keySize);
            } else {
                int h = insert(new_siblings);
                base[begin + siblings.get(i).code] = h;   //base赋值，子siblings的begin值
            }
        }
        return begin;
    }

    public DoubleArrayTrie() {
        check = null;
        base = null;
        used = null;
        size = 0;
        allocSize = 0;
        // no_delete_ = false;
        error_ = 0;
    }

    // no deconstructor

    // set_result omitted
    // the search methods returns (the list of) the value(s) instead
    // of (the list of) the pair(s) of value(s) and length(s)

    // set_array omitted
    // array omitted

    void clear() {
        // if (! no_delete_)
        check = null;
        base = null;
        used = null;
        allocSize = 0;
        size = 0;
        // no_delete_ = false;
    }

    public int getUnitSize() {
        return UNIT_SIZE;
    }

    public int getSize() {
        return size;
    }

    public int getTotalSize() {
        return size * UNIT_SIZE;
    }

    public int getNonzeroSize() {
        int result = 0;
        for (int i = 0; i < size; i++)
            if (check[i] != 0)
                result++;
        return result;
    }

    /**
     * 构建双数组
     * @param key  有序词条列表
     * @return
     */
    public int build(List<String> key) {
        return build(key, null, null, key.size());
    }

    /**
     * 构建双数组
     * @param _key   列表集合
     * @param _length
     * @param _value
     * @param _keySize  列表大小
     * @return
     */
    public int build(List<String> _key, int _length[], int _value[],
                     int _keySize) {
        if (_keySize > _key.size() || _key == null)
            return 0;

        // progress_func_ = progress_func;
        key = _key;
        length = _length;
        value = _value;
        keySize = _keySize;
        progress = 0;

        resize(65536 * 32);

        base[0] = 1;
        nextCheckPos = 0;

        Node root_node = new Node();   //根结点的左右为0和keysize
        root_node.left = 0;
        root_node.right = keySize;
        root_node.depth = 0;

        List<Node> siblings = new ArrayList<Node>();
        fetch(root_node, siblings);  //获取当前字符分支下下一个节点的字符信息以及位置

        //System.out.println("==============================================");
        insert(siblings);

        // size += (1 << 8 * 2) + 1; // ???
        // if (size >= allocSize) resize (size);

        used = null;
        key = null;

        return error_;
    }

    /**
     * 用保存的双数组进行初始化双数组
     * @param fileName
     * @throws IOException
     */
    public void open(String fileName) throws IOException {
        File file = new File(fileName);
        size = (int) file.length() / UNIT_SIZE;
        check = new int[size];
        base = new int[size];

        DataInputStream is = null;
        try {
            is = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(file), BUF_SIZE));
            for (int i = 0; i < size; i++) {
                base[i] = is.readInt();
                check[i] = is.readInt();
            }
        } finally {
            if (is != null)
                is.close();
        }
    }

    /**
     * 保存双数组
     * @param fileName
     * @throws IOException
     */
    public void save(String fileName) throws IOException {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(fileName)));
            for (int i = 0; i < size; i++) {
                out.writeInt(base[i]);
                out.writeInt(check[i]);
            }
            out.close();
        } finally {
            if (out != null)
                out.close();
        }
    }

    public int exactMatchSearch(String key) {
        return exactMatchSearch(key, 0, 0, 0);
    }

    /**
     * 进行精确匹配(exact match) 检索, 判断给定字符串是否为词典中的词条.

     key 待检索字符串,
     len 字符串长度,
     node_pos 指定从 Double-Array 的哪个节点位置开始检索.

     len, 和 node_pos 都可以省略, 省略的时候， len 缺省使用 LengthFunc 计算,
     node_pos 缺省为 root 节点.
     检索成功时， 返回 key 对应的 value 值, 失败则返回 -1.
     * @param key
     * @param pos
     * @param len
     * @param nodePos
     * @return
     */
    public int exactMatchSearch(String key, int pos, int len, int nodePos) {
        if (len <= 0)
            len = key.length();
        if (nodePos <= 0)
            nodePos = 0;

        int result = -1;

        char[] keyChars = key.toCharArray();

        int b = base[nodePos];
        int p;

        for (int i = pos; i < len; i++) {
            p = b + (int) (keyChars[i]) + 1;
            if (b == check[p])
                b = base[p];
            else
                return result;
        }

        p = b;
        int n = base[p];
        if (b == check[p] && n < 0) {
            result = -n - 1;
        }
        return result;
    }

    public List<Integer> commonPrefixSearch(String key) {
        return commonPrefixSearch(key, 0, 0, 0);
    }

    /**
     * 执行 common prefix search. 检索给定字符串的哪些的前缀是词典中的词条

     key 待检索字符串,
     result 用于保存多个命中结果的数组,
     result_size 数组 result 大小,
     len 待检索字符串长度,
     node_pos 指定从 Double-Array 的哪个节点位置开始检索.

     len, 和 node_pos 都可以省略, 省略的时候， len 缺省使用 LengthFunc 计算,
     node_pos 缺省为 root 节点.

     函数返回命中的词条个数. 对于每个命中的词条， 词条对应的 value 值存依次放在 result 数组中. 如果命中的词条个数超过 result_size 的大小，
     则 result 数组中只保存 result_size 个结果。函数的返回值为实际的命中词条个数， 可能超过 result_size 的大小。
     * @param key
     * @param pos
     * @param len
     * @param nodePos
     * @return
     */
    public List<Integer> commonPrefixSearch(String key, int pos, int len,
                                            int nodePos) {
        if (len <= 0)
            len = key.length();
        if (nodePos <= 0)
            nodePos = 0;

        List<Integer> result = new ArrayList<Integer>();

        char[] keyChars = key.toCharArray();

        int b = base[nodePos];
        int n;
        int p;

        for (int i = pos; i < len; i++) {
            p = b;
            n = base[p];

            if (b == check[p] && n < 0) {
                result.add(-n - 1);
            }

            p = b + (int) (keyChars[i]) + 1;
            if (b == check[p])
                b = base[p];
            else
                return result;
        }

        p = b;
        n = base[p];

        if (b == check[p] && n < 0) {
            result.add(-n - 1);
        }

        return result;
    }

    // debug
    public void dump() {
        for (int i = 0; i < size; i++) {
            if(base[i]>0||check[i]>0)
            System.err.println("i: " + i + " [" + base[i] + ", " + check[i]
                    + "]");
        }
    }
}
