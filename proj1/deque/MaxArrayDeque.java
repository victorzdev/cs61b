package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c){
        super();
        comparator = c;
    }

    public T max(){
        return max(comparator);
    }

    public T max(Comparator<T> c){
        if (size() == 0){
            return null;
        }
        T biggest = this.get(0);
        for (T item: this){
            if (c.compare(item, biggest) > 0){
                biggest = item;
            }
        }
        return biggest;
    }

    private static class intComparator implements Comparator<Integer>{
        public int compare(Integer i1, Integer i2){
            return i1 - i2;
        }
    }

    public static intComparator getintCompartor(){
        return new intComparator();
    }



    public static void main(String[] args){
        Comparator c = getintCompartor();
        MaxArrayDeque<Integer> a = new MaxArrayDeque<>(c);
        a.addFirst(1);
        a.addFirst(5);
        a.addFirst(4);
        a.addFirst(20);
        a.addFirst(6);
        System.out.println(a.max());

    }
}
