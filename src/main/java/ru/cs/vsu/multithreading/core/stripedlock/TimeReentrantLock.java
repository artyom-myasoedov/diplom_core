package ru.cs.vsu.multithreading.core.stripedlock;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public class TimeReentrantLock extends ReentrantLock {

    private final Date sysCreateTime;
    private final long expirationTimeMillis;

    public TimeReentrantLock(long timeToLive) {
        this.sysCreateTime = new Date();
        expirationTimeMillis = sysCreateTime.getTime() + timeToLive;
    }

    public Date getSysCreateTime() {
        return sysCreateTime;
    }

    public boolean isExpired() {
        return new Date().getTime() > expirationTimeMillis;
    }
}
