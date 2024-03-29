package com.readutf.inari.core.arena;

import com.readutf.inari.core.arena.marker.Marker;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.utils.Cuboid;
import com.readutf.inari.core.utils.JsonIgnore;
import com.readutf.inari.core.utils.Position;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    public @JsonIgnore Arena makeRelative() {
        Position relativePoint = bounds.getMin();

        Cuboid newBounds = new Cuboid(new Position(0, 0, 0), bounds.getMax().subtract(relativePoint));
        List<Marker> newMarkers = markers.stream().map(marker -> new Marker(marker.getName(), marker.getPosition().subtract(relativePoint), marker.getOffset(), marker.getYaw())).toList();

        return new Arena(name, newBounds, arenaMeta, newMarkers);
    }

    public @JsonIgnore Arena makeRelative(Position position) {

        Cuboid newBounds = new Cuboid(position, bounds.getMax().add(position));
        List<Marker> newMarkers = markers.stream().map(marker -> new Marker(marker.getName(), marker.getPosition().add(position), marker.getOffset(),marker.getYaw())).toList();

        return new Arena(name, newBounds, arenaMeta, newMarkers);
    }

    public @Nullable Cuboid getCuboid(String markerName1, String markerName2) {
        Marker marker1 = getMarker(markerName1);
        Marker marker2 = getMarker(markerName2);
        if (marker1 == null || marker2 == null) return null;
        return new Cuboid(marker1.getPositionWithOffset(), marker2.getPositionWithOffset());
    }

    public List<Marker> getMarkers(String prefix) {
        return markers.stream().filter(marker -> marker.getName().startsWith(prefix)).toList();
    }

}
