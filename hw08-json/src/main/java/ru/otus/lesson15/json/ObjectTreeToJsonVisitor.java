package ru.otus.lesson15.json;

import ru.otus.lesson15.ObjectTreeVisitor;
import ru.otus.lesson15.fields.ObjectArrayField;
import ru.otus.lesson15.fields.ObjectObjectField;
import ru.otus.lesson15.fields.ObjectPrimitiveField;

import javax.json.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class ObjectTreeToJsonVisitor implements ObjectTreeVisitor {

    private Object jsonBuilder = null;
    private Object currJsonBuilder = null;
    private Deque<StackItem> jsonBuilderStack = new ArrayDeque<>();

    @Override
    public void visit(ObjectPrimitiveField of) {
        addValue(of.getName(), of.getObj());
    }

    /**
     * Добавить примитивное значение к текущему объекту с приведением типа
     *
     * @param name  Имя свойства текущего объекта
     * @param value Значение свойства
     */
    private void addValue(String name, Object value) {
        if (currJsonBuilder == null) {
            return;
        }

        if (currJsonBuilder instanceof JsonObjectBuilder) {
            if (value == null) {
                ((JsonObjectBuilder) currJsonBuilder).addNull(name);
            } else {
                if (value instanceof Boolean) {
                    ((JsonObjectBuilder) currJsonBuilder).add(name, (Boolean) value);
                } else if (value instanceof String) {
                    ((JsonObjectBuilder) currJsonBuilder).add(name, (String) value);
                } else if (value instanceof Character) {
                    ((JsonObjectBuilder) currJsonBuilder).add(name, (Character) value);
                } else if (value instanceof Byte) {
                    ((JsonObjectBuilder) currJsonBuilder).add(name, (Byte) value);
                } else if (value instanceof Short) {
                    ((JsonObjectBuilder) currJsonBuilder).add(name, (Short) value);
                } else if (value instanceof Integer) {
                    ((JsonObjectBuilder) currJsonBuilder).add(name, (Integer) value);
                } else if (value instanceof Long) {
                    ((JsonObjectBuilder) currJsonBuilder).add(name, (Long) value);
                } else if (value instanceof Float) {
                    ((JsonObjectBuilder) currJsonBuilder).add(name, (Float) value);
                } else if (value instanceof Double) {
                    ((JsonObjectBuilder) currJsonBuilder).add(name, (Double) value);
                }
            }
        } else if (currJsonBuilder instanceof JsonArrayBuilder) {
            if (value == null) {
                ((JsonArrayBuilder) currJsonBuilder).addNull();
            } else {
                if (value instanceof Boolean) {
                    ((JsonArrayBuilder) currJsonBuilder).add((Boolean) value);
                } else if (value instanceof String) {
                    ((JsonArrayBuilder) currJsonBuilder).add((String) value);
                } else if (value instanceof Character) {
                    ((JsonArrayBuilder) currJsonBuilder).add((Character) value);
                } else if (value instanceof Byte) {
                    ((JsonArrayBuilder) currJsonBuilder).add((Byte) value);
                } else if (value instanceof Short) {
                    ((JsonArrayBuilder) currJsonBuilder).add((Short) value);
                } else if (value instanceof Integer) {
                    ((JsonArrayBuilder) currJsonBuilder).add((Integer) value);
                } else if (value instanceof Long) {
                    ((JsonArrayBuilder) currJsonBuilder).add((Long) value);
                } else if (value instanceof Float) {
                    ((JsonArrayBuilder) currJsonBuilder).add((Float) value);
                } else if (value instanceof Double) {
                    ((JsonArrayBuilder) currJsonBuilder).add((Double) value);
                }
            }
        }
    }

    @Override
    public void visit(ObjectObjectField of) {
        final Object obj = of.getObj();
        if (obj == null) {
            return;
        }
        final Class<?> aClass = obj.getClass();
        // Если есть "текущий" объект, то добавим к нему значение поля
        if (currJsonBuilder != null) {
            // Перечисление
            if (aClass.isEnum()) {
                addValue(of.getName(), obj.toString());

                // Строка
            } else if (obj instanceof String) {
                addValue(of.getName(), obj.toString());

                // Объект
            } else {
                // Не добавляем объект сразу в дерево объектов, т.к. он сразу сбилдится в пустой объект, а позднее добавленные значения полей в нем не отразятся
                // Добавлять будем на финише после добавления значений всех свойств

                // Добавляем текущий объект в стек и делаем текущим новый объект для дальнейшего заполнения его полей
                jsonBuilderStack.add(new StackItem(of.getName(), currJsonBuilder));
                currJsonBuilder = Json.createObjectBuilder();
            }
            // иначе создаем "текущий" и корневой объект
        } else {
            currJsonBuilder = Json.createObjectBuilder();
            if (jsonBuilder == null) {
                jsonBuilder = currJsonBuilder; // Первый билдер становится корнем дерева
            }
        }
    }

    @Override
    public void visit(ObjectArrayField of) {
        if (currJsonBuilder != null && of.getField() != null && of.getObj() != null) {
            // Добавляем текущий объект в стек и делаем текущим новый объект для дальнейшего заполнения его полей
            jsonBuilderStack.add(new StackItem(of.getName(), currJsonBuilder));
        }

        // Не добавляем массив сразу в дерево объектов, т.к. он сразу сбилдится в пустой массив, а позднее добавленные значения в нем не отразятся
        // Добавлять будем на финише после добавления всех значений
        currJsonBuilder = Json.createArrayBuilder();
        if (jsonBuilder == null) {
            jsonBuilder = currJsonBuilder; // Первый билдер становится корнем дерева
        }
    }

    @Override
    public void finishVisitComplexObject() {
        final StackItem parentJsonItem = jsonBuilderStack.pollLast();
        if (parentJsonItem == null) {
            return; // Такого быть не должно: посетитель перестарался
        }

        // Приводим тип родительского и текущего объектов к требуемым
        final boolean isArrayBuilder = currJsonBuilder instanceof JsonArrayBuilder;
        final Object fieldValue = parentJsonItem.getFieldValue();
        if (fieldValue instanceof JsonObjectBuilder) {
            if (isArrayBuilder) {
                ((JsonObjectBuilder) fieldValue).add(parentJsonItem.getFieldName(), (JsonArrayBuilder) currJsonBuilder);
            } else {
                ((JsonObjectBuilder) fieldValue).add(parentJsonItem.getFieldName(), (JsonObjectBuilder) currJsonBuilder);
            }
        } else if (fieldValue instanceof JsonArrayBuilder) {
            if (isArrayBuilder) {
                ((JsonArrayBuilder) fieldValue).add((JsonArrayBuilder) currJsonBuilder);
            } else {
                ((JsonArrayBuilder) fieldValue).add((JsonObjectBuilder) currJsonBuilder);
            }
        }

        currJsonBuilder = fieldValue; // Делаем текущим объект-родитель
    }

    /**
     * Получить json в виде строки
     */
    public String getJson() {
        if (jsonBuilder instanceof JsonObjectBuilder) {
            return ((JsonObjectBuilder) jsonBuilder).build().toString();
        } else if (jsonBuilder instanceof JsonArrayBuilder) {
            return ((JsonArrayBuilder) jsonBuilder).build().toString();
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
