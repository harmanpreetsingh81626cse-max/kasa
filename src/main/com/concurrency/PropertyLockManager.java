package main.com.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class PropertyLockManager {

    private static final ConcurrentHashMap<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();

    public static boolean lockProperty(int propertyId) {
        ReentrantLock lock = locks.computeIfAbsent(propertyId, id -> new ReentrantLock());
        return lock.tryLock();
    }

    public static void unlockProperty(int propertyId) {
        ReentrantLock lock = locks.get(propertyId);

        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    public static boolean isLocked(int propertyId) {
        ReentrantLock lock = locks.get(propertyId);
        return lock != null && lock.isLocked();
    }
}
