import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class SkipListStudentTest {
    private SkipListInterface<Integer> list;
    private CoinFlipper randomness;

    @Before
    public void setup() {
        randomness = new CoinFlipper(new Random(10));
        list = new SkipList<Integer>(randomness);
    }

    @Test(timeout = 200)
    public void testSmall() {
        list.put(5);
        list.put(3);
        list.put(4);

        /*
        List should be
        (null), (5)
        (null), (3), (4), (5)
         */
        assertEquals(new Integer(3), list.first());
        assertEquals(new Integer(5), list.last());

        assertEquals(3, list.size());

        Node<Integer> curr = list.getHead();
        assertEquals(new Integer(5), curr.getNext().getData());
        assertEquals(new Integer(5), curr.getNext().getDown().getData());

        curr = curr.getDown();
        curr = curr.getNext();
        assertEquals(new Integer(3), curr.getData());
        assertNull(curr.getDown());
        assertNull(curr.getUp());

        curr = curr.getNext();
        assertEquals(new Integer(4), curr.getData());
        assertNull(curr.getDown());
        assertNull(curr.getUp());

        curr = curr.getNext();
        assertEquals(new Integer(5), curr.getData());
        assertEquals(new Integer(5), curr.getUp().getData());
        assertNull(curr.getDown());
    }


    @Test(timeout = 200)
    public void testRemove() {
        list.put(5);
        list.put(3);
        list.put(4);


        assertEquals(new Integer(4), list.remove(4));
        /*
        List should be
        (null), (5)
        (null), (3), (5)
         */

        assertEquals(new Integer(3), list.first());
        assertEquals(new Integer(5), list.last());

        assertEquals(2, list.size());

        Node<Integer> curr = list.getHead();
        assertEquals(new Integer(5), curr.getNext().getData());
        assertEquals(new Integer(5), curr.getNext().getDown().getData());

        curr = curr.getDown();
        curr = curr.getNext();
        assertEquals(new Integer(3), curr.getData());
        assertNull(curr.getDown());
        assertNull(curr.getUp());

        curr = curr.getNext();
        assertEquals(new Integer(5), curr.getData());
        assertEquals(new Integer(5), curr.getUp().getData());
        assertNull(curr.getDown());




        assertEquals(new Integer(5), list.remove(5));
        /*
        List should be
        (null), (3)
         */
        assertEquals(new Integer(3), list.first());
        assertEquals(new Integer(3), list.last());

        assertEquals(1, list.size());

        curr = list.getHead();
        assertEquals(new Integer(3), curr.getNext().getData());
        assertNull(curr.getDown());
        assertNull(curr.getUp());




        assertEquals(new Integer(3), list.remove(3));
        /*
        List should be
        (null)
         */
        assertEquals(0, list.size());

    }
}