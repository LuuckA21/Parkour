package me.luucka.parkour.config.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LazyLocation {

    private String world;
    private final double x;
    private final double y;
    private final double z;

    private final float yaw;

    private final float pitch;

    public LazyLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
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

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public Location location() {
        if (this.world == null || this.world.isEmpty()) {
            return null;
        }

        World world = Bukkit.getWorld(this.world);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static LazyLocation fromLocation(final Location location) {
        return new LazyLocation(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }
}
