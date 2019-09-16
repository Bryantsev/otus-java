package ru.otus.lesson11;

public abstract class AtmState {

    protected CustomAtm atm;

    public AtmState(CustomAtm atm) {
        this.atm = atm;
    }

    public abstract AtmStateEnum getState();

    public abstract void powerOn();
    public abstract void powerOff();
    public abstract void startWork();
    public abstract void block();
    public abstract void update();

}
