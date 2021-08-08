package app.cacheservice.dao;

import app.cacheservice.model.CachePayLoad;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RPriorityQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedissonDAO<K, V extends CachePayLoad> {

    @Autowired
    private RedissonClient redisson;

    public RMap<String, V> getMap(String key) {
        return redisson.getMap(key);
    }

    public V put(String redisHash, String key, V object) {
        return getMap(redisHash).put(key, object);
    }

    public RFuture<V> putAsync(String redisHash, String key, V object) {
        return getMap(redisHash).putAsync(key, object);
    }

    public boolean fastPut(String redisHash, String key, V object) {
        return getMap(redisHash).fastPut(key, object);
    }

    public Object remove(String redisHash, String key) {
        return getMap(redisHash).remove(key);
    }

    public Object get(String redisHash, String key) {
        return getMap(redisHash).get(key);
    }

    public boolean addInPriorityQueue(String redisHash, V object) {
        RMapCache<String, Object> rMap = redisson.getMapCache(redisHash);
        RLock lock = rMap.getLock(object.getKey());
        try {
            lock.lock();
            log.info("current running thread::"+Thread.currentThread().getName());
            RPriorityQueue queue = (RPriorityQueue) rMap.get(object.getKey());
            if (Objects.isNull(queue)) {
                queue = redisson.getPriorityBlockingQueue(object.getKey());
                rMap.put(object.getKey(), queue, 24, TimeUnit.HOURS);
            }
            queue.offer(object);
        } finally {
            lock.unlock();
        }
        return true;
    }

    public RPriorityQueue getPriorityQueue(String redisHash, String key) {
        RMap<String, V> rMap = getMap(redisHash);
        return (RPriorityQueue) rMap.get(key);
    }

    public boolean addInUtilPriorityQueue(String redisHash, V object) {
        RMap<String, Object> rMap = redisson.getMap(redisHash);
        RLock lock = rMap.getLock(object.getKey());
        try {
            lock.lock();
            PriorityQueue queue = (PriorityQueue) rMap.get(object.getKey());
            if (Objects.isNull(queue)) {
                if (!(object instanceof Comparable)) {
                    new PriorityQueue((Comparator<V>) (o1, o2) -> o1.getKey().compareTo(o2.getKey()) + Long.compare(o1.getTimestamp(), o2.getTimestamp()));
                } else {
                    queue = new PriorityQueue();
                }
            }
            queue.offer(object);

            rMap.put(object.getKey(), queue);
        } finally {
            lock.unlock();
        }

        return true;
    }

    public PriorityQueue getUtilPriorityQueue(String redisHash, String key) {
        RMap<String, Object> rMap = redisson.getMap(redisHash);
        return (PriorityQueue) rMap.get(key);
    }
}
