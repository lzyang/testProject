package datastructure.tree;

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


    public static void main(String[] args){
        TrieTree tree = new TrieTree();

        tree.addStr("abcd");
        tree.addStr("cdcd");
        tree.addStr("csed");
        tree.addStr("dscd");
        System.out.println(tree.info());


    }
}
