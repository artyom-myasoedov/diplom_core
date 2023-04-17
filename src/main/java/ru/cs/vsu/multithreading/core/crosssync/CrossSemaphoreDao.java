package ru.cs.vsu.multithreading.core.crosssync;

import java.util.Optional;
import java.util.UUID;

public interface CrossSemaphoreDao {

    Optional<CrossSyncSemaphore> getById(String id);

    CrossSyncSemaphore save(CrossSyncSemaphore semaphore);

    CrossSyncSemaphore createIfNotExists(CrossSyncSemaphore semaphore);

    CrossSyncSemaphore release(UUID id);

    CrossSyncSemaphore acquire(UUID id);

    Optional<CrossSyncSemaphore> tryAcquire(UUID id);

}
