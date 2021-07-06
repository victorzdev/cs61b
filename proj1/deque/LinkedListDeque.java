package deque;


import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T>{

    private class IntNode {
        public T item;
        public IntNode prev;
        public IntNode next;

        public IntNode(T i, IntNode p, IntNode n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    private IntNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new IntNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public LinkedListDeque(T x) {
        sentinel = new IntNode(null, null, null);
        sentinel.next = new IntNode(x, sentinel, sentinel);
        sentinel.prev = sentinel.next;
        size = 1;
    }

    public void addFirst(T item){
        sentinel.next = new IntNode(item, sentinel, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size++;
    }

    public void addLast(T item){
        sentinel.prev = new IntNode(item, sentinel.prev, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size++;
    }

    public T removeFirst(){
        if (isEmpty()){
            return  null;
        }
        T first = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return first;
    }

    public T removeLast(){
        if (isEmpty()){
            return  null;
        }
        T last = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return last;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        IntNode ind = sentinel;
        for (int i = 0; i < size; i++){
            System.out.printf("%s ", ind.next.item);
            ind = ind.next;
        }
        System.out.println();
    }

    public T get(int index){
        if (index >= size){
            return null;
        }

        IntNode ind = sentinel.next;
        for (int i = 0; i < size; i++){
            if (i == index){
                return ind.item;
            }
            ind = ind.next;
        }
        return sentinel.next.item;

    }

    private T getRHelper(int index, IntNode temp){
        if (index == 0){
            return temp.item;
        }
        return getRHelper(index - 1, temp.next);
    }

    public T getRecursive(int index){
        if (index >= size){
            return null;
        }
        T r = getRHelper(index, sentinel.next);
        return r;
    }

    public Iterator<T> iterator(){
        return new LinkedListDeque.LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T>{
        private int wizPos;

        public LinkedListDequeIterator(){
            wizPos = 0;
        }

        public boolean hasNext(){
            return wizPos < size;
        }

        public T next(){
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object other){
        LinkedListDeque<T> o = (LinkedListDeque<T>) other;
        if (o instanceof LinkedListDeque && o.size == size){
            for (int i = 0; i < size; i++){
                if (!o.get(i).equals(this.get(i))){
                    return false;
                }
            }
        }else {
            return false;
        }
        return true;
    }


    public static void main(String[] args) {
        /* Creates a list of one integer, namely 10 */
        LinkedListDeque<Integer> L = new LinkedListDeque<Integer>();
        LinkedListDeque<Integer> L1 = new LinkedListDeque<Integer>();
        L.addLast(1);
        boolean t1 = L.equals(L1);
        L1.addLast(1);

        L.addLast(2);
        L1.addLast(2);

        L.addLast(3);
        L1.addLast(3);
        t1 = L1.equals(L);
        for (int t: L){
            System.out.println(t);
        }
        L.printDeque();
    }
}