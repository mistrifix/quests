package net.mistrifix.main.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {

    public static Location parseLocation(String string) {
        if (string == null) {
            return null;
        }

        String[] locSplit = string.split(",");

        if (locSplit.length < 4) {
            return null;
        }

        World world = Bukkit.getWorld(locSplit[0]);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        return new Location(world, Integer.parseInt(locSplit[1]), Integer.parseInt(locSplit[2]), Integer.parseInt(locSplit[3]));
    }

    public static String toString(Location loc) {
        if (loc == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(loc.getWorld().getName());
        sb.append(",");
        sb.append(loc.getBlockX());
        sb.append(",");
        sb.append(loc.getBlockY());
        sb.append(",");
        sb.append(loc.getBlockZ());

        return sb.toString();
    }
}
