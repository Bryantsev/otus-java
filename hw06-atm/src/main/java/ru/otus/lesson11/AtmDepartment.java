package ru.otus.lesson11;

import java.util.*;

/**
 * Подразделение для работы с банкоматами
 */
public class AtmDepartment {

    /**
     * Банкоматы и их история версий ПО, пополняемая при обновлении и сокращаемая при восстановлении всегда с конца
     */
    private Map<CustomAtm, Deque<CustomAtm.SoftwareVersion>> atms = new HashMap<>();

    public void addAtm(CustomAtm atm) {
        atms.putIfAbsent(atm, new ArrayDeque<>());
    }

    public boolean removeAtm(CustomAtm atm) {
        if (atms.containsKey(atm)) {
            return atms.remove(atm, atms.get(atm));
        }
        return false;
    }

    /**
     * Сохранить текущие версии ПО банкоматов
     * @param atms Список банкоматов или групп
     */
    private void saveAtmsCurrentSoftwareVersion(List<Atm> atms) throws Exception {
        for (Atm atm : atms) {
            if (!atm.isGroup()) {
                if (this.atms.containsKey(atm)) {
                    this.atms.get(atm).addLast(atm.saveSoftwareVersion());
                }
            } else {
                // Группы сохраняем рекурсивно
                saveAtmsCurrentSoftwareVersion(((AtmGroup) atm).getAtms());
            }
        }
    }

    /**
     * Обновить ПО банкоматов по списку
     *
     * @param atms Список банкоматов для обновления
     * @return Успешность обновления
     */
    public boolean updateAtms(List<Atm> atms, String newVersion) throws Exception {
        saveAtmsCurrentSoftwareVersion(atms);

        atms.forEach(atm -> atm.update(newVersion));

        return true;
    }

    public boolean updateAtms(AtmGroup atmGroup, String newVersion) throws Exception {
        return updateAtms(atmGroup.getAtms(), newVersion);
    }

    public boolean updateAtm(Atm atm, String newVersion) throws Exception {
        List<Atm> list = new ArrayList<>();
        list.add(atm);

        return updateAtms(list, newVersion);
    }

    /**
     * Восстановить предыдущую версию ПО банкоматов из списка
     * @param atms Список банкоматов
     * @return Успешность восстановления
     */
    public boolean restoreAtms(List<Atm> atms) throws Exception {
        for (Atm atm : atms) {
            if (!atm.isGroup()) {
                if (this.atms.containsKey(atm)) {
                    final Deque<Atm.SoftwareVersion> versions = this.atms.get(atm);
                    if (!versions.isEmpty()) {
                        atm.restoreSoftwareVersion(versions.pollLast());
                    }
                }
            } else {
                // Группы восстанавливаем рекурсивно
                restoreAtms(((AtmGroup) atm).getAtms());
            }
        }

        return true;
    }

    public boolean restoreAtms(AtmGroup atmGroup) throws Exception {
        return restoreAtms(atmGroup.getAtms());
    }

    public boolean restoreAtm(Atm atm) throws Exception {
        List<Atm> list = new ArrayList<>();
        list.add(atm);

        return restoreAtms(list);
    }

}
