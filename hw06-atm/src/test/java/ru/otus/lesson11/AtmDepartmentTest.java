package ru.otus.lesson11;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.lesson11.exceptions.NotEnoughCellCapacityException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AtmDepartmentTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Создаем 2 группы плюс супергруппу банкоматов, в которую входят 2 предыдущих. В 2 обычных группы добавляем банкоматы.
     * Получаем сумму, оставшуюся во всех банкоматах супергруппы, обращаясь только к нему
     * Шаблон Компоновщик (Composite)
     */
    @Test
    void testGetTotal() {
        AtmGroup dep1 = new AtmGroup(1, "dep 1");
        dep1.addAtm(Utils.getCustomAtm(1, "atm1", 50, 40));
        dep1.addAtm(Utils.getCustomAtm(2, "atm2", 50, 30));

        AtmGroup dep2 = new AtmGroup(2, "dep 2");
        dep2.addAtm(Utils.getCustomAtm(3, "atm3", 50, 30));
        dep2.addAtm(Utils.getCustomAtm(4, "atm4", 50, 20));
        dep2.addAtm(Utils.getCustomAtm(5, "atm5", 50, 10));

        AtmGroup superDep = new AtmGroup(3, "Superdepartment 3");
        superDep.addAtm(dep1);
        superDep.addAtm(dep2);

        assertEquals(479500, dep1.getTotal());
        assertEquals(411000, dep2.getTotal());
        assertEquals(479500 + 411000, superDep.getTotal());

        // Проверим, что в группу нельзя добавить купюру
        assertThrows(NotEnoughCellCapacityException.class, () -> dep1.addBanknotes(1, 500));
    }

    /**
     * Проверим полный цикл переключения статусов банкомата
     * Шаблоны: Снимок (Memento) и Состояние (State)
     */
    @Test
    void testAtmStates() throws InterruptedException {
        CustomAtm atm = Utils.getCustomAtm(1, "atm1", 50, 40);

        // Начальный статус банкомата Выключен
        assertEquals(AtmStateEnum.TURNED_OFF, atm.getState());

        // Включаем банкомат, при этом он переходит в статус Заблокирован
        atm.powerOn();
        assertEquals(AtmStateEnum.BLOCKED, atm.getState());

        // Запускаем банкомат в работу
        atm.startWork();
        assertEquals(AtmStateEnum.WORKING, atm.getState());

        // Блокируем банкомат
        atm.block();
        assertEquals(AtmStateEnum.BLOCKED, atm.getState());

        // Обновляем банкомат. Сразу после запуска он будет в статусе Обновляется, но после окончания обновления должен автоматически перейти в статус Заблокирован
        assertEquals("currentVersion", atm.getSoftwareVersion()); // Версия ПО банкомата перед обновлением
        atm.update("newVersion");
        assertEquals(AtmStateEnum.UPDATING, atm.getState());
        // Засыпаем на 1 секунду, и проверяем, что у банкомата статус Заблокирован и новая версия ПО
        Thread.sleep(1000);
        assertEquals(AtmStateEnum.BLOCKED, atm.getState());
        assertEquals("newVersion", atm.getSoftwareVersion());

        // Снова обновляем банкомат, но не ждем завершения и вызываем операцию Выключить, таким образом прерывая обновление.
        // Выдерживаем небольшую паузу для отработки операций при прерывании обновления и проверяем,
        // что статус банкомата Выключен и исходная версия ПО newVersion, а не newVersion2
        atm.update("newVersion2");
        Thread.sleep(100);
        atm.powerOff();
        Thread.sleep(200);
        assertEquals(AtmStateEnum.TURNED_OFF, atm.getState());
        assertEquals("newVersion", atm.getSoftwareVersion());
    }


    /**
     * Создаем департамент и группу банкоматов, в которые добавляем банкоматы.
     * Обновляем ПО у банкоматов в группе на разные версии, затем еще раз, после чего восстанавливаем предыдущую версию у всех банкоматов
     * Шаблоны: Снимок (Memento) и Состояние (State)
     */
    @Test
    void testRestoreSoftwareVersion() throws Exception {
        AtmDepartment dep = new AtmDepartment(); // Департамент выступает диспетчером банкоматов или Опекуном в шаблоне Снимок
        AtmGroup group = new AtmGroup(1, "Group 1"); // Группа банкоматов

        final CustomAtm atm1 = Utils.getCustomAtm(1, "atm1", 50, 40);
        dep.addAtm(atm1);
        group.addAtm(atm1);

        final CustomAtm atm2 = Utils.getCustomAtm(2, "atm2", 50, 30);
        dep.addAtm(atm2);
        group.addAtm(atm2);

        // Включим все банкоматы и обновим их разными версиями
        group.powerOn(); // После включения банкоматы в статусе Заблокирован и их можно обновить
        dep.updateAtm(atm1, "version1");
        dep.updateAtm(atm2, "version2");
        Thread.sleep(1000); // Ждем секунду, пока обновятся банкоматы

        // Обновим еще раз, чтобы в истории версий были разные версии банкоматов
        dep.updateAtm(atm1, "version3");
        dep.updateAtm(atm2, "version4");
        Thread.sleep(1000); // Ждем секунду, пока обновятся банкоматы

        // Восстановим предыдущие версии банкоматов для всей группы и проверим
        dep.restoreAtms(group);

        assertEquals("version1", atm1.getSoftwareVersion());
        assertEquals("version2", atm2.getSoftwareVersion());

    }

}
