package ru.cs.vsu.multithreading.core.schedule;

import org.reflections.Reflections;

import java.util.Arrays;

public class SchedulerUtils {

    public static void registerAllSchedulable() {
        Scheduler scheduler = Scheduler.getInstance();
        Arrays.stream(Thread.currentThread().getContextClassLoader().getDefinedPackages())
                .map(pack -> new Reflections(pack.getName()))
                .flatMap(reflection ->
                        reflection.getSubTypesOf(Schedulable.class).stream()
                                .map((Class<? extends Schedulable> it) -> {
                                    try {
                                        return (Schedulable) Arrays.stream(it.getDeclaredConstructors())
                                                .filter(constructor -> constructor.getParameterCount() == 0)
                                                .findFirst().orElseThrow(() -> new IllegalStateException("Class: " + it.getName() + " implements ru.cs.vsu.multithreading.core.schedule.Schedulable must have no args constructor"))
                                                .newInstance();
                                    } catch (Exception e) {
                                        throw new RuntimeException(e.getMessage());
                                    }
                                })).forEach(it -> scheduler.registerTask(it.initScheduleTask()));
        scheduler.start();
    }
}
