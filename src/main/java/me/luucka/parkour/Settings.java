package me.luucka.parkour;

import lombok.Getter;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.config.IConfig;
import me.luucka.parkour.config.entities.LazyItem;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings implements IConfig {

    private static final Logger LOGGER = Logger.getLogger("Parkour");

    private final Messages messages;

    private final BaseConfiguration config;

    @Getter
    private boolean perParkourPermission;

    @Getter
    private LazyItem setStartItem;

    @Getter
    private LazyItem setEndItem;

    @Getter
    private LazyItem wandItem;

    @Getter
    private LazyItem saveItem;

    @Getter
    private LazyItem cancelItem;

    @Getter
    private LazyItem completePlayerCommands;

    @Getter
    private LazyItem completeConsoleCommands;

    @Getter
    private LazyItem setCooldown;

    @Getter
    private LazyItem exitParkourItem;

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
        this.messages = plugin.getMessages();
        this.config = new BaseConfiguration(new File(plugin.getDataFolder(), "config.yml"), "/config.yml");
        reloadConfig();
        if (perParkourPermission) LOGGER.log(Level.INFO, "Using Per-Parkour-Permission");
    }

    @Override
    public void reloadConfig() {
        config.load();
        perParkourPermission = config.getBoolean("per-parkour-permission", false);
        setStartItem = config.getItem("setup-items.set-start");
        setEndItem = config.getItem("setup-items.set-end");
        wandItem = config.getItem("setup-items.wand");
        saveItem = config.getItem("setup-items.save");
        cancelItem = config.getItem("setup-items.cancel");
        completePlayerCommands = config.getItem("setup-items.complete-player-cmd");
        completeConsoleCommands = config.getItem("setup-items.complete-console-cmd");
        setCooldown = config.getItem("setup-items.set-cooldown");
        exitParkourItem = config.getItem("parkour-item.exit");
        completeSign = new String[]{
                config.getString("complete-wall-sign.one", ""),
                config.getString("complete-wall-sign.two", ""),
                config.getString("complete-wall-sign.three", ""),
                config.getString("complete-wall-sign.four", "")
        };
        commandsOnQuit = config.getList("commands-on-quit", String.class);
        mongoDbConnectionUri = config.getString("mongodb-connection-uri", "");
    }

}
