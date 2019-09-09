package ru.otus.lesson11.exceptions;

public class NotEnoughSumException extends Exception {

    public NotEnoughSumException() {
        super("Банкомат не имеет достаточной суммы или купюр подходящего номинала для выдачи!");
    }
}
