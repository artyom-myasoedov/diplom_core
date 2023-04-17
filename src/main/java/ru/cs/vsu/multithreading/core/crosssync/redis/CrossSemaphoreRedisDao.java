package ru.cs.vsu.multithreading.core.crosssync.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import ru.cs.vsu.multithreading.core.crosssync.CrossSemaphoreDao;
import ru.cs.vsu.multithreading.core.crosssync.CrossSyncSemaphore;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CrossSemaphoreRedisDao implements CrossSemaphoreDao {
    private static final String KEY_PREFIX = "semaphore#";

    private final Jedis jedis;

    public CrossSemaphoreRedisDao(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public Optional<CrossSyncSemaphore> getById(String id) {
        Map<String, String> fields = jedis.hgetAll(KEY_PREFIX + id);
        if (!fields.isEmpty()) {
            return Optional.of(CrossSyncSemaphore.fromValueMap(fields));
        }
        return Optional.empty();
    }

    @Override
    public CrossSyncSemaphore save(CrossSyncSemaphore semaphore) {
        jedis.hset(KEY_PREFIX + semaphore.getId(), semaphore.toValueMap());
        return semaphore;
    }

    @Override
    public CrossSyncSemaphore createIfNotExists(CrossSyncSemaphore semaphore) {
        Transaction t = jedis.multi();
        Response<Map<String, String>> res = t.hgetAll(KEY_PREFIX + semaphore.getId().toString());
        if (res.get() == null || res.get().size() == 0) {
            t.hset(KEY_PREFIX + semaphore.getId(), semaphore.toValueMap());
        }
        t.exec();
        return semaphore;
    }

    @Override
    public CrossSyncSemaphore release(UUID id) {
        Transaction t = jedis.multi();
        CrossSyncSemaphore css = CrossSyncSemaphore.fromValueMap(t.hgetAll(id.toString()).get());
        css.release();
        t.hset(KEY_PREFIX + css.getId(), css.toValueMap());
        t.exec();
        return css;
    }

    @Override
    public CrossSyncSemaphore acquire(UUID id) {
        Transaction t = jedis.multi();
        CrossSyncSemaphore css;
        boolean res;
        do {
            css = CrossSyncSemaphore.fromValueMap(t.hgetAll(id.toString()).get());
            res = css.tryAcquire();
        } while (!res);
        t.hset(KEY_PREFIX + css.getId(), css.toValueMap());
        t.exec();
        return css;
    }

    @Override
    public Optional<CrossSyncSemaphore> tryAcquire(UUID id) {
        Transaction t = jedis.multi();
        CrossSyncSemaphore css = CrossSyncSemaphore.fromValueMap(t.hgetAll(id.toString()).get());
        boolean res = css.tryAcquire();
        if (res) {
            t.hset(KEY_PREFIX + css.getId(), css.toValueMap());
            t.exec();
            return Optional.of(css);
        } else {
            t.discard();
            return Optional.empty();
        }
    }
}
