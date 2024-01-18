package com.readutf.inari.test;

public class Test {

    public static class A {

    }

    public static class B extends A {

    }

    public static void main(String[] args) {


        B b = new B();

        System.out.println(b instanceof A);
        System.out.println(A.class.isAssignableFrom(b.getClass()));


    }


}
