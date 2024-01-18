package com.readutf.inari.core.arena.marker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.readutf.inari.core.utils.AngleUtils;
import com.readutf.inari.core.utils.Position;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
@Getter
public class Marker {

    private final String name;
    private final Position position;
    private final float yaw;

    @JsonCreator
    public Marker(@JsonProperty("name") String name, @JsonProperty("position") Position position, @JsonProperty("yaw") float yaw) {
        this.name = name;
        this.position = position;
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

    public static @Nullable Marker parseFromSign(Location location) {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign sign)) return null;
        @NotNull String[] lines = sign.getLines();
        if (!lines[0].equalsIgnoreCase("#marker")) return null;

        System.out.println("found marker");

        Position position = new Position(location);

        //get name
        String nameLine = lines[1];

        //check for skull for locations
        float yaw = 0;
        if (block.getRelative(BlockFace.UP) instanceof Skull skull) {
            BlockFace rotation = skull.getRotation();

            yaw = AngleUtils.faceToYaw(rotation);
        }

        //check for offset
        String coordinateLine = lines[2];
        if (!coordinateLine.isBlank()) {
            String[] cordinateSplit = coordinateLine.split(" ");
            if (cordinateSplit.length != 3) return null;

            double offsetX = Double.parseDouble(cordinateSplit[0]);
            double offsetY = Double.parseDouble(cordinateSplit[1]);
            double offsetZ = Double.parseDouble(cordinateSplit[2]);

            position = position.add(offsetX, offsetY, offsetZ);
        }


        return new Marker(nameLine, position, yaw);
    }
}
