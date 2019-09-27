package ru.otus.lesson11;

import ru.otus.lesson11.exceptions.NotEnoughBanknotesException;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;
import ru.otus.lesson11.exceptions.NotEnoughSumException;

public interface Atm {

    /**
     * Вернуть ид-р банкомата
     */
    public long getId();

    /**
     * Признак группы банкоматов
     */
    public boolean isGroup();

    /**
     * Включить банкомат
     */
    public void powerOn();

    /**
     * Выключить банкомат
     */
    public void powerOff();

    /**
     * Запустить банкомат в работу: прием и выдачу купюр
     */
    public void startWork();

    /**
     * Заблокировать банкомат для служебных операций
     */
    public void block();

    /**
     * Обновить версию ПО банкомата
     *
     * @param newVersion Новая версия ПО
     */
    public void update(String newVersion);

    /**
     * Сохранить текущую версию ПО банкомата
     *
     * @return Текущая версия ПО банкомата
     */
    public SoftwareVersion saveSoftwareVersion() throws Exception;

    /**
     * Восстановить заданную версию ПО банкомата
     *
     * @param softwareVersion Версия ПО банкомата
     * @return Успешность восстановления
     */
    public boolean restoreSoftwareVersion(SoftwareVersion softwareVersion) throws Exception;

    /**
     * Емкость банкомата в купюрах заданного номинала
     *
     * @param nominal Номинал купюры
     * @return Максимальное количество купюр заданного номинала, которое вмещает банкомат
     */
    public int getBanknotesCapacity(int nominal);

    /**
     * Количество купюр заданного номинала, которое может принять банкомат
     *
     * @param nominal Номинал купюр
     * @return Количество купюр
     */
    public int getBanknotesCapacityRemained(int nominal);

    /**
     * Количество купюр заданного номинала, которое может выдать банкомат
     *
     * @param nominal Номинал купюр
     * @return Количество купюр
     */
    public int getBanknotesRemained(int nominal);

    /**
     * Сумма, которую может выдать банкомат
     */
    public long getTotal();

    /**
     * Внести купюры заданного номинала
     *
     * @param count   Количество купюр
     * @param nominal Номинал купюр
     * @return Сумма внесенных купюр
     */
    public int addBanknotes(int count, int nominal) throws NotEnoughCellCapacityException;

    /**
     * Внести купюры разных номиналов
     *
     * @param countAndNominal Массив с четным количеством элементов, где четный элемент - это количество купюр, а нечетный - номинал купюры
     * @return Сумма внесенных купюр
     */
    public int addBanknotes(int[] countAndNominal) throws NotEnoughCellCapacityException;

    /**
     * Получить заданную сумму любыми купюрами
     *
     * @param total Сумма для снятия
     * @return Выданные купюры. Массив с четным количеством элементов: четные элементы - количество купюр, нечетные - номинал купюр
     */
    public int[] withdraw(int total) throws NotEnoughSumException, NotEnoughBanknotesException;


    /**
     * Сохраненная версия ПО банкомата
     */
    public class SoftwareVersion {

        private String version;

        protected SoftwareVersion(String version) {
            this.version = version;
        }

        protected String getVersion() {
            return version;
        }
    }

}
