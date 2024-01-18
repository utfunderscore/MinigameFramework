package com.readutf.inari.core.utils;

import org.jetbrains.annotations.Nullable;

public class NumberUtils {

    public static @Nullable Integer parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
