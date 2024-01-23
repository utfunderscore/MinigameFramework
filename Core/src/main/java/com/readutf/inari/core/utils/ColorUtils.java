package com.readutf.inari.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {

    public static Component color(String s) {
        return LegacyComponentSerializer.legacy('&').deserialize(s);
    }

}
