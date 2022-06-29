package me.luucka.parkour;

import lombok.Getter;
import me.luucka.parkour.commands.PAdminCommand;
import me.luucka.parkour.commands.ParkourCommand;
import me.luucka.parkour.config.IConfig;
import me.luucka.parkour.listeners.ParkourListeners;
import me.luucka.parkour.listeners.SetupModeListeners;
import me.luucka.parkour.managers.ParkourGameManager;
import me.luucka.parkour.managers.ParkourDataManager;
import me.luucka.parkour.managers.ParkourSetupManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ParkourPlugin extends JavaPlugin {

    @Getter
    private Settings settings;

    @Getter
    private Messages messages;

    @Getter
    private ParkourDataManager parkourDataManager;

    @Getter
    private ParkourGameManager parkourGameManager;

    @Getter
    private ParkourSetupManager parkourSetupManager;

    private final List<IConfig> configList = new ArrayList<>();

    @Override
    public void onEnable() {
        settings = new Settings(this);
        configList.add(settings);

        messages = new Messages(this);
        configList.add(messages);

        parkourDataManager = new ParkourDataManager(this);
        configList.add(parkourDataManager);

        parkourGameManager = new ParkourGameManager(this);

        parkourSetupManager = new ParkourSetupManager(this);

        getServer().getPluginManager().registerEvents(new SetupModeListeners(this), this);
        getServer().getPluginManager().registerEvents(new ParkourListeners(this), this);

        new PAdminCommand(this);
        new ParkourCommand(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload() {
        for (final IConfig iConfig : configList) {
            iConfig.reloadConfig();
        }
    }

}
