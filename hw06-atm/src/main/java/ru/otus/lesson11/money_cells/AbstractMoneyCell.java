package ru.otus.lesson11.money_cells;

import ru.otus.lesson11.exceptions.NotEnoughBanknotesException;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;

public abstract class AbstractMoneyCell implements MoneyCell {

    private int capacity;
    private int remained;
    private int nominal;

    protected AbstractMoneyCell() {
    }

    protected AbstractMoneyCell(int capacity, int nominal) {
        this.capacity = capacity;
        this.nominal = nominal;
    }

    protected void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int getRemained() {
        return remained;
    }

    @Override
    public int getCapacityRemained() {
        return getCapacity() - getRemained();
    }

    protected void setNominal(int nominal) {
        this.nominal = nominal;
    }

    @Override
    public int getNominal() {
        return nominal;
    }

    @Override
    public int getTotalRemained() {
        return getNominal() * getRemained();
    }

    @Override
    public int withdrawBanknotes(int count) throws NotEnoughBanknotesException {
        if (remained < count) {
            throw new NotEnoughBanknotesException();
        }

        remained -= count;

        return remained;
    }

    @Override
    public int addBanknotes(int count) throws NotEnoughCellCapacityException {
        if (getCapacityRemained() < count) {
            throw new NotEnoughCellCapacityException();
        }

        remained += count;

        return remained;
    }
}
