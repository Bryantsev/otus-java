package ru.otus.lesson15;

import ru.otus.lesson15.fields.ObjectArrayField;
import ru.otus.lesson15.fields.ObjectObjectField;
import ru.otus.lesson15.fields.ObjectPrimitiveField;
import ru.otus.lesson15.json.ObjectTreeToJsonVisitor;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

/**
 * Класс-конвертор объекта в различные форматы: json и т.п.
 */
public class ObjectToConvertorUtils {

    /**
     * Вернуть объект в виде json
     *
     * @param obj Объект для конвертации в json
     * @return json-строка
     */
    public static String objectToJson(Object obj) throws IllegalAccessException {

        var toJsonVisitor = new ObjectTreeToJsonVisitor(); // Обойдем объект с помощью json-посетителя
        traverseObjectTree(null, obj, toJsonVisitor);

        return toJsonVisitor.getJson();
    }

    /**
     * Вернуть признак простого значения объекта: примитива, перечисления, числа, строки, символа
     */
    public static boolean isSimpleValue(Object obj) {
        Class<?> aClass = obj.getClass();
        return aClass.isPrimitive() || aClass.isEnum() || obj instanceof String || obj instanceof Number || obj instanceof Character;
    }

    /**
     * Обойти дерево объекта с помощью визитера
     *
     * @param mainField Имя поля, к которому относится объект, null - для корневого объекта и массива/коллекции
     * @param object    Объект для обхода: объект, массив или коллекция
     * @param v         Визитер
     */
    private static void traverseObjectTree(Field mainField, Object object, ObjectTreeVisitor v) throws IllegalAccessException {
        if (object == null) {
            return;
        }
        boolean isObjOrArray = false; // маркер комплексных объектов для финиша
        final Class<?> aClass = object.getClass();

        // Массив
        if (aClass.isArray()) {
            new ObjectArrayField(mainField, object).accept(v);
            isObjOrArray = true;

            int size = Array.getLength(object);
            for (int i = 0; i < size; i++) {
                traverseObjectTree(null, Array.get(object, i), v);
            }

            // Коллекция
        } else if (object instanceof Collection<?>) {
            new ObjectArrayField(mainField, object).accept(v);
            isObjOrArray = true;

            for (Object obj : (Collection<?>) object) {
                traverseObjectTree(null, obj, v);
            }

            // Простой тип данных объекта
        } else if (isSimpleValue(object)) {
            new ObjectPrimitiveField(mainField, object).accept(v);

            // Иначе объект
        } else {
            new ObjectObjectField(mainField, object).accept(v);

            // Обходим поля объекта, не являющегося примитивом, перечислением, строкой, числом, символом
            if (!isSimpleValue(object)) {
                isObjOrArray = true;
                // Берем только свойства класса объекта без предков
                // TODO сделать с взятием свойств классов-предков
                for (Field field : aClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    // Игнорируем статик-поля
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    // Объекты с простым типом данных посещаем сразу
                    final Object obj = field.get(object);
                    if (isSimpleValue(obj)) {
                        new ObjectPrimitiveField(field, obj).accept(v);

                        // сложный объект или массив запускаем в рекурсию
                    } else {
                        traverseObjectTree(field, obj, v);
                    }
                }
            }
        }

        // Если посетили сложный объект, то финишируем его
        if (isObjOrArray) {
            v.finishVisitComplexObject();
        }
    }

}
