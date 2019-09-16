package ru.otus.lesson11;

import ru.otus.lesson11.exceptions.NotEnoughBanknotesException;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;
import ru.otus.lesson11.exceptions.NotEnoughSumException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Группа банкоматов. Имеет такой же интерфейс, как и у обычного банкомата, но поддерживает не все операции
 */
public class AtmGroup implements Atm {

    final String notSupportedExceptionMessage = "Данная операция не поддерживается группой банкоматов!";

    private long id;
    private String name;
    private List<Atm> atms = new ArrayList<>();


    public AtmGroup(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    /**
     * Сравниваем банкоматы по ид-рам
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atm otherAtm = (Atm) o;
        return isGroup() == otherAtm.isGroup() && id == otherAtm.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isGroup());
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
    public void powerOn() {
        atms.forEach(Atm::powerOn);
    }

    @Override
    public void powerOff() {
        atms.forEach(Atm::powerOff);
    }

    @Override
    public void startWork() {
        atms.forEach(Atm::startWork);
    }

    @Override
    public void block() {
        atms.forEach(Atm::block);
    }

    @Override
    public void update(String newVersion) {
        atms.forEach(atm -> atm.update(newVersion));
    }

    @Override
    public SoftwareVersion saveSoftwareVersion() throws Exception {
        throw new Exception(notSupportedExceptionMessage);
    }

    @Override
    public boolean restoreSoftwareVersion(SoftwareVersion softwareVersion) throws Exception {
        throw new Exception(notSupportedExceptionMessage);
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
        throw new NotEnoughCellCapacityException(notSupportedExceptionMessage);
    }

    @Override
    public int addBanknotes(int[] countAndNominal) throws NotEnoughCellCapacityException {
        throw new NotEnoughCellCapacityException(notSupportedExceptionMessage);
    }

    @Override
    public int[] withdraw(int total) throws NotEnoughSumException, NotEnoughBanknotesException {
        throw new NotEnoughBanknotesException(notSupportedExceptionMessage);
    }
}
