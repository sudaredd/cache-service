package app.cacheservice.util;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RedissionLocks {

    private List<Lock> locks;

    @PostConstruct
    public void init() {
        locks = IntStream.range(0, 20)
            .mapToObj(i -> i)
            .map(i -> new ReentrantLock())
            .collect(Collectors.toUnmodifiableList());
    }

    public Lock evaluate(String key) {
        int index = (Math.abs(key.hashCode()) % locks.size());
        System.out.printf("index %s for a key %s", index, key);
        System.out.println();
        return locks.get(index);
    }

    public static void main(String[] args) {
        RedissionLocks redissionLocks = new RedissionLocks();
        redissionLocks.init();
        for(int i=0; i<100000; i++) {
            System.out.println(redissionLocks.evaluate("Message"+i).getClass());
        }
    }
}
