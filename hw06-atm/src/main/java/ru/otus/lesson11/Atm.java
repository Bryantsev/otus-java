package ru.otus.lesson11;

import ru.otus.lesson11.exceptions.NotEnoughBanknotesException;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;
import ru.otus.lesson11.exceptions.NotEnoughSumException;

public interface Atm {

    /**
     * Емкость банкомата в купюрах заданного номинала
     *
     * @param nominal Номинал купюры
     * @return Максимальное количество купюр заданного номинала, которое вмещает банкомат
     */
    public int getBanknotesCapacity(int nominal);

    /**
     * Количество купюр заданного номинала, которое может принять банкомат
     *
     * @param nominal Номинал купюр
     * @return Количество купюр
     */
    public int getBanknotesCapacityRemained(int nominal);

    /**
     * Количество купюр заданного номинала, которое может выдать банкомат
     *
     * @param nominal Номинал купюр
     * @return Количество купюр
     */
    public int getBanknotesRemained(int nominal);

    /**
     * Внести купюры заданного номинала
     *
     * @param count   Количество купюр
     * @param nominal Номинал купюр
     * @return Сумма внесенных купюр
     */
    public int addBanknotes(int count, int nominal) throws NotEnoughCellCapacityException;

    /**
     * Внести купюры разных номиналов
     *
     * @param countAndNominal Массив с четным количеством элементов, где четный элемент - это количество купюр, а нечетный - номинал купюры
     * @return Сумма внесенных купюр
     */
    public int addBanknotes(int[] countAndNominal) throws NotEnoughCellCapacityException;

    /**
     * Получить заданную сумму любыми купюрами
     *
     * @param total Сумма для снятия
     * @return Выданные купюры. Массив с четным количеством элементов: четные элементы - количество купюр, нечетные - номинал купюр
     */
    public int[] withdraw(int total) throws NotEnoughSumException, NotEnoughBanknotesException;

}
