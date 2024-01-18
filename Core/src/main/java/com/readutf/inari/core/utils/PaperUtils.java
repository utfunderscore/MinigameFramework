package com.readutf.inari.core.utils;

import lombok.Getter;

public class PaperUtils {

    private @Getter static boolean paper;

    static {
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            paper = true;
        } catch (ClassNotFoundException e) {
            paper = false;
        }
    }

}
