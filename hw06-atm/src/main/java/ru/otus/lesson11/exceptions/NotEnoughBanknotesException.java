package ru.otus.lesson11.exceptions;

public class NotEnoughBanknotesException extends Exception {

    public NotEnoughBanknotesException() {
        super("Недостаточно купюр в ячейке для выдачи заданного количества!");
    }

    public NotEnoughBanknotesException(String message) {
        super(message);
    }
}
