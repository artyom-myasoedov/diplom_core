package ru.cs.vsu.multithreading.core.stripedlock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultLocker {

    private static volatile DefaultLocker INSTANCE;

    private final ConcurrentHashMap<String, TimeReentrantLock> locks;
    private final long timeToLiveLock;

    public DefaultLocker(long timeToLiveLock) {
        this.timeToLiveLock = timeToLiveLock;
        this.locks = new ConcurrentHashMap<>();

    }

    public void lock(String id) {
        locks.computeIfAbsent(id, it -> new TimeReentrantLock(timeToLiveLock)).lock();
    }

    public boolean tryLock(String id) {
        return locks.computeIfAbsent(id, it -> new TimeReentrantLock(timeToLiveLock)).tryLock();
    }

    public void unlock(String id) {
        locks.computeIfPresent(id, (key, lock) -> {
            lock.unlock();
            return lock;
        });
    }

    public boolean isLocked(String id) {
        return locks.getOrDefault(id, new TimeReentrantLock(0)).isLocked();
    }

    public void cleanOldLocks() {
        var oldLocksToCleanId = locks.entrySet()
                .stream().filter(it -> !it.getValue().isLocked() && it.getValue().isExpired()).map(Map.Entry::getKey)
                .collect(Collectors.toList());

        oldLocksToCleanId.forEach(locks::remove);
    }

    public long getTimeToLiveLock() {
        return timeToLiveLock;
    }

    public static DefaultLocker getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultLocker(60 * 60 * 10000);
        }
        return INSTANCE;
    }
}
