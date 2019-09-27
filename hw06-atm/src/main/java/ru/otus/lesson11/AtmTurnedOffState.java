package ru.otus.lesson11;

public class AtmTurnedOffState extends AtmState {

    public AtmTurnedOffState(CustomAtm atm) {
        super(atm);
    }

    @Override
    public AtmStateEnum getState() {
        return AtmStateEnum.TURNED_OFF;
    }

    @Override
    public void powerOn() {
        atm.changeState(new AtmBlockedState(atm));
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
        // do nothing
    }

    @Override
    public void update() {
        // do nothing
    }
}
