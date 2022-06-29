package me.luucka.parkour.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ItemBuilder {

    private static final ItemFactory ITEM_FACTORY;

    private final Material material;
    private final ItemMeta meta;
    private final int amount;

    static {
        ITEM_FACTORY = Bukkit.getItemFactory();
    }
    public ItemBuilder(final Material material) {
        this(material, 1);
    }
    public ItemBuilder(final Material material, final int amount) {
        this.material = material;
        this.meta = ITEM_FACTORY.getItemMeta(material);
        this.amount = amount <= 0 ? 1 : amount;
    }
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.material, this.amount);
        item.setItemMeta(this.meta);
        return item;
    }
    public ItemBuilder setDisplayName(final Component name) {
        meta.displayName(name);
        return this;
    }

    public ItemBuilder setLore(final List<Component> lore) {
        meta.lore(lore);
        return this;
    }

    public ItemBuilder setPersistentDataContainerValue(final JavaPlugin plugin, final String key, final String value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        return this;
    }

}