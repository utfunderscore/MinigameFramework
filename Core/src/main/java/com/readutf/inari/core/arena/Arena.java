package com.readutf.inari.core.arena;

import com.readutf.inari.core.arena.marker.Marker;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.utils.Cuboid;
import com.readutf.inari.core.utils.JsonIgnore;
import com.readutf.inari.core.utils.Position;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Arena {

    private final String name;
    private final Cuboid bounds;
    private final ArenaMeta arenaMeta;
    private final List<Marker> markers;

    public Arena(String name, Cuboid bounds, ArenaMeta arenaMeta, List<Marker> markers) {
        this.name = name;
        this.bounds = bounds;
        this.markers = markers;
        this.arenaMeta = arenaMeta;
    }

    public @Nullable Marker getMarker(String markerName) {
        return markers.stream().filter(marker -> marker.getName().equalsIgnoreCase(markerName)).findFirst().orElse(null);
    }

    public @JsonIgnore Arena normalize() {
        Position relativePoint = bounds.getMin();

        Cuboid newBounds = new Cuboid(new Position(0, 0, 0), bounds.getMax().subtract(relativePoint));
        List<Marker> newMarkers = markers.stream().map(marker -> {
            System.out.println("before: " + marker.getPosition());
            Position after = marker.getPosition().subtract(relativePoint);
            System.out.println("after: " + after);
            return new Marker(marker.getName(), after, marker.getOffset(), marker.getYaw());
        }).toList();

        return new Arena(name, newBounds, arenaMeta, newMarkers);
    }

    public @JsonIgnore Arena makeRelative(Position position) {

        Cuboid newBounds = new Cuboid(position, bounds.getMax().add(position));
        List<Marker> newMarkers = markers.stream().map(marker -> new Marker(marker.getName(), marker.getPosition().add(position), marker.getOffset(), marker.getYaw())).toList();

        return new Arena(name, newBounds, arenaMeta, newMarkers);
    }

    public @Nullable Cuboid getCuboid(String markerName1, String markerName2) {
        Marker marker1 = getMarker(markerName1);
        Marker marker2 = getMarker(markerName2);
        if (marker1 == null || marker2 == null) return null;
        return new Cuboid(marker1.getPositionWithOffset(), marker2.getPositionWithOffset());
    }

    /**
     * Requires the markers to be of the format "{name}:{id}{1/2} e.g {bounds:1:1} and {bounds:1:2}
     * @param prefix
     * @return
     */
    public @Nullable List<Cuboid> getCuboids(String prefix) {
        List<Marker> markers = getMarkers(prefix);
        Set<String> uniqueIds = new HashSet<>();

        for (Marker marker : markers) {
            String[] split = marker.getName().split(":");
            if (split.length != 3) continue;
            uniqueIds.add(split[0]);
        }

        List<Cuboid> cuboids = new ArrayList<>();

        for (String uniqueId : uniqueIds) {
            Marker marker1 = getMarker(prefix + ":" + uniqueId + ":1");
            Marker marker2 = getMarker(prefix + ":" + uniqueId + ":2");
            System.out.println("marker1: " + prefix + ":" + uniqueId + ":1");
            System.out.println("marker2: " + prefix + ":" + uniqueId + ":2");

            if (marker1 == null || marker2 == null) continue;
            cuboids.add(new Cuboid(marker1.getPositionWithOffset(), marker2.getPositionWithOffset()));
        }

        return cuboids;
    }

    public List<Marker> getMarkers(String prefix) {
        return markers.stream().filter(marker -> marker.getName().startsWith(prefix)).toList();
    }

    @Override
    public String toString() {
        return "Arena{" +
                "name='" + name + '\'' +
                ", bounds=" + bounds +
                ", arenaMeta=" + arenaMeta +
                ", markers=" + markers +
                '}';
    }
}
