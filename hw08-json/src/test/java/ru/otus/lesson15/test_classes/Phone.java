package ru.otus.lesson15.test_classes;

import java.util.Objects;

public class Phone {

    private PhoneType phoneType;
    private String number;

    public Phone() {
    }

    public Phone(PhoneType phoneType, String number) {
        this.phoneType = phoneType;
        this.number = number;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return phoneType == phone.phoneType &&
            number.equals(phone.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneType, number);
    }
}
