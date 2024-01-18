package com.readutf.inari.core.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor @Getter
public class GameEventListener {

    private final Object owner;
    private final Method method;

}
