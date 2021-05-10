package app.cacheservice.dao;

import app.cacheservice.model.CachePayLoad;
import org.junit.jupiter.api.Test;
import org.redisson.api.RPriorityQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.PriorityQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class RedissonDAOTest {


    @Autowired
    private RedissonDAO redissonDAO;

    String redishHash = "REDIS";

    @Test
    void getMap() {
    }

    @Test
    void put() {
     /*   CachePayLoad cachePayLoad = new CachePayLoad("123", System.currentTimeMillis(), "Payload1");
        radissonDAO.put("ORDERS", cachePayLoad.getKey(), cachePayLoad);
*/
        Object payload = redissonDAO.get("ORDERS", "123");
        System.out.println(payload);
    }

    @Test
    public void addInSortingOrder() throws InterruptedException {
        RPriorityQueue priorityQueue = redissonDAO.getPriorityQueue(redishHash, "123");
        if (priorityQueue != null) {
            priorityQueue.clear();
        }
        long timeMillis = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(4);
        executorService.submit(()-> runTask(timeMillis, 2, "Payload1 Delete", countDownLatch));
        executorService.submit(()-> runTask(timeMillis, 1, "Payload1 CHange", countDownLatch));
        executorService.submit(()-> runTask(timeMillis, 0, "Payload1 Add", countDownLatch));
        executorService.submit(()-> runTask(timeMillis, 0, "Payload1 Add2", countDownLatch));
        countDownLatch.await();
        priorityQueue = redissonDAO.getPriorityQueue(redishHash, "123");
        System.out.println("priorityQueue::"+priorityQueue);
        executorService.shutdownNow();
    }

    private void runTask(long timeMillis, long i, String s, CountDownLatch countDownLatch) {
        redissonDAO.addInPriorityQueue(redishHash, new CachePayLoad("123", timeMillis + i, s));
        countDownLatch.countDown();
    }

    @Test
    public void addInUtilSortingOrder() {
        PriorityQueue priorityQueue = redissonDAO.getUtilPriorityQueue(redishHash, "1234");
        if (priorityQueue != null) {
            priorityQueue.clear();
            redissonDAO.remove(redishHash, "1234");
        }
        long timeMillis = System.currentTimeMillis();
        redissonDAO.addInUtilPriorityQueue(redishHash, new CachePayLoad("1234", timeMillis + 1, "Payload1 CHange"));
        redissonDAO.addInUtilPriorityQueue(redishHash, new CachePayLoad("1234", timeMillis, "Payload1 Add"));
        redissonDAO.addInUtilPriorityQueue(redishHash, new CachePayLoad("1234", timeMillis + 2, "Payload1 Delete"));

        priorityQueue = redissonDAO.getUtilPriorityQueue(redishHash, "1234");
        System.out.println(priorityQueue);
    }

    @Test
    void putAsync() {
    }

    @Test
    void fastPut() {
    }

    @Test
    void remove() {
    }

    @Test
    void addInPriorityQueue() {
    }
}