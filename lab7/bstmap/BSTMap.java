package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node<K extends Comparable<K>,V>{
        private K key;
        private V val;
        private Node<K, V> left;
        private Node<K, V> right;

        private Node(K key, V val){
            this.key = key;
            this.val = val;
            this.left = null;
            this.right = null;
        }
    }

    public Node<K, V> root;
    public int size;

    public BSTMap(){
        root = null;
        size = 0;
    }

    /** Removes all of the mappings from this map. */
    public void clear(){
        root = null;
        size = 0;
    }

    private boolean checkKey(K key,Node n){
        if (n == null){
            return false;
        }else if (n.key.compareTo(key) > 0){
            return checkKey(key, n.left);
        }else if(n.key.compareTo(key) < 0){
            return checkKey(key, n.right);
        }else{
            return true;
        }
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        return checkKey(key, root);
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key){
        return gethelper(key, root);
    }

    private V gethelper(K key, Node n){
        if (n == null){
            return null;
        }else if (n.key.compareTo(key) > 0){
            return (V) gethelper(key, n.left);
        }else if(n.key.compareTo(key) < 0){
            return (V) gethelper(key, n.right);
        }else{
            return (V) n.val;
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size(){
        return this.size;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value){
        if (this.size == 0){
            this.root = new Node(key, value);
            size += 1;
        }else{
            insert(key, value, root);
        }
    }

    private void insert(K key, V value, Node n){
        if (n.key.compareTo(key) > 0){
            //left
            if (n.left == null){
                n.left = new Node(key, value);
                size += 1;
            }else{
             insert(key, value, n.left);
            }
        }else{
            if (n.right == null){
                n.right = new Node(key, value);
                size += 1;
            }else{
                insert(key, value, n.right);
            }
        }
    }

    public void printInOrder(){
        printHelper(root);
    }

    private void printHelper(Node n){
        if (n == null){
            return;
        }
        printHelper(n.left);
        System.out.println("Key:" + n.key + " Value:" + n.val);
        printHelper(n.right);
    }


    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet(){
     Set<K> keys = new HashSet<K>();
     keys = getkeys(keys, root);
     return keys;
    }

    private Set<K> getkeys(Set s, Node n){
        if (n == null){
            return null;
        }
        getkeys(s, n.left);
        s.add(n.key);
        getkeys(s, n.right);
        return s;
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */

    @Override
    public V remove(K key){
        V result = get(key);
        if (result == null){
            return null;
        }
        root = removehelper(root, key);
        size -= 1;
        return result;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value){
        V r = get(key);
        if (r == value){
            return remove(key);
        }
        return null;
    };

    public Node removehelper(Node n, K key){
        if (n == null){
            return null;
        }
        int i = n.key.compareTo(key);
        if (i > 0){
            n.left = removehelper(n.left, key);
        }else if(i < 0){
            n.right = removehelper(n.right, key);
        }else{
            if (n.left == null) {return n.right;}
            if (n.right == null) {return null;}

            Node temp = n;
            n = getmin(temp.right);
            n.right = deletemin(temp.right);
            n.left = temp.left;
        }
        return n;
    }

    private Node getmin(Node n){
        if (n.left == null){
            return n;
        }
        return getmin(n.left);
    }

    private Node deletemin(Node n){
        if (n.left == null){
            return n.right;
        }
        n.left = deletemin(n.left);
        return n;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTIterator();
    }

    private class BSTIterator implements Iterator<K>{
        private int wizPos;
        private K[] keySet;

        public BSTIterator(){
            wizPos = 0;
            Object[] keySet = keySet().toArray();
        }

        public boolean hasNext(){
            return wizPos < size;
        }

        public K next(){
            K returned = keySet[wizPos];
            wizPos += 1;
            return returned;
        }

    }
}
