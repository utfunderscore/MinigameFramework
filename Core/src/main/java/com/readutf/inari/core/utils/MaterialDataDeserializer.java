package com.readutf.inari.core.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.io.IOException;

public class MaterialDataDeserializer extends StdDeserializer<MaterialData> {

    public MaterialDataDeserializer() {
        this(null);
    }

    protected MaterialDataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MaterialData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {

        JsonNode node = p.getCodec().readTree(p);
        String typeName = node.get("itemType").asText();
        byte data = (byte) node.get("data").asInt();

        return new MaterialData(Material.valueOf(typeName), data);
    }
}
