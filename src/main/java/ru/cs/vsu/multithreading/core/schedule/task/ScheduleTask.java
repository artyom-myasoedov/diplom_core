package ru.cs.vsu.multithreading.core.schedule.task;

import org.apache.log4j.Logger;
import ru.cs.vsu.multithreading.util.SchedulingIntersectionStrategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class ScheduleTask {
    private static final Logger LOGGER = Logger.getLogger(ScheduleTask.class);

    private final UUID id;
    protected final Runnable runnable;
    private final Date start;
    private final long interval;
    protected volatile boolean isRunning = false;
    protected volatile long nextExecution;

    public ScheduleTask(Runnable runnable, Date start, long interval) {
        this.runnable = runnable;
        this.start = start;
        this.interval = interval;
        this.nextExecution = start.getTime() + interval;
        id = UUID.randomUUID();
    }

    public ScheduleTask(Runnable runnable, Date start, long interval, UUID id) {
        this.runnable = runnable;
        this.start = start;
        this.interval = interval;
        this.nextExecution = start.getTime() + interval;
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public long getInterval() {
        return interval;
    }

    public SchedulingIntersectionStrategy getStrategy() {
        return SchedulingIntersectionStrategy.RUN_IMMEDIATELY;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getNextExecution() {
        return nextExecution;
    }

    protected void execute(ExecutorService executorService) throws Exception {
        executorService.execute(runnable);
        isRunning = false;
        updateNextExecution();
    }

    public void updateNextExecution() {
        long temp = nextExecution + interval;
        nextExecution = temp;
    }

    public boolean isReady() {
        return nextExecution <= new Date().getTime();
    }

    public void prepareAndExecute(ExecutorService executorService) {
        try {
            execute(executorService);
        } catch (Exception e) {
            LOGGER.error("Exception during execution task", e);
        }
    }

    public UUID getId() {
        return id;
    }

    public static ScheduleTask of(SchedulingIntersectionStrategy strategy, long interval, String start, Runnable runnable, String id, int queueSize) {

        if (interval < 100)
            throw new IllegalArgumentException("interval can't be less than 100ms, in this case you should use cycle");
        Date startDate;
        if ("DEFAULT".equals(start)) {
            startDate = new Date();
        } else {
            try {
                startDate = new SimpleDateFormat("dd-MM-yyyyTHH:mm:ss").parse(start);
            } catch (Exception e) {
                LOGGER.error("Invalid start dateTime: " + start, e);
                throw new RuntimeException(e);
            }
        }
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable task can't be null");
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception e) {
            LOGGER.error("Invalid task id", e);
            throw new RuntimeException(e);
        }
        ScheduleTask task;
        switch (strategy) {
            case RUN_IMMEDIATELY: {
                task = new ScheduleTask(runnable, startDate, interval, uuid);
            }
            break;
            case SKIP: {
                task = new ScheduleTaskWithSkipping(runnable, startDate, interval, uuid);
            }
            break;
            case PUT_TO_QUEUE: {
                if (queueSize < 1) throw new IllegalArgumentException("queueSize can't be less than 1");
                task = new ScheduleTaskWithQueue(runnable, startDate, interval, queueSize, uuid);
            }
            break;
            default:
                throw new IllegalArgumentException("Invalid SchedulingIntersectionStrategy: " + strategy);
        }
        return task;
    }
}
