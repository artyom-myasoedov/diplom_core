package ru.cs.vsu.multithreading.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface CrossSemaphore {

    /**
     * Идентификатор семафора во внешнем источнике.
     */
    String semophoreId();
    /**
     * Кол-во допустимых разрешений к ресурсу, игнорируется если семафор уже создан.
     */
    int permits() default 1;
}
