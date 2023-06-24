package me.luucka.parkour;

import lombok.Getter;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.config.IConfig;
import me.luucka.parkour.config.entities.LazyItem;
import me.luucka.parkour.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Items implements IConfig {

    private final ParkourPlugin plugin;

    private final BaseConfiguration config;

    public Items(final ParkourPlugin plugin) {
        this.plugin = plugin;
        this.config = new BaseConfiguration(new File(plugin.getDataFolder(), "items.yml"), "/items.yml");
        reloadConfig();
    }

    @Getter
    private ItemStack leaveItem,
            startItem,
            endItem,
            wandItem,
            checkpointItem,
            checkpointListItem,
            moreOptions,
            completeCommands,
            cooldownItem,
            saveItem,
            cancelItem;

    private LazyItem lazyCheckpointListItem;

    public ItemStack getCheckpointListItem(final int number, final Location location) {
        Component newName = lazyCheckpointListItem.name();
        List<Component> newLore = new ArrayList<>(lazyCheckpointListItem.lore());
        newLore.replaceAll(component -> component
                .replaceText(TextReplacementConfig.builder().matchLiteral("{X}").replacement(Double.toString(location.getX())).build())
                .replaceText(TextReplacementConfig.builder().matchLiteral("{Y}").replacement(Double.toString(location.getY())).build())
                .replaceText(TextReplacementConfig.builder().matchLiteral("{Z}").replacement(Double.toString(location.getZ())).build())
        );
        return new ItemBuilder(lazyCheckpointListItem.material())
                .setDisplayName(newName.replaceText(TextReplacementConfig.builder().matchLiteral("{NUMBER}").replacement(Integer.toString(number)).build()))
                .setLore(newLore)
                .toItemStack();
    }

    @Override
    public void reloadConfig() {
        config.load();
        startItem = toItemStack(config.getItem("setup-items.set-start"), "setup-item", "SETSTART");
        endItem = toItemStack(config.getItem("setup-items.set-end"), "setup-item", "SETEND");
        wandItem = toItemStack(config.getItem("setup-items.wand"), "setup-item", "WAND");
        checkpointItem = toItemStack(config.getItem("setup-items.checkpoint"), "setup-item", "CHECKPOINT");
        lazyCheckpointListItem = config.getItem("setup-items.checkpoint-list-item");
        moreOptions = toItemStack(config.getItem("setup-items.more-options"), "setup-item", "MORE-OPTIONS");
        completeCommands = toItemStack(config.getItem("setup-items.complete-commands"), "setup-item", "COMPLETE-CMD");
        cooldownItem = toItemStack(config.getItem("setup-items.set-cooldown"), "setup-item", "COOLDOWN");
        saveItem = toItemStack(config.getItem("setup-items.save"), "setup-item", "SAVE");
        cancelItem = toItemStack(config.getItem("setup-items.cancel"), "setup-item", "CANCEL");
        leaveItem = toItemStack(config.getItem("parkour-item.leave"), "parkour-item", "LEAVE");
    }

    private ItemStack toItemStack(final LazyItem item, final String key, final String persistentValue) {
        return new ItemBuilder(item.material())
                .setDisplayName(item.name())
                .setLore(item.lore())
                .setPersistentDataContainerValue(plugin, key, persistentValue)
                .toItemStack();
    }
}
