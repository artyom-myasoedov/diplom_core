package ru.cs.vsu.multithreading.core.schedule.task;

import ru.cs.vsu.multithreading.util.SchedulingIntersectionStrategy;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class ScheduleTaskWithSkipping extends ScheduleTask {
    public ScheduleTaskWithSkipping(Runnable runnable, Date start, long interval, UUID id) {
        super(runnable, start, interval, id);
    }

    public ScheduleTaskWithSkipping(Runnable runnable, Date start, long interval) {
        super(runnable, start, interval, UUID.randomUUID());
    }

    public SchedulingIntersectionStrategy getStrategy() {
        return SchedulingIntersectionStrategy.SKIP;
    }

    protected void execute(ExecutorService executorService) {
        if (!isRunning) {
            isRunning = true;
            executorService.execute(() -> {
                updateNextExecution();
                runnable.run();
                isRunning = false;
            });
        }
    }
}
