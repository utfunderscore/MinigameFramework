package com.readutf.inari.core.arena.meta;


import lombok.Getter;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ArenaMeta {

    private final String name;
    private final String description;
    private final MaterialData materialData;
    private final Map<String, Object> data;

    public ArenaMeta(String name, String description, MaterialData materialData) {
        this.name = name;
        this.description = description;
        this.materialData = materialData;
        this.data = new HashMap<>();
    }
}
