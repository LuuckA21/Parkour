package me.luucka.parkour.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@AllArgsConstructor
public class Checkpoint {

    @Getter
    private int number;

    @Getter
    private Location tpLocation;

    @Getter
    private Location blockLocation;

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
