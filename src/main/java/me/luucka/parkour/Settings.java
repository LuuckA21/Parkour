package me.luucka.parkour;

import lombok.Getter;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.config.IConfig;

import java.io.File;

public class Settings implements IConfig {

    private final BaseConfiguration config;

    @Getter
    private boolean perParkourPermission;

    @Getter
    private String mongoDbConnectionUri;


    public Settings(final ParkourPlugin plugin) {
        this.config = new BaseConfiguration(new File(plugin.getDataFolder(), "config.yml"), "/config.yml");
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        config.load();
        perParkourPermission = config.getBoolean("per-parkour-permission", false);
        mongoDbConnectionUri = config.getString("mongodb-connection-uri", "");
    }
}
