package ru.otus.lesson3;

import java.util.Collections;

public class Main {

    public static void main(String[] args) {

        int initialCapacity = 50;
        DIYarrayList<Integer> list1 = new DIYarrayList(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            list1.set(i, i);
        }
        DIYarrayList<Integer> list2 = new DIYarrayList();
        for (int i = 0; i < 30; i++) {
            list2.add(i * 10);
        }

        System.out.println("list1: " + list1);
        System.out.println("list2: " + list2);

        Collections.copy(list1, list2);
        System.out.println("list1 with copied list2:\n" + list1);

        Collections.sort(list1);
        System.out.println("Sorted list1: " + list1);

    }

}
