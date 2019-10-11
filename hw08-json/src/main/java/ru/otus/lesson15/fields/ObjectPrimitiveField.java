package ru.otus.lesson15.fields;

import ru.otus.lesson15.ObjectTreeVisitor;

import java.lang.reflect.Field;

public class ObjectPrimitiveField extends ObjectField {

    public ObjectPrimitiveField(Field field, Object obj) {
        super(field, obj);
    }

    @Override
    public void accept(ObjectTreeVisitor v) {
        v.visit(this);
    }

}
