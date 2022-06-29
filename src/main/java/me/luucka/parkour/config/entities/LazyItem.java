package me.luucka.parkour.config.entities;

import java.util.List;

public class LazyItem {

    private final String material;

    private final String name;

    private final List<String> lore;

    public LazyItem(String material, String name, List<String> lore) {
        this.material = material;
        this.name = name;
        this.lore = lore;
    }

    public String material() {
        return material;
    }

    public String name() {
        return name;
    }

    public List<String> lore() {
        return lore;
    }
}
