package com.readutf.inari.core.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class JsonIgnoreStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(JsonIgnore.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
