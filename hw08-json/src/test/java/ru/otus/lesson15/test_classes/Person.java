package ru.otus.lesson15.test_classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Человек. Класс для тестов. Содержит примитивный тип long, строку, 2 объекта Phone для проверки восстановления одного и того же телефона в один объект,
 * список телефонов и массив заметок
 */
public class Person {

    private long id;
    private String fio;
    private Integer age;
    private Phone mobilePhone;
    private Phone homePhone;
    private List<Phone> phones;
    private String[] notes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void addPhone(Phone phone) {
        if (phones == null) {
            phones = new ArrayList<>();
        }
        phones.add(phone);
    }

    public String[] getNotes() {
        return notes;
    }

    public void setNotes(String[] notes) {
        this.notes = notes;
    }

    public Phone getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(Phone mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Phone getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(Phone homePhone) {
        this.homePhone = homePhone;
    }

    @Override
    public String toString() {
        return "Person{" +
            "id=" + id +
            ", fio='" + fio + '\'' +
            ", age=" + age +
            ", mobilePhone=" + mobilePhone +
            ", homePhone=" + homePhone +
            ", phones=" + phones +
            ", notes=" + Arrays.toString(notes) +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id &&
            Objects.equals(fio, person.fio) &&
            Objects.equals(age, person.age) &&
            Objects.equals(mobilePhone, person.mobilePhone) &&
            Objects.equals(homePhone, person.homePhone) &&
            Objects.equals(phones, person.phones) &&
            Arrays.equals(notes, person.notes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, fio, age, mobilePhone, homePhone, phones);
        result = 31 * result + Arrays.hashCode(notes);
        return result;
    }
}
