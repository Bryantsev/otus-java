package ru.otus.lesson17.orm.api;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Признак, что поле класса сохраняется в колонку таблицы
 */
@Retention(RUNTIME)
public @interface Column {
    /**
     * Имя колонки в таблице, соответствующее полю класса. По умолчанию пусто - в этом случае имя колонки берется по имени поля
     */
    String value() default "";

}
