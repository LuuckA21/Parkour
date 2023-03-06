package me.luucka.parkour;

import lombok.Getter;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.config.IConfig;
import me.luucka.parkour.config.entities.LazyItem;
import me.luucka.parkour.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings implements IConfig {

    private static final Logger LOGGER = Logger.getLogger("Parkour");
    private final ParkourPlugin plugin;
    private final Messages messages;

    private final BaseConfiguration config;

    @Getter
    private boolean perParkourPermission;

    @Getter
    private ItemStack startItem;

    @Getter
    private ItemStack endItem;

    @Getter
    private ItemStack wandItem;

    @Getter
    private ItemStack playerCommandsItem;

    @Getter
    private ItemStack consoleCommandsItem;

    @Getter
    private ItemStack cooldownItem;

    @Getter
    private ItemStack saveItem;

    @Getter
    private ItemStack cancelItem;

    @Getter
    private ItemStack exitItem;

    private String[] completeSign;

    @Getter
    private List<String> commandsOnQuit;

    @Getter
    private String mongoDbConnectionUri;

    public String[] getCompleteSign(final String parkour) {
        final String[] newSign = new String[4];
        for (int i = 0; i < completeSign.length; i++) {
            newSign[i] = completeSign[i].replace("{PARKOUR}", parkour).replace("{PREFIX}", messages.getPrefix());
        }
        return newSign;
    }

    public Settings(final ParkourPlugin plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.config = new BaseConfiguration(new File(plugin.getDataFolder(), "config.yml"), "/config.yml");
        reloadConfig();
        if (perParkourPermission) LOGGER.log(Level.INFO, "Using Per-Parkour-Permission");
    }

    @Override
    public void reloadConfig() {
        config.load();
        perParkourPermission = config.getBoolean("per-parkour-permission", false);
        startItem = toItemStack(config.getItem("setup-items.set-start"), "SETSTART");
        endItem = toItemStack(config.getItem("setup-items.set-end"), "SETEND");
        wandItem = toItemStack(config.getItem("setup-items.wand"), "WAND");
        playerCommandsItem = toItemStack(config.getItem("setup-items.complete-player-cmd"), "PLAYER-CMD");
        consoleCommandsItem = toItemStack(config.getItem("setup-items.complete-console-cmd"), "CONSOLE-CMD");
        cooldownItem = toItemStack(config.getItem("setup-items.set-cooldown"), "COOLDOWN");
        saveItem = toItemStack(config.getItem("setup-items.save"), "SAVE");
        cancelItem = toItemStack(config.getItem("setup-items.cancel"), "CANCEL");
        exitItem = toItemStack(config.getItem("parkour-item.exit"), "EXIT");
        completeSign = new String[]{
                config.getString("complete-wall-sign.one", ""),
                config.getString("complete-wall-sign.two", ""),
                config.getString("complete-wall-sign.three", ""),
                config.getString("complete-wall-sign.four", "")
        };
        commandsOnQuit = config.getList("commands-on-quit", String.class);
        mongoDbConnectionUri = config.getString("mongodb-connection-uri", "");
    }

    private ItemStack toItemStack(final LazyItem item, final String persistentValue) {
        return new ItemBuilder(item.material())
                .setDisplayName(item.name())
                .setLore(item.lore())
                .setPersistentDataContainerValue(plugin, "setup-item", persistentValue)
                .toItemStack();
    }

}
