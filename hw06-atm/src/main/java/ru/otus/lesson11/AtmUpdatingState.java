package ru.otus.lesson11;

/**
 * В данном статусе банкомат ни на что не реагирует, кроме выключения - по сути сброс процесса обновления.
 * При нормально протекающем обновлении банкомат сам перейдет в статус Заблокирован после завершения всех процедур
 */
public class AtmUpdatingState extends AtmState {

    Thread updatingProcess;

    public AtmUpdatingState(CustomAtm atm) {
        super(atm);

        // Запускаем обновление в отдельном потоке
        updatingProcess = new Thread(() -> {
            // Сохраняем текущую версию ПО банкомата для восстановления в случае ошибок в процессе обновления
            // Данные сохраняются в т.ч. на диск, чтобы в случае аварийного выключения питания восстановиться при следующем включении!
            CustomAtm.SoftwareVersion currentSoftwareVersion = atm.saveSoftwareVersion();
            try {
                // Берем данные по новой версии для обновления, засыпаем на полсекунды, имитируя процесс обновление, и затем меняем статус на Заблокирован
                atm.getNewSoftwareVersionForUpdate();
                Thread.sleep(500);
                atm.updateSuccessful(); // Сигнализируем банкомату об успешном обновлении для замены версии

                atm.changeState(new AtmBlockedState(atm));

                // Если процесс обновления прерван, то банкомат логирует данный факт, восстанавливает предыдущую версию и переходит в статус Выключен
            } catch (Exception e) {
                atm.restoreSoftwareVersion(currentSoftwareVersion);
                atm.changeState(new AtmTurnedOffState(atm));
            }
        });

        updatingProcess.start();
    }

    @Override
    public AtmStateEnum getState() {
        return AtmStateEnum.UPDATING;
    }

    @Override
    public void powerOn() {
        // do nothing
    }

    @Override
    public void powerOff() {
        // Прерываем выполнение потока обновления
        if (updatingProcess.isAlive()) {
            updatingProcess.interrupt();
        }
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
