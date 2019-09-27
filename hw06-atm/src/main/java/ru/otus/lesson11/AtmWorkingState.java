package ru.otus.lesson11;

public class AtmWorkingState extends AtmState {

    public AtmWorkingState(CustomAtm atm) {
        super(atm);
    }

    @Override
    public AtmStateEnum getState() {
        return AtmStateEnum.WORKING;
    }

    @Override
    public void powerOn() {
        // do nothing
    }

    @Override
    public void powerOff() {
        // do nothing
    }

    @Override
    public void startWork() {
        // do nothing
    }

    @Override
    public void block() {
        // Завершаем текущие операции и переводим банкомат в статус Заблокирован
        atm.changeState(new AtmBlockedState(atm));
    }

    @Override
    public void update() {
        // do nothing
    }
}
