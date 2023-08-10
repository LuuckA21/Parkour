package me.luucka.parkour.setting;

import lombok.Getter;
import me.luucka.extendlibrary.util.IReload;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.config.BaseConfiguration;

import java.io.File;

public class Settings implements IReload {

    private final BaseConfiguration config;

    @Getter
    private boolean perParkourPermission;

    @Getter
    private String mongoDbConnectionUri;


    public Settings(final ParkourPlugin plugin) {
        this.config = new BaseConfiguration(new File(plugin.getDataFolder(), "config.yml"), "/config.yml");
        reload();
    }

    @Override
    public void reload() {
        config.load();
        perParkourPermission = config.getBoolean("per-parkour-permission", false);
        mongoDbConnectionUri = config.getString("mongodb-connection-uri", "");
    }
}
