package com.readutf.inari.core.utils.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.events.GameDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.Map;


public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack>, Listener {

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Object> map = Game.getGson().fromJson(json, TypeToken.get(Map.class).getType());
        map.putIfAbsent("v", Bukkit.getUnsafe().getDataVersion());

        if(map.containsKey("meta")) {
            Map<String, Object> meta = (Map<String, Object>) map.get("meta");
            ConfigurationSerializable deserializedMeta = context.deserialize(Game.getGson().toJsonTree(meta), ConfigurationSerializable.class);
            map.remove("meta");
            ItemStack is = ItemStack.deserialize(map);
            is.setItemMeta((ItemMeta) deserializedMeta);
            return is;
        } else {
            return ItemStack.deserialize(map);
        }
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        Map<String, Object> map = src.serialize();
        map.putIfAbsent("v", Bukkit.getUnsafe().getDataVersion());
        if(src.hasItemMeta()) {
            JsonElement meta = context.serialize(src.getItemMeta(), ConfigurationSerializable.class);
            map.put("meta", meta.getAsJsonObject());
        }
        return Game.getGson().toJsonTree(map);
    }



}