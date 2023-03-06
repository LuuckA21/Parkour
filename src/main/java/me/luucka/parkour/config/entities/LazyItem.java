package me.luucka.parkour.config.entities;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

public record LazyItem(Material material, Component name, List<Component> lore) {
}
