package ru.otus.lesson3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Main {

    public static void main(String[] args) {

        int initialCapacity = 50;
        DIYarrayList<Integer> list1 = new DIYarrayList<>(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            list1.add(i);
        }
        DIYarrayList<Integer> list2 = new DIYarrayList<>();
        for (int i = 0; i < 30; i++) {
            list2.add(i * 10);
        }

        System.out.println("list1: " + list1);
        System.out.println("list2: " + list2);

        Collections.copy(list1, list2);
        System.out.println("list1 with copied list2:\n" + list1);

        Collections.sort(list1);
        System.out.println("Sorted list1: " + list1);

        Collections.sort(list1, Comparator.reverseOrder());
        System.out.println("Reverse sorted list1: " + list1);

        Collections.addAll(list1, list2.toArray(new Integer[]{}));
        System.out.println("list1 after adding list2: " + list1);

    }

}
