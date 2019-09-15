package ru.otus.lesson11;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;

import static org.junit.jupiter.api.Assertions.*;

class AtmDepartmentTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Создаем 2 подразделения плюс суперподразделение, в которые входят 2 предыдущих. В 2 обычных подразделения добавляем банкоматы.
     * Получаем сумму, оставшуюся во всех банкоматах суперподразделения, обращаясь только к нему
     * Шаблон Компоновщик
     */
    @Test
    void testGetTotal() {
        AtmDepartment dep1 = new AtmDepartment(1, "dep 1");
        dep1.addAtm(Utils.getCustomAtm(1, "atm1", 50, 40));
        dep1.addAtm(Utils.getCustomAtm(2, "atm2", 50, 30));

        AtmDepartment dep2 = new AtmDepartment(2, "dep 2");
        dep2.addAtm(Utils.getCustomAtm(1, "atm1", 50, 30));
        dep2.addAtm(Utils.getCustomAtm(2, "atm2", 50, 20));
        dep2.addAtm(Utils.getCustomAtm(2, "atm2", 50, 10));

        AtmDepartment superDep = new AtmDepartment(3, "Superdep 3");
        superDep.addAtm(dep1);
        superDep.addAtm(dep2);

        assertEquals(479500, dep1.getTotal());
        assertEquals(411000, dep2.getTotal());
        assertEquals(479500 + 411000, superDep.getTotal());

        // Проверим, что в подразделение нельзя добавить купюру
        assertThrows(NotEnoughCellCapacityException.class, () -> dep1.addBanknotes(1, 500));

    }

}
