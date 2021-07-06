package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>{
    private T[] items;
    int nextFirst;
    int nextLast;
    private int size;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        nextFirst = 4;
        nextLast = 5;
        size = 0;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        System.arraycopy(items, 0, a, 0, nextLast);
        System.arraycopy(items, nextLast + 1, a, capacity - (size - nextLast), size - nextLast);
        nextFirst = capacity - 1 - (size - nextLast);
        items = a;
    }


    public void addFirst(T item){
        if (nextFirst == nextLast){
            resize(size * 4);
        }
        items[nextFirst] = item;
        if (nextFirst == 0){
            nextFirst = items.length - 1;
        }else{
            nextFirst = nextFirst - 1;
        }
        size++;
    }

    public void addLast(T item){
        if (nextFirst == nextLast){
            resize(size * 4);
        }
        items[nextLast] = item;
        if (nextLast == items.length - 1){
            nextLast = 0;
        }else{
            nextLast = nextLast + 1;
        }
        size++;
    }

    private void downSize(){
        if (size < items.length * 0.25 && items.length >= 16){
            T[] a = (T[]) new Object[(int) Math.ceil(items.length * 0.25)];
            if (nextFirst < nextLast){
                System.arraycopy(items, nextFirst + 1, a, 0, nextLast - nextFirst - 1);
                nextLast = nextLast - nextFirst - 1;
                nextFirst = a.length - 1;
            }else{
                System.arraycopy(items, 0, a, 0, nextLast);
                System.arraycopy(items, nextFirst + 1, a, a.length - (items.length - 1 - nextFirst), items.length - 1 - nextFirst);
                nextFirst = a.length - 1 - (items.length - 1 - nextFirst);
            }
            items = a;
        }
    }

    public T removeFirst(){
        if (isEmpty()){
            return null;
        }
        int firstLoc = nextFirst + 1;
        if (firstLoc > items.length - 1){
            firstLoc = 0;
        }
        T firstItems = items[firstLoc];
        items[firstLoc] = null;
        size--;
        nextFirst = firstLoc;
        downSize();
        return firstItems;
    }

    public T removeLast(){
        if (isEmpty()){
            return null;
        }
        int lastLoc = nextLast - 1;
        if (lastLoc < 0){
            lastLoc = items.length - 1;
        }
        T lastItems = items[lastLoc];
        items[lastLoc] = null;
        size--;
        nextLast = lastLoc;
        downSize();
        return lastItems;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        for (int i = 0; i < size; i++){
            System.out.printf("%s ", get(i));
        }
        System.out.println();
    }

    private int convertIndex(int index){
        int rIndex = index + nextFirst + 1;
        if (rIndex > items.length - 1){
            rIndex = rIndex - items.length;
        }
        return rIndex;
    }

    public T get(int index){
        if (index >= size){
            return null;
        }
        return items[convertIndex(index)];
    }

    public Iterator<T> iterator(){
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>{
        private int wizPos;

        public ArrayDequeIterator(){
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
        ArrayDeque<T> o = (ArrayDeque<T>) other;
        if (o instanceof ArrayDeque && o.size == size){
            for (int i = 0; i < size; i++) {
                if (!o.get(i).equals(this.get(i))) {
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
        ArrayDeque<Integer> AL = new ArrayDeque<>();
        ArrayDeque<Integer> AL1 = new ArrayDeque<>();

        boolean t1 = AL1.equals(AL);
        AL1.addFirst(19);
        t1 = AL1.equals(AL);
        AL.addFirst(5);
        AL.addLast(6);
        AL.addFirst(7);
        AL.addLast(8);
        AL.addFirst(9);
        AL.addLast(10);
        AL.addFirst(12);
        AL.addLast(4);
//        AL.printDeque();
//        System.out.println(1);
        for (int t: AL){
            System.out.println(t);
        }
    }
}
