package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int initialSize;
    private double loadFactor;
    private int size;
    private HashSet<K> kset = new HashSet<>();

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        this.size = 0;
        this.buckets = createTable(this.initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    /** Removes all of the mappings from this map. */
    public void clear(){
        this.size = 0;
        buckets = createTable(initialSize);
    };

    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        int hashNum = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[hashNum];
        if (bucket == null){
            return false;
        }
        for(Node n: bucket){
            if (n.key.equals(key)){
                return true;
            }
        }
        return false;
    };

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key){
        if (containsKey(key)) {
            int hashNum = Math.floorMod(key.hashCode(), buckets.length);
            Collection<Node> bucket = buckets[hashNum];
            for (Node n: bucket){
                if (n.key.equals(key)){
                    return n.value;
                }
            }
        }
        return null;
    };

    /** Returns the number of key-value mappings in this map. */
    public int size(){
        return this.size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */

    public void resize(int newsize){
        MyHashMap<K, V> newHM = new MyHashMap<>(newsize);
        for (K key: keySet()){
            V value = get(key);
            newHM.put(key, value);
        }
        buckets = newHM.buckets;
    }

    public void put(K key, V value){
        if (size() / buckets.length > loadFactor){
            resize(buckets.length * 2);
        }
        int hashNum = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[hashNum];
        if (bucket == null){
            buckets[hashNum] = createBucket();
            bucket = buckets[hashNum];
        }
        for (Node n: bucket){
            if (n.key == key){
                n.value = value;
                return;
            }
        }
        Node n = new Node(key, value);
        bucket.add(n);
        size += 1;
        kset.add(key);
    };

    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet(){
        return kset;
    };

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key){
        int hsnum = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[hsnum];
        for (Node n: bucket){
            if (n.key.equals(key)) {
                size = size - 1;
                bucket.remove(n);
                kset.remove(key);
                return  n.value;
            }
        }

        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value){
        V removeV = get(key);
        if (removeV.equals(value)) {
            return remove(key);
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new HMIterator();
    }

    private class HMIterator implements Iterator<K>{
        private int wizPos;
        private K[] keySet;

        public HMIterator(){
            wizPos = 0;
            Object[] keySet = keySet().toArray();
        }

        public boolean hasNext(){
            return wizPos < size();
        }

        public K next(){
            K returned = keySet[wizPos];
            wizPos += 1;
            return returned;
        }

    }

//    public static void main(String[] args) {
//        MyHashMap<Integer, Integer> a = new MyHashMap<Integer, Integer>();
//        a.put(100,100);
//        a.put(100-16, 20);
//        System.out.println(a.keySet());
//        System.out.println(a.buckets.length);
//    }
}
