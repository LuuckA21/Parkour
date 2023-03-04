package me.luucka.parkour;

import lombok.Getter;
import me.luucka.helplib.config.IConfig;
import me.luucka.parkour.commands.PAdminCommand;
import me.luucka.parkour.commands.ParkourCommand;
import me.luucka.parkour.listeners.ParkourListeners;
import me.luucka.parkour.listeners.SetupModeListeners;
import me.luucka.parkour.managers.DataManager;
import me.luucka.parkour.managers.GameManager;
import me.luucka.parkour.managers.SetupManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ParkourPlugin extends JavaPlugin {

    @Getter
    private Settings settings;

    @Getter
    private Messages messages;

    @Getter
    private DataManager dataManager;

    @Getter
    private GameManager gameManager;

    @Getter
    private SetupManager setupManager;

//    @Getter
//    private CooldownManager cooldownManager;

    private final List<IConfig> configList = new ArrayList<>();

    @Override
    public void onEnable() {
        settings = new Settings(this);
        configList.add(settings);

        messages = new Messages(this);
        configList.add(messages);

        dataManager = new DataManager(this);
        configList.add(dataManager);

        gameManager = new GameManager(this);

        setupManager = new SetupManager(this);

//        cooldownManager = new CooldownManager(this);

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
