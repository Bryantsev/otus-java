package ru.otus.lesson11.exceptions;

public class NotEnoughCellCapacityException extends Exception {

    public NotEnoughCellCapacityException() {
        super("Недостаточная емкость ячейки для приема заданного количества купюр!");
    }

    public NotEnoughCellCapacityException(int nominal) {
        super("Недостаточная емкость для приема заданного количества купюр с номиналом " + nominal + "!");
    }

}
