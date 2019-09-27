package ru.otus.lesson11;

import ru.otus.lesson11.exceptions.NotEnoughBanknotesException;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;
import ru.otus.lesson11.exceptions.NotEnoughSumException;
import ru.otus.lesson11.money_cells.MoneyCell;

import java.util.*;

public class CustomAtm implements Atm {

    /**
     * Ид-р банкомата
     */
    private long id;
    /**
     * Имя банкомата
     */
    private String name;
    /**
     * Ячейки с купюрами
     */
    private List<MoneyCell> moneyCells;
    /**
     * Текущая версия ПО банкомата
     */
    private String softwareVersion;
    /**
     * Новая версия ПО банкомата для обновления
     */
    private String newSoftwareVersionForUpdate;


    private AtmState state;

    public CustomAtm(long id, String name, List<MoneyCell> moneyCells, String softwareVersion) {
        this.id = id;
        this.name = name;
        this.moneyCells = moneyCells == null ? new ArrayList<>() : moneyCells;
        this.softwareVersion = softwareVersion;
        this.state = new AtmTurnedOffState(this);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    public String getName() {
        return name;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public String getNewSoftwareVersionForUpdate() {
        return newSoftwareVersionForUpdate;
    }

    /**
     * Изменить статус банкомата. Синхронизируем
     *
     * @param state Новый статус банкомата
     */
    public synchronized void changeState(AtmState state) {
        this.state = state;
    }

    /**
     * Получить тип статуса банкомата. Синхронизируем
     *
     * @return
     */
    public synchronized AtmStateEnum getState() {
        return state.getState();
    }

    @Override
    public void powerOn() {
        state.powerOn();
    }

    @Override
    public void powerOff() {
        state.powerOff();
    }

    @Override
    public void startWork() {
        state.startWork();
    }

    @Override
    public void block() {
        state.block();
    }

    @Override
    public void update(String newVersion) {
        this.newSoftwareVersionForUpdate = newVersion;
        state.update();
    }

    /**
     * Изменение состояния банкомата в случае успешного обновления. Операция срабатывает только, если банкомат в статусе Обновляется
     */
    public void updateSuccessful() {
        if (AtmStateEnum.UPDATING.equals(state.getState())) {
            softwareVersion = newSoftwareVersionForUpdate;
        }
    }

    /**
     * Сохранить текущую версию ПО банкомата
     *
     * @return Текущая версия ПО банкомата
     */
    public SoftwareVersion saveSoftwareVersion() {
        return new SoftwareVersion(softwareVersion);
    }

    /**
     * Восстановить заданную версию ПО банкомата
     *
     * @param softwareVersion Версия ПО банкомата
     * @return Успешность восстановления
     */
    public boolean restoreSoftwareVersion(SoftwareVersion softwareVersion) {
        this.softwareVersion = softwareVersion.getVersion();
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

    /**
     * Емкость банкомата в купюрах заданного номинала
     *
     * @param nominal Номинал купюры
     * @return Максимальное количество купюр заданного номинала, которое вмещает банкомат
     */
    @Override
    public int getBanknotesCapacity(int nominal) {
        // Проходим по всем ячейкам и суммируем емкость ячеек заданного номинала
        int capacity = 0;
        for (MoneyCell cell : moneyCells) {
            if (cell.getNominal() == nominal) {
                capacity += cell.getCapacity();
            }
        }

        return capacity;
    }

    /**
     * Количество купюр заданного номинала, которое может принять банкомат
     *
     * @param nominal Номинал купюр
     * @return Количество купюр
     */
    @Override
    public int getBanknotesCapacityRemained(int nominal) {
        // Проходим по всем ячейкам и суммируем емкость ячеек заданного номинала
        int capacityRemained = 0;
        for (MoneyCell cell : moneyCells) {
            if (cell.getNominal() == nominal) {
                capacityRemained += cell.getCapacityRemained();
            }
        }

        return capacityRemained;
    }

    /**
     * Количество купюр заданного номинала, которое может выдать банкомат
     *
     * @param nominal Номинал купюр
     * @return Количество купюр
     */
    @Override
    public int getBanknotesRemained(int nominal) {
        // Проходим по всем ячейкам и суммируем количество купюр в ячейках заданного номинала
        int remained = 0;
        for (MoneyCell cell : moneyCells) {
            if (cell.getNominal() == nominal) {
                remained += cell.getRemained();
            }
        }

        return remained;
    }

    /**
     * Мапа количества купюр по номиналам
     */
    private Map<Integer, Integer> getBanknotesByNominal() {
        Map<Integer, Integer> banknotes = new HashMap<>();

        for (MoneyCell cell : moneyCells) {
            final int nominal = cell.getNominal();
            if (banknotes.containsKey(nominal)) {
                banknotes.put(nominal, banknotes.get(nominal) + cell.getRemained());
            } else {
                banknotes.put(nominal, cell.getRemained());
            }
        }

        return banknotes;
    }

    @Override
    public long getTotal() {
        long total = 0;
        Map<Integer, Integer> banknotes = getBanknotesByNominal();
        for (Map.Entry<Integer, Integer> cell : banknotes.entrySet()) {
            total += cell.getKey() * cell.getValue();
        }
        return total;
    }

    /**
     * Внести купюры заданного номинала
     *
     * @param count   Количество купюр
     * @param nominal Номинал купюр
     * @return Сумма внесенных купюр
     */
    @Override
    public int addBanknotes(int count, int nominal) throws NotEnoughCellCapacityException {
        if (count < 1) {
            throw new IllegalArgumentException("Количество купюр д.б. больше 0!");
        }

        // Проверим, что номинал поддерживается банкоматом
        if (getBanknotesCapacity(nominal) == 0) {
            throw new IllegalArgumentException("Номинал купюр " + nominal + " не поддерживается!");
        }

        // Проверим достаточна ли емкость ячеек заданного номинала
        int capacityRemained = getBanknotesCapacityRemained(nominal);
        if (capacityRemained < count) {
            throw new NotEnoughCellCapacityException();
        }

        int remainedToAdd = count;
        // Проходим по ячейкам с заданным номиналом (их м.б. несколько) и добавляем в них купюры
        for (MoneyCell cell : moneyCells) {
            if (cell.getNominal() == nominal) {
                int countToAdd = Math.min(remainedToAdd, cell.getCapacityRemained());
                cell.addBanknotes(countToAdd);
                remainedToAdd -= countToAdd;
                if (remainedToAdd == 0) {
                    break;
                }
            }
        }

        return count * nominal;
    }

    /**
     * Внести купюры разных номиналов
     *
     * @param countAndNominal Массив с четным количеством элементов, где четный элемент - это количество купюр, а нечетный - номинал купюры
     * @return Сумма внесенных купюр
     */
    @Override
    public int addBanknotes(int[] countAndNominal) throws NotEnoughCellCapacityException {

        if (countAndNominal.length % 2 != 0) {
            throw new IllegalArgumentException("Массив должен содержать четное количество элементов со значениями больше нуля: <количество купюр>, <номинал купюры>, ...!");
        }

        Map<Integer, Integer> banknotes = new HashMap<>();
        // Перенесем данные в мапу по номиналу (купюры могли быть перемешаны по номиналу)
        for (int i = 0; i < countAndNominal.length; i += 2) {
            final int nominal = countAndNominal[i + 1];
            if (nominal < 1 || countAndNominal[i] < 1) {
                throw new IllegalArgumentException("Количество и номинал купюр должны быть больше нуля!");
            }
            if (banknotes.containsKey(nominal)) {
                banknotes.put(nominal, banknotes.get(nominal) + countAndNominal[i]);
            } else {
                // Проверим, что номинал поддерживается банкоматом
                if (getBanknotesCapacity(nominal) == 0) {
                    throw new IllegalArgumentException("Номинал купюр " + nominal + " не поддерживается!");
                }
                banknotes.put(nominal, countAndNominal[i]);
            }
        }

        // Проверим, что емкости ячеек достаточно для купюр всех номиналов
        for (Map.Entry<Integer, Integer> entry : banknotes.entrySet()) {
            if (getBanknotesCapacityRemained(entry.getKey()) < entry.getValue()) {
                throw new NotEnoughCellCapacityException(entry.getKey());
            }
        }

        // Вносим купюры по номиналу
        int total = 0;
        for (Map.Entry<Integer, Integer> entry : banknotes.entrySet()) {
            addBanknotes(entry.getValue(), entry.getKey());
            total += entry.getKey() * entry.getValue();
        }


        return total;
    }

    /**
     * Получить заданную сумму любыми купюрами
     *
     * @param total Сумма для снятия
     * @return Выданные купюры. Массив с четным количеством элементов: четные элементы - количество купюр, нечетные - номинал купюр
     */
    @Override
    public int[] withdraw(int total) throws NotEnoughSumException, NotEnoughBanknotesException {

        int totalRemained = total;

        // Берем мапу количества купюр по номиналам в банкомате
        Map<Integer, Integer> banknotes = getBanknotesByNominal();
        // В отдельном списке сортируем номиналы по убыванию
        List<Integer> sortedDescNominalList = new ArrayList<>(banknotes.keySet());
        sortedDescNominalList.sort(Comparator.reverseOrder());

        // Собираем мапу количества купюр по номиналам для выдачи, если не получается -> исключение, иначе применяем полученную мапу
        Map<Integer, Integer> banknotesToWithdrawMap = new LinkedHashMap<>();
        for (Integer nominal : sortedDescNominalList) {
            if (totalRemained >= nominal) {
                final int count = Math.min(banknotes.get(nominal), totalRemained / nominal);
                banknotesToWithdrawMap.put(nominal, count);
                totalRemained -= nominal * count; // Уменьшаем сумму, которую необходимо выдать
            }
        }

        // Если не удалось подобрать сумму, то исключение
        if (totalRemained > 0) {
            throw new NotEnoughSumException();
        }

        // Выдаем подобранные купюры
        int[] banknotesToWithdraw = new int[banknotesToWithdrawMap.values().size() * 2];
        int idx = -1;
        for (Map.Entry<Integer, Integer> nominalEntry : banknotesToWithdrawMap.entrySet()) {
            // Проходим по ячейкам с заданным номиналом (их м.б. несколько) и берем купюры
            int remainedToWithdraw = nominalEntry.getValue();
            banknotesToWithdraw[++idx] = remainedToWithdraw;
            banknotesToWithdraw[++idx] = nominalEntry.getKey();
            for (MoneyCell cell : moneyCells) {
                if (cell.getNominal() == nominalEntry.getKey()) {
                    int countToWithdraw = Math.min(remainedToWithdraw, cell.getRemained());
                    cell.withdrawBanknotes(countToWithdraw);
                    remainedToWithdraw -= countToWithdraw;
                    if (remainedToWithdraw == 0) {
                        break;
                    }
                }
            }

        }

        return banknotesToWithdraw;
    }

}
