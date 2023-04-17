package ru.cs.vsu.multithreading.annotation;


import ru.cs.vsu.multithreading.util.SchedulingIntersectionStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Scheduled {
    /**
     * Интервал запуска задач в миллисекундах
     */
    long interval() default 1000;
    /**
     * Время начала выполнения задачи по расписанию(время первого запуска) в формате dd-MM-yyyy HH:mm:ss.
     */
    String start() default "DEFAULT";
    /**
     * Стратегия запуска задач, если исполнение текущей не завершено.
     */
    SchedulingIntersectionStrategy strategy() default SchedulingIntersectionStrategy.RUN_IMMEDIATELY;
    /**
     * Идентификатор задачи.
     */
    String id() default "11111111-1111-1111-1111-111111111111";
    /**
     *  Размер очереди для задач(только если стратегия - PUT_TO_QUEUE).
     */
    int queueSize() default 100;
}
