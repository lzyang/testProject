/*
The MIT License

Copyright (c) 2008 Tahseen Ur Rehman, Javid Jamae

http://code.google.com/p/radixtree/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package datastructure.tree.radixtree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementation for Radix tree {@link RadixTree}
 *
 * @author Tahseen Ur Rehman (tahseen.ur.rehman {at.spam.me.not} gmail.com)
 * @author Javid Jamae
 */
public class RadixTreeImpl<T> implements RadixTree<T> {

    protected RadixTreeNode<T> root;

    protected long size;
    protected boolean ignoreCase = false;

    /**
     * Create a Radix Tree with only the default node root.
     */
    public RadixTreeImpl() {
        this(false);
    }

    public RadixTreeImpl(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        root = new RadixTreeNode<T>(this);
        root.setKey("");
        size = 0;
    }

    public boolean ignoreCase() {
        return this.ignoreCase;
    }

    @SuppressWarnings("unchecked")
    public T find(String key) {
        Visitor<T> visitor = new Visitor<T>() {
            T result = null;

            public void visit(String key, RadixTreeNode<T> parent, RadixTreeNode<T> node) {
                if (node.isReal())
                    result = node.getValue();
            }

            public Object getResult() {
                return result;
            }
        };

        visit(key, visitor);

        return (T) visitor.getResult();
    }

    public boolean delete(String key) {
        Visitor<T> visitor = new Visitor<T>() {
            boolean delete = false;

            public void visit(String key, RadixTreeNode<T> parent, RadixTreeNode<T> node) {
                delete = node.isReal();

                // if it is a real node
                if (delete) {
                    // If there no children of the node we need to
                    // delete it from the its parent children list
                    if (node.getChildern().size() == 0) {
                        Iterator<RadixTreeNode<T>> it = parent.getChildern().iterator();
                        while (it.hasNext()) {
                            if (it.next().getKey().equals(node.getKey())) {
                                it.remove();
                                break;
                            }
                        }

                        // if parent is not real node and has only one child
                        // then they need to be merged.
                        if (parent.getChildern().size() == 1 && parent.isReal() == false) {
                            mergeNodes(parent, parent.getChildern().get(0));
                        }
                    } else if (node.getChildern().size() == 1) {
                        // we need to merge the only child of this node with
                        // itself
                        mergeNodes(node, node.getChildern().get(0));
                    } else { // we jus need to mark the node as non real.
                        node.setReal(false);
                    }
                }
            }

            /**
             * Merge a child into its parent node. Opertaion only valid if it is
             * only child of the parent node and parent node is not a real node.
             *
             * @param parent
             *            The parent Node
             * @param child
             *            The child Node
             */
            private void mergeNodes(RadixTreeNode<T> parent, RadixTreeNode<T> child) {
                parent.setKey(parent.getKey() + child.getKey());
                parent.setReal(child.isReal());
                parent.setValue(child.getValue());
                parent.setChildern(child.getChildern());
            }

            public Object getResult() {
                return Boolean.valueOf(delete);
            }
        };

        visit(key, visitor);

        if (((Boolean) visitor.getResult()).booleanValue()) {
            size--;
        }

        return ((Boolean) visitor.getResult()).booleanValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see ds.tree.RadixTree#insert(java.lang.String, java.lang.Object)
     */
    public boolean insert(String key, T value) {
        if (insert(key, root, value)) {
            size++;
            return true;
        }
        return false;
    }

    public void addAll(T[] values) {
        for (int i = 0; values != null && i < values.length; i++) {
            insert(String.valueOf(values[i]), values[i]);
        }
    }

    /**
     * Recursively insert the key in the radix tree.
     *
     * @param key
     *            The key to be inserted
     * @param node
     *            The current node
     * @param value
     *            The value associated with the key
     * @throws DuplicateKeyException
     *             If the key already exists in the database.
     */
    private boolean insert(String key, RadixTreeNode<T> node, T value) {

        int numberOfMatchingCharacters = node.getNumberOfMatchingCharacters(key);

        // we are either at the root node
        // or we need to go down the tree
        if (node.getKey().equals("") == true || numberOfMatchingCharacters == 0
                || (numberOfMatchingCharacters < key.length() && numberOfMatchingCharacters >= node.getKey().length())) {
            boolean flag = false;
            String newText = key.substring(numberOfMatchingCharacters, key.length());
            for (RadixTreeNode<T> child : node.getChildern()) {
                if (child.getKey().startsWith(newText.charAt(0) + "")) {
                    flag = true;
                    return insert(newText, child, value);
                }
            }

            // just add the node as the child of the current node
            if (flag == false) {
                RadixTreeNode<T> n = new RadixTreeNode<T>(this);
                n.setKey(newText);
                n.setReal(true);
                n.setValue(value);

                node.getChildern().add(n);
            }
        }
        // there is a exact match just make the current node as data node
        else if (numberOfMatchingCharacters == key.length() && numberOfMatchingCharacters == node.getKey().length()) {
            if (node.isReal() == true) {
                return false;
            }

            node.setReal(true);
            node.setValue(value);
        }
        // This node need to be split as the key to be inserted
        // is a prefix of the current node key
        else if (numberOfMatchingCharacters > 0 && numberOfMatchingCharacters < node.getKey().length()) {
            RadixTreeNode<T> n1 = new RadixTreeNode<T>(this);
            n1.setKey(node.getKey().substring(numberOfMatchingCharacters, node.getKey().length()));
            n1.setReal(node.isReal());
            n1.setValue(node.getValue());
            n1.setChildern(node.getChildern());

            node.setKey(key.substring(0, numberOfMatchingCharacters));
            node.setReal(false);
            node.setChildern(new ArrayList<RadixTreeNode<T>>());
            node.getChildern().add(n1);

            if (numberOfMatchingCharacters < key.length()) {
                RadixTreeNode<T> n2 = new RadixTreeNode<T>(this);
                n2.setKey(key.substring(numberOfMatchingCharacters, key.length()));
                n2.setReal(true);
                n2.setValue(value);

                node.getChildern().add(n2);
            } else {
                node.setValue(value);
                node.setReal(true);
            }
        }
        // this key need to be added as the child of the current node
        else {
            RadixTreeNode<T> n = new RadixTreeNode<T>(this);
            n.setKey(node.getKey().substring(numberOfMatchingCharacters, node.getKey().length()));
            n.setChildern(node.getChildern());
            n.setReal(node.isReal());
            n.setValue(node.getValue());

            node.setKey(key);
            node.setReal(true);
            node.setValue(value);

            node.getChildern().add(n);
        }
        return true;
    }

    public ArrayList<T> searchPrefix(String key, int recordLimit) {
        ArrayList<T> keys = new ArrayList<T>();
        RadixTreeNode<T> node = searchPefix(key, root);
        if (node != null) {
            if (node.isReal()) {
                keys.add(node.getValue());
            }
            getNodes(node, keys, recordLimit);
        }
        return keys;
    }

    private void getNodes(RadixTreeNode<T> parent, ArrayList<T> keys, int limit) {
        Queue<RadixTreeNode<T>> queue = new LinkedList<RadixTreeNode<T>>();

        queue.addAll(parent.getChildern());

        while (!queue.isEmpty()) {
            RadixTreeNode<T> node = queue.remove();
            if (node.isReal() == true) {
                keys.add(node.getValue());
            }

            if (keys.size() == limit) {
                break;
            }

            queue.addAll(node.getChildern());
        }
    }

    private RadixTreeNode<T> searchPefix(String key, RadixTreeNode<T> node) {
        RadixTreeNode<T> result = null;

        int numberOfMatchingCharacters = node.getNumberOfMatchingCharacters(key);

        if (numberOfMatchingCharacters == key.length() && numberOfMatchingCharacters <= node.getKey().length()) {
            result = node;
        } else if (node.getKey().equals("") == true || (numberOfMatchingCharacters < key.length() && numberOfMatchingCharacters >= node.getKey().length())) {
            String newText = key.substring(numberOfMatchingCharacters, key.length());
            for (RadixTreeNode<T> child : node.getChildern()) {
                char c1 = child.getKey().charAt(0);
                char c2 = newText.charAt(0);
                if (this.ignoreCase) {
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                }
                if (c1 == c2) {
                    result = searchPefix(newText, child);
                    break;
                }
            }
        }

        return result;
    }

    public boolean contains(String key) {
        Visitor<T> visitor = new Visitor<T>() {
            boolean result = false;

            public void visit(String key, RadixTreeNode<T> parent, RadixTreeNode<T> node) {
                result = node.isReal();
            }

            public Object getResult() {
                return Boolean.valueOf(result);
            }
        };

        visit(key, visitor);

        return ((Boolean) visitor.getResult()).booleanValue();
    }

    /**
     * visit the node those key matches the given key
     *
     * @param key
     *            The key that need to be visited
     * @param visitor
     *            The visitor object
     */
    public void visit(String key, Visitor<T> visitor) {
        if (root != null)
            visit(key, visitor, null, root);
    }

    /**
     * recursively visit the tree based on the supplied "key". calls the Visitor
     * for the node those key matches the given prefix
     *
     * @param prefix
     *            The key o prefix to search in the tree
     * @param visitor
     *            The Visitor that will be called if a node with "key" as its
     *            key is found
     * @param node
     *            The Node from where onward to search
     */
    private void visit(String prefix, Visitor<T> visitor, RadixTreeNode<T> parent, RadixTreeNode<T> node) {

        int numberOfMatchingCharacters = node.getNumberOfMatchingCharacters(prefix);

        // if the node key and prefix match, we found a match!
        if (numberOfMatchingCharacters == prefix.length() && numberOfMatchingCharacters == node.getKey().length()) {
            visitor.visit(prefix, parent, node);
        } else if (node.getKey().equals("") == true // either we are at the
                // root
                || (numberOfMatchingCharacters < prefix.length() && numberOfMatchingCharacters >= node.getKey().length())) { // OR
            // we
            // need
            // to
            // traverse the childern
            String newText = prefix.substring(numberOfMatchingCharacters, prefix.length());
            for (RadixTreeNode<T> child : node.getChildern()) {
                // recursively search the child nodes
                if (child.getKey().startsWith(newText.charAt(0) + "")) {
                    visit(newText, visitor, node, child);
                    break;
                }
            }
        }
    }

    /**
     * Display the Trie on console. WARNING! Do not use for large Trie. For
     * testing purpose only.
     */
    @Deprecated
    public void display() {
        display(0, root);
    }

    @Deprecated
    private void display(int level, RadixTreeNode<T> node) {
        for (int i = 0; i < level; i++) {
            System.out.print(" ");
        }
        System.out.print("|");
        for (int i = 0; i < level; i++) {
            System.out.print("-");
        }

        if (node.isReal() == true)
            System.out.println(node.getKey() + "[" + node.getValue() + "]*");
        else
            System.out.println(node.getKey());

        for (RadixTreeNode<T> child : node.getChildern()) {
            display(level + 1, child);
        }
    }

    public long getSize() {
        return size;
    }
}