package moe.orangemc.clutchgames.util;

import org.bukkit.Location;

public class LocationUtil {
    public static double distanceWithoutYAxis(Location a, Location b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
    }
}
