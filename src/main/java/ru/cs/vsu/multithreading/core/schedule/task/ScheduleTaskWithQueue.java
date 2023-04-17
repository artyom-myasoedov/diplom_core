package ru.cs.vsu.multithreading.core.schedule.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cs.vsu.multithreading.util.SchedulingIntersectionStrategy;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;

public class ScheduleTaskWithQueue extends ScheduleTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskWithQueue.class);


    private final BlockingQueue<Runnable> tasks;


    public ScheduleTaskWithQueue(Runnable runnable, Date start, long interval, int queueSize) {
        this(runnable, start, interval, new LinkedBlockingQueue<>(queueSize), UUID.randomUUID());
    }

    public ScheduleTaskWithQueue(Runnable runnable, Date start, long interval, int queueSize, UUID id) {
        this(runnable, start, interval, new LinkedBlockingQueue<>(queueSize), id);
    }

    public ScheduleTaskWithQueue(Runnable runnable, Date start, long interval, BlockingQueue<Runnable> tasks, UUID id) {
        super(runnable, start, interval, id);
        this.tasks = tasks;
    }


    @Override
    public SchedulingIntersectionStrategy getStrategy() {
        return SchedulingIntersectionStrategy.PUT_TO_QUEUE;
    }

    @Override
    protected void execute(ExecutorService executorService) throws InterruptedException, ExecutionException {
        if (isRunning) {
            tasks.add(runnable);
        } else {
            isRunning = true;
            executorService.execute(() -> {
                updateNextExecution();
                runnable.run();
                while (!tasks.isEmpty()) {
                    try {
                        tasks.take().run();
                    } catch (InterruptedException e) {
                        LOGGER.error("ERROR", e);
                    }
                }
            });

        }
    }
}
