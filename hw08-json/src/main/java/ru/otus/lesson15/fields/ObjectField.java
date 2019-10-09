package ru.otus.lesson15.fields;

import ru.otus.lesson15.ObjectTreeVisitor;

import java.lang.reflect.Field;

public abstract class ObjectField {

    protected final Field field;
    protected final Object obj;

    public ObjectField(Field field, Object obj) {
        this.field = field;
        this.obj = obj;
    }

    public Field getField() {
        return field;
    }

    public Object getObj() {
        return obj;
    }

    public abstract void accept(ObjectTreeVisitor v);

    public String getName() {
        return field == null ? "null" : field.getName();
    }

}
