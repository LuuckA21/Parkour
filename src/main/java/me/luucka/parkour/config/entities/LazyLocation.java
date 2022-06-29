package me.luucka.parkour.config.entities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LazyLocation {

    private String world;
    private final double x;
    private final double y;
    private final double z;

    public LazyLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String world() {
        return world;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public Location location() {
        if (this.world == null || this.world.isEmpty()) {
            return null;
        }

        World world = Bukkit.getWorld(this.world);

        return new Location(world, x, y, z);
    }

    public static LazyLocation fromLocation(final Location location) {
        return new LazyLocation(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }
}
