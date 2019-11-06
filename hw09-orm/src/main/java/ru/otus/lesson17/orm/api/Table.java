package ru.otus.lesson17.orm.api;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Признак, что класс сохраняется в таблицу
 */
@Retention(RUNTIME)
public @interface Table {

    /**
     * Имя таблицы, соответствующей классу. По умолчанию пусто - в этом случае имя таблицы берется по имени класса
     */
    String value() default "";

}
