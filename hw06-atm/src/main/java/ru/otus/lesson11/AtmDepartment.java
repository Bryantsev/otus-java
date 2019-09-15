package ru.otus.lesson11;

import ru.otus.lesson11.exceptions.NotEnoughBanknotesException;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;
import ru.otus.lesson11.exceptions.NotEnoughSumException;

import java.util.ArrayList;
import java.util.List;

public class AtmDepartment implements Atm {

    private long id;
    private String name;
    private List<Atm> atms = new ArrayList<>();

    public AtmDepartment(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Atm> getAtms() {
        return atms;
    }

    public void addAtm(Atm atm) {
        atms.add(atm);
    }

    public boolean removeAtm(Atm atm) {
        return atms.remove(atm);
    }

    @Override
    public int getBanknotesCapacity(int nominal) {
        return atms.stream().mapToInt(atm -> atm.getBanknotesCapacity(nominal)).sum();
    }

    @Override
    public int getBanknotesCapacityRemained(int nominal) {
        return atms.stream().mapToInt(atm -> atm.getBanknotesCapacityRemained(nominal)).sum();
    }

    @Override
    public int getBanknotesRemained(int nominal) {
        return atms.stream().mapToInt(atm -> atm.getBanknotesRemained(nominal)).sum();
    }

    @Override
    public long getTotal() {
        return atms.stream().mapToLong(Atm::getTotal).sum();
    }

    @Override
    public int addBanknotes(int count, int nominal) throws NotEnoughCellCapacityException {
        throw new NotEnoughCellCapacityException("Операция не поддерживается для подразделения банкоматов!");
    }

    @Override
    public int addBanknotes(int[] countAndNominal) throws NotEnoughCellCapacityException {
        throw new NotEnoughCellCapacityException("Операция не поддерживается для подразделения банкоматов!");
    }

    @Override
    public int[] withdraw(int total) throws NotEnoughSumException, NotEnoughBanknotesException {
        throw new NotEnoughBanknotesException("Операция не поддерживается для подразделения банкоматов!");
    }
}
