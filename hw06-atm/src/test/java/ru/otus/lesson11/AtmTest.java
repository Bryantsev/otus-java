package ru.otus.lesson11;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;
import ru.otus.lesson11.exceptions.NotEnoughSumException;
import ru.otus.lesson11.money_cells.AbstractMoneyCell;
import ru.otus.lesson11.money_cells.CustomMoneyCell;
import ru.otus.lesson11.money_cells.MoneyCellC500N200;
import ru.otus.lesson11.money_cells.MoneyCellC500N500;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AtmTest {

    private Atm<AbstractMoneyCell> initCustomMoneyCellAtm(int capacity, int loadedBanknotes) {
        List<AbstractMoneyCell> model500MoneyCellList = new ArrayList<>();
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 50));
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 100));
        // Часть ячеек работают с заданным номиналом купюр и могут хранить заданное их количество
        model500MoneyCellList.add(new MoneyCellC500N200());
        model500MoneyCellList.add(new MoneyCellC500N500());
        // model500MoneyCellList.add(new CustomMoneyCell(capacity, 200));
        // model500MoneyCellList.add(new CustomMoneyCell(capacity, 500));
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 1000));
        model500MoneyCellList.add(new CustomMoneyCell(capacity, 5000));

        Atm<AbstractMoneyCell> atm = new Atm<>(1, "Atm 1", model500MoneyCellList);

        // Загружаем в банкомат купюры разных номиналов, оставляя место для внесения купюр клиентами
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 50));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 100));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 200));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 500));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 1000));
        assertDoesNotThrow(() -> atm.addBanknotes(loadedBanknotes, 5000));
        return atm;
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Внесение 1 купюры не поддерживаемого номинала 2000  -> исключение
     */
    @Test
    void testUnexpectedNominal() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 400);

        assertThrows(IllegalArgumentException.class, () -> atm.addBanknotes(1, 2000));
    }

    /**
     * Внесение 101 купюры номиналом 1000 (емкости недостаточно) -> исключение -> количество купюр не изменилось
     */
    @Test
    void testExceedNominalCapacity() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 400);

        final int nominal = 1000;
        assertThrows(NotEnoughCellCapacityException.class, () -> atm.addBanknotes(101, nominal));
        assertEquals(400, atm.getBanknotesRemained(nominal));
    }

    /**
     * Внесение 100 купюр номиналом 1000 (емкости достаточно) -> исключения нет -> количество купюр 500 -> емкость 0
     */
    @Test
    void testAddBanknotesSuccessful() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 400);

        final int nominal = 1000;
        assertDoesNotThrow(() -> atm.addBanknotes(100, nominal));
        assertEquals(500, atm.getBanknotesRemained(nominal));
        assertEquals(0, atm.getBanknotesCapacityRemained(nominal));
    }

    /**
     * Попытка внести нулевое количество купюр (системная ошибка, клиент такого сделать не может) -> исключение
     */
    @Test
    void testSystemErrorAttemptToAddZeroBanknotes() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 400);

        final int nominal = 1000;
        assertThrows(IllegalArgumentException.class, () -> atm.addBanknotes(0, nominal));
        assertEquals(400, new AtomicInteger(atm.getBanknotesRemained(nominal)).get());
    }

    /**
     * Внесение купюр разного номинала сгруппированными
     */
    @Test
    void testAddBanknotesAsArrayGroupSuccessful() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 400);

        assertDoesNotThrow(() -> atm.addBanknotes(new int[] {100, 500, 100, 1000}));
        assertEquals(500, atm.getBanknotesRemained(500));
        assertEquals(500, atm.getBanknotesRemained(1000));
        assertEquals(0, atm.getBanknotesCapacityRemained(500));
        assertEquals(0, atm.getBanknotesCapacityRemained(1000));
    }

    /**
     * Внесение купюр разного номинала вперемешку
     */
    @Test
    void testAddBanknotesAsArrayChaosSuccessful() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 400);

        assertDoesNotThrow(() -> atm.addBanknotes(new int[] {49, 500, 100, 1000, 51, 500}));
        assertEquals(500, atm.getBanknotesRemained(500));
        assertEquals(500, atm.getBanknotesRemained(1000));
        assertEquals(0, atm.getBanknotesCapacityRemained(500));
        assertEquals(0, atm.getBanknotesCapacityRemained(1000));
    }

    /**
     * Внесение купюр разного номинала с превышением емкости -> исключение -> остатки купюр в банкомате не изменились
     */
    @Test
    void testAddBanknotesAsArrayFail() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 400);

        // Клиент убрал лишнюю купюру и снова пытается внести -> ошибок нет -> количество купюр стало 500 -> оставшаяся емкость банкомата для купюр данного номинала 0
        assertThrows(NotEnoughCellCapacityException.class, () -> atm.addBanknotes(new int[] {100, 500, 101, 1000}));
        assertEquals(400, atm.getBanknotesRemained(500));
        assertEquals(400, atm.getBanknotesRemained(1000));
        assertEquals(100, atm.getBanknotesCapacityRemained(500));
        assertEquals(100, atm.getBanknotesCapacityRemained(1000));
    }

    /**
     * Снятие суммы, которая может быть выдана банкоматом
     */
    @Test
    void testWithdrawSuccessful() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 400);

        var ref = new Object() {
            int[] banknotes;
        };
        assertDoesNotThrow(() -> ref.banknotes = atm.withdraw(4700));
        // Проверяем количество и номинал выданных купюр
        assertEquals(6, ref.banknotes.length, "Выдано меньше номиналов купюр, чем ожидалось!");
        assertArrayEquals(new int[] {4, 1000, 1, 500, 1, 200}, ref.banknotes);
    }

    /**
     * Снятие суммы больше, чем есть в банкомате -> исключение
     */
    @Test
    void testWithdrawFail() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 1);

        var ref = new Object() {
            int[] banknotes;
        };
        assertThrows(NotEnoughSumException.class, () -> ref.banknotes = atm.withdraw(10000));
        // Банкомат не должен выдать ни одной купюры и в нем количество купюр должно остаться без изменений
        assertTrue(ref.banknotes == null || ref.banknotes.length == 0, "Выданы купюры, хотя в банкомате недостаточно денег!");
        assertEquals(1, atm.getBanknotesRemained(50));
        assertEquals(1, atm.getBanknotesRemained(100));
        assertEquals(1, atm.getBanknotesRemained(200));
        assertEquals(1, atm.getBanknotesRemained(500));
        assertEquals(1, atm.getBanknotesRemained(1000));
        assertEquals(1, atm.getBanknotesRemained(5000));
    }

    /**
     * Снятие суммы, которую нельзя выдать купюрами, находящимися в банкомате, хотя общая сумма купюр в банкомате достаточна -> исключение
     */
    @Test
    void testWithdrawFail2() {
        Atm<AbstractMoneyCell> atm = initCustomMoneyCellAtm(500, 1);

        var ref = new Object() {
            int[] banknotes;
        };
        assertThrows(NotEnoughSumException.class, () -> ref.banknotes = atm.withdraw(1900));
        // Банкомат не должен выдать ни одной купюры и в нем количество купюр должно остаться без изменений
        assertTrue(ref.banknotes == null || ref.banknotes.length == 0, "Выданы купюры, хотя в банкомате нет подходящих купюр!");
        assertEquals(1, atm.getBanknotesRemained(50));
        assertEquals(1, atm.getBanknotesRemained(100));
        assertEquals(1, atm.getBanknotesRemained(200));
        assertEquals(1, atm.getBanknotesRemained(500));
        assertEquals(1, atm.getBanknotesRemained(1000));
        assertEquals(1, atm.getBanknotesRemained(5000));
    }

}
