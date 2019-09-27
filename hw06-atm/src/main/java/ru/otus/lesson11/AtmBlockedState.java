package ru.otus.lesson11;

public class AtmBlockedState extends AtmState {

    public AtmBlockedState(CustomAtm atm) {
        super(atm);
    }

    @Override
    public AtmStateEnum getState() {
        return AtmStateEnum.BLOCKED;
    }

    @Override
    public void powerOn() {
        // do nothing
    }

    @Override
    public void powerOff() {
        atm.changeState(new AtmTurnedOffState(atm));
    }

    @Override
    public void startWork() {
        atm.changeState(new AtmWorkingState(atm));
    }

    @Override
    public void block() {
        // do nothing
    }

    @Override
    public void update() {
        atm.changeState(new AtmUpdatingState(atm));
    }
}
