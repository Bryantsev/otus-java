package ru.otus.lesson15;

import ru.otus.lesson15.fields.ObjectArrayField;
import ru.otus.lesson15.fields.ObjectObjectField;
import ru.otus.lesson15.fields.ObjectPrimitiveField;

public interface ObjectTreeVisitor {

    /**
     * Посетить "примитивное" значение
     */
    public void visit(ObjectPrimitiveField of);

    /**
     * Посетить объект, включая строки и перечисления
     */
    public void visit(ObjectObjectField of);

    /**
     * Посетить массив или наследника Collection
     */
    public void visit(ObjectArrayField of);

    /**
     * Зафиксировать окончание обхода сложного объекта: массива или объекта, кроме строк и перечислений
     */
    public void finishVisitComplexObject();

}
