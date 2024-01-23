package com.readutf.inari.core.arena.marker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.readutf.inari.core.utils.AngleUtils;
import com.readutf.inari.core.utils.Position;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Rotatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@SuppressWarnings("deprecation")
@Getter
public class Marker {

    private final String name;
    private final Position position;
    private final Position offset;
    private final float yaw;

    @JsonCreator
    public Marker(@JsonProperty("name") String name, @JsonProperty("position") Position position, @JsonProperty("offset") Position offset, @JsonProperty("yaw") float yaw) {
        this.name = name;
        this.position = position;
        this.offset = offset;
        this.yaw = yaw;
    }

    @Override
    public String toString() {
        return "Marker{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", yaw=" + yaw +
                '}';
    }

    @JsonIgnore
    public Location toLocation(World world) {
        Location location = position.add(offset).toLocation(world);
        location.setYaw(yaw);
        return location;
    }

    @JsonIgnore
    public Position getPositionWithOffset() {
        return position.add(offset);
    }

    public static @Nullable Marker parseFromSign(Location location) {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign sign)) return null;
        @NotNull String[] lines = sign.getLines();
        if (!lines[0].equalsIgnoreCase("#marker")) return null;


        Position position = new Position(location);

        //get name
        String nameLine = lines[1];

        float yaw = 0;

        if (sign instanceof Rotatable) {
            yaw = AngleUtils.faceToYaw(((Rotatable) sign).getRotation());
        }

        Position offset = new Position(0, 0, 0);

        //check for offset
        String coordinateLine = lines[2];
        if (!coordinateLine.isBlank()) {
            String[] cordinateSplit = coordinateLine.split(",");

            if (cordinateSplit.length != 3) return null;

            double offsetX = Double.parseDouble(cordinateSplit[0]);
            double offsetY = Double.parseDouble(cordinateSplit[1]);
            double offsetZ = Double.parseDouble(cordinateSplit[2]);

             offset = new Position(offsetX, offsetY, offsetZ);
        }


        return new Marker(nameLine, position, offset, yaw);
    }
}
