package ru.otus.lesson11;

import ru.otus.lesson11.money_cells.CustomMoneyCell;
import ru.otus.lesson11.money_cells.MoneyCell;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class Utils {

    public static CustomAtm getCustomAtm(long id, String name, int capacity, int loadedBanknotes) {
        List<MoneyCell> model500MoneyCellList = new ArrayList<>();
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 50));
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 100));
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 200));
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 500));
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 1000));
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 5000));

        CustomAtm atm = new CustomAtm(id, name, model500MoneyCellList, "currentVersion");

        // Загружаем в банкомат купюры разных номиналов, оставляя место для внесения купюр клиентами
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 50));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 100));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 200));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 500));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 1000));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 5000));
        return atm;
    }

}
