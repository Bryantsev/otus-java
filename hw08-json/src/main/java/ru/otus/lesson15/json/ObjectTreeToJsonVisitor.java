package ru.otus.lesson15.json;

import ru.otus.lesson15.ObjectTreeVisitor;
import ru.otus.lesson15.fields.ObjectArrayField;
import ru.otus.lesson15.fields.ObjectObjectField;
import ru.otus.lesson15.fields.ObjectPrimitiveField;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;

import static ru.otus.lesson15.ObjectToConvertorUtils.isSimpleValue;

public class ObjectTreeToJsonVisitor implements ObjectTreeVisitor {

    private Object jsonRootObject = null;
    private Object currJsonObject = null;
    private Deque<StackItem> jsonObjectStack = new ArrayDeque<>();

    /**
     * Добавить примитивное значение к текущему объекту с приведением типа
     *
     * @param name  Имя свойства текущего объекта
     * @param value Значение свойства
     */
    private void addValue(String name, Object value) {
        if (currJsonObject == null) {
            return;
        }

        JsonValue jsonValue = getJsonValue(value);

        if (currJsonObject instanceof JsonObjectBuilder) {
            if (jsonValue == null) {
                ((JsonObjectBuilder) currJsonObject).addNull(name);
            } else {
                ((JsonObjectBuilder) currJsonObject).add(name, jsonValue);
            }
        } else if (currJsonObject instanceof JsonArrayBuilder) {
            if (jsonValue == null) {
                ((JsonArrayBuilder) currJsonObject).addNull();
            } else {
                ((JsonArrayBuilder) currJsonObject).add(jsonValue);
            }
        }
    }

    private JsonValue getJsonValue(Object value) {
        if (value == null) {
            return JsonValue.NULL;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? JsonValue.TRUE : JsonValue.FALSE;
        } else if (value instanceof String) {
            return Json.createValue((String) value);
        } else if (value instanceof Enum) {
            return Json.createValue(value.toString());
        } else if (value instanceof Character) {
            return Json.createValue(value.toString());
        } else if (value instanceof Byte) {
            return Json.createValue((Byte) value);
        } else if (value instanceof Short) {
            return Json.createValue((Short) value);
        } else if (value instanceof Integer) {
            return Json.createValue((Integer) value);
        } else if (value instanceof Long) {
            return Json.createValue((Long) value);
        } else if (value instanceof Float) {
            return Json.createValue((Float) value);
        } else if (value instanceof Double) {
            return Json.createValue((Double) value);
        } else if (value instanceof BigInteger) {
            return Json.createValue((BigInteger) value);
        } else if (value instanceof BigDecimal) {
            return Json.createValue((BigDecimal) value);
        }
        return null;
    }

    @Override
    public void visit(ObjectPrimitiveField of) {
        // Если корневой объект не задан, и переданный объект представляет простой тип данных, то сохраним его сразу в корневой объект и выйдем
        // Ожидается, что на этом обход дерева свойств объекта завершится
        final Object obj = of.getObj();
        if (jsonRootObject == null && isSimpleValue(obj)) {
            jsonRootObject = getJsonValue(obj);
            return;
        }
        addValue(of.getName(), obj);
    }

    @Override
    public void visit(ObjectObjectField of) {
        checkRootObject();

        final Object obj = of.getObj();
        // Если корневой объект не задан, и переданный объект представляет простой тип данных, то сохраним его сразу в корневой объект и выйдем
        // Ожидается, что на этом обход дерева свойств объекта завершится
        // if (jsonRootObject == null && isSimpleValue(obj)) {
        //     jsonRootObject = getJsonValue(obj);
        //     return;
        // }

        // Если объект null, то просто выходим
        if (obj == null) {
            return;
        }

        // Если есть "текущий" объект, то добавим к нему значение поля
        if (currJsonObject != null) {
            // Простой тип данных
            if (isSimpleValue(obj)) {
                addValue(of.getName(), obj);

                // Объект
            } else {
                // Не добавляем объект сразу в дерево объектов, т.к. он сразу сбилдится в пустой объект, а позднее добавленные значения полей в нем не отразятся
                // Добавлять будем на финише после добавления значений всех свойств

                // Добавляем текущий объект в стек и делаем текущим новый объект для дальнейшего заполнения его полей
                jsonObjectStack.add(new StackItem(of.getName(), currJsonObject));
                currJsonObject = Json.createObjectBuilder();
            }
            // иначе создаем "текущий" и корневой объект
        } else {
            currJsonObject = Json.createObjectBuilder();
            if (jsonRootObject == null) {
                jsonRootObject = currJsonObject; // Первый билдер становится корнем дерева
            }
        }
    }

    @Override
    public void visit(ObjectArrayField of) {
        checkRootObject();

        if (currJsonObject != null && of.getField() != null && of.getObj() != null) {
            // Добавляем текущий объект в стек
            jsonObjectStack.add(new StackItem(of.getName(), currJsonObject));
        }

        // Делаем текущим новый объект для дальнейшего заполнения его полей
        // Не добавляем массив сразу в дерево объектов, т.к. он сразу сбилдится в пустой массив, а позднее добавленные значения в нем не отразятся
        // Добавлять будем на финише после добавления всех значений
        currJsonObject = Json.createArrayBuilder();
        if (jsonRootObject == null) {
            jsonRootObject = currJsonObject; // Первый билдер становится корнем дерева
        }
    }

    private void checkRootObject() {
        if (jsonRootObject instanceof JsonValue) {
            throw new RuntimeException("Корневой объект содержит простой тип данных. Посещать поля таких типов или другие объекты уже нельзя!");
        }
    }

    @Override
    public void finishVisitComplexObject() {
        final StackItem parentJsonItem = jsonObjectStack.pollLast();
        if (parentJsonItem == null) {
            return; // Такого быть не должно: посетитель перестарался
        }

        // Приводим тип родительского и текущего объектов к требуемым
        final boolean isArrayBuilder = currJsonObject instanceof JsonArrayBuilder;
        final Object fieldValue = parentJsonItem.getFieldValue();
        if (fieldValue instanceof JsonObjectBuilder) {
            if (isArrayBuilder) {
                ((JsonObjectBuilder) fieldValue).add(parentJsonItem.getFieldName(), (JsonArrayBuilder) currJsonObject);
            } else {
                ((JsonObjectBuilder) fieldValue).add(parentJsonItem.getFieldName(), (JsonObjectBuilder) currJsonObject);
            }
        } else if (fieldValue instanceof JsonArrayBuilder) {
            if (isArrayBuilder) {
                ((JsonArrayBuilder) fieldValue).add((JsonArrayBuilder) currJsonObject);
            } else {
                ((JsonArrayBuilder) fieldValue).add((JsonObjectBuilder) currJsonObject);
            }
        }

        currJsonObject = fieldValue; // Делаем текущим объект-родитель
    }

    /**
     * Получить json в виде строки
     */
    public String getJson() {
        if (jsonRootObject == null) {
            return JsonValue.NULL.toString();
        } else if (jsonRootObject instanceof JsonObjectBuilder) {
            return ((JsonObjectBuilder) jsonRootObject).build().toString();
        } else if (jsonRootObject instanceof JsonArrayBuilder) {
            return ((JsonArrayBuilder) jsonRootObject).build().toString();
        } else if (jsonRootObject instanceof JsonValue) {
            return jsonRootObject.toString();
        }
        return null;
    }

    /**
     * Вспомогательный класс для хранения в стеке объектов, с которыми идет работа на данном уровне
     */
    private class StackItem {

        /**
         * Имя поля объекта, если есть
         */
        private String fieldName;
        /**
         * Значение поля
         */
        private Object fieldValue;

        public StackItem(String fieldName, Object fieldValue) {
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Object getFieldValue() {
            return fieldValue;
        }

    }


}
