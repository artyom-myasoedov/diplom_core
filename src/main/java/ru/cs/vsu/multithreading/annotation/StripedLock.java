package ru.cs.vsu.multithreading.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface StripedLock {

    /**
     * Обращение к идентификатору, по которому лочить данные, например "arg.getId()", где arg аргумент метода.
     */
    String lockIdentifier();
}
