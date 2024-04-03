package com.readutf.inari.core.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import lombok.experimental.UtilityClass;
import org.bukkit.block.BlockFace;

import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public final class AngleUtils {

    private static final Map<BlockFace, Integer> FACE_TO_YAW = new EnumMap<>(BlockFace.class);
    private static final Map<Integer, BlockFace> YAW_TO_FACE = new Int2ObjectArrayMap<>();

    static {

        FACE_TO_YAW.put(BlockFace.NORTH, 180);
        FACE_TO_YAW.put(BlockFace.NORTH_NORTH_EAST, 202);
        FACE_TO_YAW.put(BlockFace.NORTH_EAST, 225);
        FACE_TO_YAW.put(BlockFace.EAST_NORTH_EAST, 247);
        FACE_TO_YAW.put(BlockFace.EAST, 270);
        FACE_TO_YAW.put(BlockFace.EAST_SOUTH_EAST, 292);
        FACE_TO_YAW.put(BlockFace.SOUTH_EAST, 315);
        FACE_TO_YAW.put(BlockFace.SOUTH_SOUTH_EAST, 337);
        FACE_TO_YAW.put(BlockFace.SOUTH, 0);
        FACE_TO_YAW.put(BlockFace.SOUTH_SOUTH_WEST, 22);
        FACE_TO_YAW.put(BlockFace.SOUTH_WEST, 45);
        FACE_TO_YAW.put(BlockFace.WEST_SOUTH_WEST, 67);
        FACE_TO_YAW.put(BlockFace.WEST, 90);
        FACE_TO_YAW.put(BlockFace.WEST_NORTH_WEST, 112);
        FACE_TO_YAW.put(BlockFace.NORTH_WEST, 135);
        FACE_TO_YAW.put(BlockFace.NORTH_NORTH_WEST, 157);

        FACE_TO_YAW.forEach((face, yaw) -> YAW_TO_FACE.put(yaw, face));

    }

    public static int faceToYaw(BlockFace face) {
        return FACE_TO_YAW.getOrDefault(face, 0);
    }

    public static BlockFace yawToFace(float yaw) {

        int roundedYaw = (((int) yaw) / 22) * 22;

        System.out.println("Yaw: " + yaw + " Rounded: " + roundedYaw);

        return YAW_TO_FACE.get(wrapAngle(roundedYaw));
    }

    private static int wrapAngle(int angle) {
        int wrappedAngle = angle;

        while (wrappedAngle <= -180) {
            wrappedAngle += 360;
        }

        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }

        return wrappedAngle;
    }

}