package ru.otus.lesson11;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.lesson11.exceptions.NotEnoughBanknotesException;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;
import ru.otus.lesson11.money_cells.CustomMoneyCell;
import ru.otus.lesson11.money_cells.MoneyCell;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CustomMoneyCellTest {

    private MoneyCell moneyCell;
    private AtomicInteger remained;

    /**
     * Создаем ячейку на 500 купюр номиналом 100 и загружаем в нее 100 купюр
     */
    @BeforeEach
    void setUp() {
        moneyCell = new CustomMoneyCell(500, 100);
        assertDoesNotThrow(() -> moneyCell.addBanknotes(100));
        remained = new AtomicInteger(moneyCell.getRemained());
    }

    @AfterEach
    void tearDown() {
        moneyCell = null;
    }

    /**
     * Проверяем:
     * - отсутствие исключений при успешном снятии купюр из ячейки (в ячейке достаточно купюр для снятия);
     * - верное количество купюр в ячейке после успешного снятия.
     */
    @Test
    void withdrawBanknotesSuccessful() {
        assertDoesNotThrow(() -> remained.set(moneyCell.withdrawBanknotes(49)));
        assertEquals(51, remained.get());
    }

    /**
     * Проверяем:
     * - выброс исключения, при попытке снять больше купюр, чем есть в ячейке;
     * - неизменность исходного количества купюр в ячейке после неудачной попытки снятия
     */
    @Test
    void withdrawBanknotesFail() {
        assertThrows(NotEnoughBanknotesException.class, () -> remained.set(moneyCell.withdrawBanknotes(101)));
        assertEquals(100, remained.get());
    }

    /**
     * Проверяем:
     * - отсутствие исключений при успешном добавлении купюр в ячейку (в ячейке достаточно емкости для добавления);
     * - верное количество купюр в ячейке после успешного добавления.
     */
    @Test
    void addBanknotesSuccessful() {
        assertDoesNotThrow(() -> remained.set(moneyCell.addBanknotes(100)));
        assertEquals(200, remained.get());
    }

    /**
     * Проверяем:
     * - бросок исключения, при попытке добавить больше купюр, чем позволяет емкость ячейки;
     * - неизменность исходного количества купюр в ячейке после неудачной попытки добавления
     */
    @Test
    void addBanknotesFail() {
        assertThrows(NotEnoughCellCapacityException.class, () -> remained.set(moneyCell.addBanknotes(401)));
        assertEquals(100, remained.get());
    }

}
