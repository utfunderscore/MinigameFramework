package com.readutf.inari.test;

import java.util.Comparator;
import java.util.List;

public class Test {

    public static class A {

    }

    public static class B extends A {

    }

    public static void main(String[] args) {


        List<Integer> integers = new java.util.ArrayList<>(List.of(1, 5, 4, 3));

        integers.sort(Comparator.comparingInt(value -> (int) value).reversed());

        System.out.println(integers);

    }


}
