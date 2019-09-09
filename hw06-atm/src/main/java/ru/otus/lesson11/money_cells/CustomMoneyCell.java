package ru.otus.lesson11.money_cells;

public class CustomMoneyCell extends AbstractMoneyCell {

    public CustomMoneyCell() {
    }

    public CustomMoneyCell(int capacity, int nominal) {
        super(capacity, nominal);
    }

    @Override
    public void setCapacity(int capacity) {
        super.setCapacity(capacity);
    }

    @Override
    public void setNominal(int nominal) {
        super.setNominal(nominal);
    }
}
