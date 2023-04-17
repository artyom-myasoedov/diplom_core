package ru.cs.vsu.multithreading.core.schedule;

import ru.cs.vsu.multithreading.core.schedule.task.ScheduleTask;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private volatile static Scheduler INSTANCE;

    private final List<ScheduleTask> tasks;

    private final ThreadPoolExecutor executor;
    private volatile boolean isRunning = false;

    public Scheduler(List<ScheduleTask> tasks, ThreadPoolExecutor executor) {
        this.tasks = new CopyOnWriteArrayList<>(tasks);
        this.executor = executor;
    }

    public List<ScheduleTask> getTasks() {
        return tasks;
    }

    public void start() {
        if (!isRunning) {
            executor.execute(() -> {
                while (true) {
                    tasks.forEach(this::executeIfNeed);
                }
            });
            isRunning = true;
        }
    }

    public void stop() {
        executor.shutdown();
        isRunning = false;
    }

    public void registerTask(ScheduleTask task) {
        tasks.add(task);
    }

    private void executeIfNeed(ScheduleTask task) {
        if (task.isReady()) {
            task.prepareAndExecute(executor);
        }
    }

    public static Scheduler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Scheduler(
                    List.of(),
                    new ThreadPoolExecutor(32, 64, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100)));
        }
        return INSTANCE;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
