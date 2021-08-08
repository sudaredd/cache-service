package app.cacheservice;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Iteratortest {

    @Test
    public void testRemove() {
        List<Integer> list = new ArrayList<>(List.of(1,2,3,4,5));
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            try {
                Integer next = iterator.next();
                System.out.println(next);
                if(next == 3) {
                    throw new ArithmeticException("not divide 3");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                iterator.remove();
            }

        }

        System.out.println(list);
    }
}
