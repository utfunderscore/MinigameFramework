package com.readutf.inari.core.arena.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ArenaMeta {

    private final String name;
    private final String description;
    private final MaterialData materialData;
    private final Map<String, Object> data;

    @JsonCreator
    public ArenaMeta(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("materialData") MaterialData materialData) {
        this.name = name;
        this.description = description;
        this.materialData = materialData;
        this.data = new HashMap<>();
    }
}
