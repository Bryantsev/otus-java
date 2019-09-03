package ru.otus.lesson9;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Alexander Bryantsev on 07.08.2019.
 */
public class Benchmark {

    private int size;
    private final int loopCounter;
    private final int oldListSize;
    private final int leakMemoryPercent;

    public Benchmark(int size, int loopCounter, int oldPercent, int leakMemoryPercent) {
        this.loopCounter = loopCounter;
        // Вычисляем размер массива "старых" объектов в рамках size. При этом новые объекты всегда берутся как 100%
        this.oldListSize = size / (100 + oldPercent) * oldPercent;
        this.size = size - this.oldListSize;
        this.leakMemoryPercent = leakMemoryPercent;
    }

    long run() {

        long processedObjects = 0;

        // Заполняем массив постоянных объектов (старых)
        Object[] oldList = IntStream.range(0, oldListSize).mapToObj(i -> new String(new char[0])).toArray();
        System.out.println("start. oldList size: " + oldList.length);

        // Готовим список рабочих объектов
        List<Integer> workList = new ArrayList<>(size);
        IntStream.range(0, size).forEach(workList::add);
        processedObjects += workList.size();
        System.out.println("start. workList size: " + workList.size());

        try {
            for (int idx = 0; idx < loopCounter; idx++) {
                // Очищаем список, оставляя заданное кол-во объектов для утечки памяти
                int curListSize = workList.size();
                if (leakMemoryPercent > 0) {
                    final int fromIndex = (int) (curListSize * ((double) leakMemoryPercent / 100));
                    workList.subList(fromIndex, curListSize).clear();
                } else {
                    workList.clear();
                }

                // Если есть утечка, то увеличиваем исходный размер на размер утечки
                if (leakMemoryPercent > 0) {
                    size = workList.size() + size;
                }
                ((ArrayList<Integer>) workList).ensureCapacity(size);
                IntStream.range(workList.size(), size).forEach(workList::add);

                processedObjects += workList.size();
                Thread.sleep(20); // дадим поработать мусорщику и работаем дальше
            }

        } catch (Error ex) {
            processedObjects += workList.size();
            workList.clear(); // Очищаем, предполагая, что упали по OOM, чтобы высвободить память для завершающих операций
            System.out.println("Error: " + ex.getMessage());

        } catch (InterruptedException ex) {
            System.out.println("InterruptedException: " + ex.getMessage());
        }

        long count = 0;
        for (Object obj : oldList) {
            count += obj != null ? 1 : 0;
        }
        System.out.println("end. oldList size: " + count);

        return processedObjects;
    }

}
