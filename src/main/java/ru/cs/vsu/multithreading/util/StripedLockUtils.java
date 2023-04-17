package ru.cs.vsu.multithreading.util;

import ru.cs.vsu.multithreading.core.schedule.Scheduler;
import ru.cs.vsu.multithreading.core.schedule.task.ScheduleTask;
import ru.cs.vsu.multithreading.core.stripedlock.DefaultLocker;

import java.util.UUID;

public class StripedLockUtils {

    public static void initCleanningSchedulingForDefaultInstance(long intervalMillis) {
        Scheduler.getInstance()
                .registerTask(ScheduleTask.of(
                        SchedulingIntersectionStrategy.SKIP,
                        intervalMillis == 0 ? 60 * 60 * 1000 : intervalMillis,
                        "DEFAULT",
                        () -> DefaultLocker.getInstance().cleanOldLocks(), UUID.randomUUID().toString(), 0)
                );
        Scheduler.getInstance().start();
    }
}
