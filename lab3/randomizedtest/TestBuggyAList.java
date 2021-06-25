package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing nrlist = new AListNoResizing<Integer>();
        BuggyAList blist = new BuggyAList<Integer>();

        nrlist.addLast(4);
        nrlist.addLast(5);
        nrlist.addLast(6);

        blist.addLast(4);
        blist.addLast(5);
        blist.addLast(6);

        Assert.assertEquals(nrlist.size(), blist.size());
        Assert.assertEquals(nrlist.removeLast(), blist.removeLast());
        Assert.assertEquals(nrlist.removeLast(), blist.removeLast());
        Assert.assertEquals(nrlist.removeLast(), blist.removeLast());
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int sizeb = B.size();
                Assert.assertEquals(size, sizeb);
                System.out.println("size: " + size);
            }else if(operationNumber == 2 && L.size() > 0){
                int l = L.getLast();
                int b = B.getLast();
                Assert.assertEquals(l, b);
                System.out.println("last: " + l);
            }else if(operationNumber == 3 && L.size() > 0){
                int l = L.removeLast();
                int b = B.removeLast();
                Assert.assertEquals(l, b);
                System.out.println("remove last: " + l);
            }
        }
    }
}

