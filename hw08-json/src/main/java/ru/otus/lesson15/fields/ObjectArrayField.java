package ru.otus.lesson15.fields;

import ru.otus.lesson15.ObjectTreeVisitor;

import java.lang.reflect.Field;

public class ObjectArrayField extends ObjectField {

    public ObjectArrayField(Field field, Object obj) {
        super(field, obj);
    }

    @Override
    public void accept(ObjectTreeVisitor v) {
        v.visit(this);
    }

}
