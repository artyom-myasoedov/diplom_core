package ru.cs.vsu.multithreading.core.schedule;

import ru.cs.vsu.multithreading.core.schedule.task.ScheduleTask;

public interface Schedulable {

    ScheduleTask initScheduleTask();
}
