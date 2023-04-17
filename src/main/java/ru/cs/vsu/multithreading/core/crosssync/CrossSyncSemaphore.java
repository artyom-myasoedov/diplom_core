package ru.cs.vsu.multithreading.core.crosssync;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CrossSyncSemaphore {

    private final UUID id;
    private AtomicInteger currentPermits;
    private final int maxPermits;
    private volatile long lastUpdateTimestamp;

    private CrossSyncSemaphore(UUID id, int currentPermits, int maxPermits) {
        this.id = id;
        this.currentPermits = new AtomicInteger(currentPermits);
        this.maxPermits = maxPermits;
        this.lastUpdateTimestamp = Instant.now().toEpochMilli();
    }

    private CrossSyncSemaphore(UUID id, int currentPermits, int maxPermits, long lastUpdateTimestamp) {
        this.id = id;
        this.currentPermits = new AtomicInteger(currentPermits);
        this.maxPermits = maxPermits;
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public int getCurrentPermits() {
        return currentPermits.get();
    }

    public int getMaxPermits() {
        return maxPermits;
    }

    public UUID getId() {
        return id;
    }

    public void release() {
        int next = currentPermits.get() + 1;
        currentPermits.set(Math.max(next, maxPermits));
        updateTime();
    }

    public boolean tryAcquire() {
        if (currentPermits.get() == 0) return false;
        currentPermits.decrementAndGet();
        updateTime();
        return true;
    }

    public Map<String, String> toValueMap() {
        return Map.of("id", id.toString(), "currentPermits", String.valueOf(currentPermits), "maxPermits", String.valueOf(maxPermits), "lastUpdateTimestamp", String.valueOf(lastUpdateTimestamp));
    }

    private void updateTime() {lastUpdateTimestamp = Instant.now().toEpochMilli();}

    public static CrossSyncSemaphore fromValueMap(Map<String, String> valueMap) {
        return newInstance(UUID.fromString(valueMap.get("id")), Integer.parseInt(valueMap.getOrDefault("currentPermits", "0")), Integer.parseInt(valueMap.getOrDefault("maxPermits", "0")), Long.parseLong(valueMap.getOrDefault("lastUpdateTimestamp", "0")));
    }

    public static CrossSyncSemaphore newInstance(UUID id, int currentPermits, int maxPermits, long lastUpdateTimestamp) {
        if (id == null) throw new IllegalArgumentException("id can't be null");
        return new CrossSyncSemaphore(id, currentPermits, maxPermits, lastUpdateTimestamp);
    }

    public static CrossSyncSemaphore newInstance(UUID id, int currentPermits, int maxPermits) {
        if (id == null) throw new IllegalArgumentException("id can't be null");
        return new CrossSyncSemaphore(id, currentPermits, maxPermits);
    }

    public static CrossSyncSemaphore newInstance(int currentPermits, int maxPermits) {
        return new CrossSyncSemaphore(UUID.randomUUID(), currentPermits, maxPermits);
    }
}
