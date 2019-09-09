package ru.otus.lesson11.money_cells;

import ru.otus.lesson11.exceptions.NotEnoughBanknotesException;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;

public interface MoneyCell {

    /**
     * Вернуть емкость ячейки
     *
     * @return Максимальное количество купюр, которое м.б. размещено в ячейке
     */
    public int getCapacity();

    /**
     * Вернуть остаток емкости ячейки
     *
     * @return Количество купюр, которые можно добавить в ячейку до полного заполнения
     */
    public default int getCapacityRemained() {
        return getCapacity() - getRemained();
    }

    /**
     * Вернуть остаток купюр в ячейке
     *
     * @return Количество купюр, оставшихся в ячейке
     */
    public int getRemained();

    /**
     * Вернуть номинал купюр в ячейке
     *
     * @return Номинал купюры
     */
    public int getNominal();

    /**
     * Вернуть сумму всех купюр в ячейке
     *
     * @return Сумма всех купюр в ячейке
     */
    public default int getTotalRemained() {
        return getNominal() * getRemained();
    }

    /**
     * Выдать заданое количество купюр из ячейки
     *
     * @param count Количество купюр для выдачи
     * @return Количество оставшихся купюр в ячейке после выдачи
     * @throws NotEnoughBanknotesException Исключение в случае отсутствия достаточно количества купюр в ячейке для выдачи
     */
    public int withdrawBanknotes(int count) throws NotEnoughBanknotesException;

    /**
     * Добавить купюры в ячейку
     *
     * @param count Количество купюр для добавления в ячейку
     * @return Количество оставшихся купюр в ячейке после добавления
     * @throws NotEnoughCellCapacityException Исключение в случае недостаточности свободной емкости ячейки для приема заданного количества купюр
     */
    public int addBanknotes(int count) throws NotEnoughCellCapacityException;

}
