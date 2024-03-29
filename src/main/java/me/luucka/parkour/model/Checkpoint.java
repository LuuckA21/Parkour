package me.luucka.parkour.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

public class Checkpoint {

    @Getter
    @Setter
    private int number;

    @Getter
    private Location tpLocation;

    @Getter
    private Location blockLocation;

    public Checkpoint(int number, Location tpLocation, Location blockLocation) {
        this.number = number;
        this.tpLocation = tpLocation;
        this.blockLocation = blockLocation;
    }

    public void updateLocation(final Location tpLocation, final Location blockLocation) {
        this.blockLocation.getBlock().setType(Material.AIR);
        this.tpLocation = tpLocation;
        this.blockLocation = blockLocation;
        this.blockLocation.getBlock().setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Checkpoint that = (Checkpoint) o;

        if (number != that.number) return false;
        return blockLocation.equals(that.blockLocation);
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + (blockLocation != null ? blockLocation.hashCode() : 0);
        return result;
    }
}
