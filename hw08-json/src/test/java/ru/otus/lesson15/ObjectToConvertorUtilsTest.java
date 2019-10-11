package ru.otus.lesson15;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import ru.otus.lesson15.test_classes.Person;
import ru.otus.lesson15.test_classes.Phone;
import ru.otus.lesson15.test_classes.PhoneType;

import javax.json.JsonValue;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectToConvertorUtilsTest {

    @Test
    public void saveObjToJsonAndRestore() throws IllegalAccessException {

        // Тест простого объекта с 2мя свойствами: строкой и перечислением
        final var phone = new Phone(PhoneType.MOBILE, "9999999999");
        String jsonStr = ObjectToConvertorUtils.objectToJson(phone);
        System.out.println("\nphone-jsonStr: " + jsonStr);
        // Проверим соответствие полученного json
        assertEquals("{\"phoneType\":\"MOBILE\",\"number\":\"9999999999\"}", jsonStr);

        var gson = new Gson(); // Восстановим объект из json с помощью Gson

        var phoneRestored = gson.fromJson(jsonStr, Phone.class);
        assertEquals(phone, phoneRestored);
        // System.out.println("\nphoneRestored: " + phoneRestored);

        // Тест сложного объекта с примитивными свойствами long и строкой, а также объектом, массивом и коллекцией
        Person person = new Person();
        person.setId(1L);
        person.setFio("Иванов Иван Иванович");
        person.setAge(23);
        person.setNotes(new String[]{"note1", "note2"}); // Добавляем массив заметок
        person.addPhone(phone); // Добавляем телефон в список
        // Задаем этот же телефон в качестве мобильного и домашнего
        person.setMobilePhone(phone);
        person.setHomePhone(phone);

        jsonStr = ObjectToConvertorUtils.objectToJson(person);
        System.out.println("\nperson-jsonStr:\n" + jsonStr);
        // Проверим полученный json
        assertEquals("{\"id\":1,\"fio\":\"Иванов Иван Иванович\",\"age\":23,\"mobilePhone\":{\"phoneType\":\"MOBILE\",\"number\":\"9999999999\"},\"homePhone\":{\"phoneType\":\"MOBILE\",\"number\":\"9999999999\"},\"phones\":[{\"phoneType\":\"MOBILE\",\"number\":\"9999999999\"}],\"notes\":[\"note1\",\"note2\"]}", jsonStr);

        var personRestored = gson.fromJson(jsonStr, Person.class);
        System.out.println("\nperson-object:\n" + person);
        System.out.println("\nperson-object Restored from json:\n" + personRestored);
        assertEquals(person, personRestored, "Восстановленный объект не соответствует исходному!");
    }

    @Test
    public void customTest() throws IllegalAccessException {
        var gson = new Gson();
        assertEquals(gson.toJson(null), JsonValue.NULL.toString());
        assertEquals(gson.toJson(null), ObjectToConvertorUtils.objectToJson(null));
        assertEquals(gson.toJson((byte) 1), ObjectToConvertorUtils.objectToJson((byte) 1));
        assertEquals(gson.toJson((short) 1f), ObjectToConvertorUtils.objectToJson((short) 1f));
        assertEquals(gson.toJson(1), ObjectToConvertorUtils.objectToJson(1));
        assertEquals(gson.toJson(1L), ObjectToConvertorUtils.objectToJson(1L));
        assertEquals(gson.toJson(1f), ObjectToConvertorUtils.objectToJson(1f));
        assertEquals(gson.toJson(1d), ObjectToConvertorUtils.objectToJson(1d));
        assertEquals(gson.toJson("aaa"), ObjectToConvertorUtils.objectToJson("aaa"));
        assertEquals(gson.toJson('a'), ObjectToConvertorUtils.objectToJson('a'));
        assertEquals(gson.toJson(new int[]{1, 2, 3}), ObjectToConvertorUtils.objectToJson(new int[]{1, 2, 3}));
        assertEquals(gson.toJson(List.of(1, 2, 3)), ObjectToConvertorUtils.objectToJson(List.of(1, 2, 3)));
        assertEquals(gson.toJson(Collections.singletonList(1)), ObjectToConvertorUtils.objectToJson(Collections.singletonList(1)));
    }

}
