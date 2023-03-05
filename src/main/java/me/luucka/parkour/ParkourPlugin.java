package me.luucka.parkour;

import lombok.Getter;
import me.luucka.parkour.commands.BaseCommand;
import me.luucka.parkour.commands.PAdminCommand;
import me.luucka.parkour.commands.ParkourCommand;
import me.luucka.parkour.config.IConfig;
import me.luucka.parkour.listeners.ParkourListeners;
import me.luucka.parkour.listeners.PlayerListener;
import me.luucka.parkour.listeners.SetupModeListeners;
import me.luucka.parkour.managers.DataManager;
import me.luucka.parkour.managers.GameManager;
import me.luucka.parkour.managers.PlayerDataManager;
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

    @Getter
    private PlayerDataManager playerDataManager;

    private final List<IConfig> configList = new ArrayList<>();

    @Override
    public void onEnable() {
        messages = new Messages(this);
        configList.add(messages);

        settings = new Settings(this);
        configList.add(settings);

        dataManager = new DataManager(this);
        configList.add(dataManager);

        playerDataManager = new PlayerDataManager(this);

        gameManager = new GameManager(this);

        setupManager = new SetupManager(this);

        getServer().getPluginManager().registerEvents(new SetupModeListeners(this), this);
        getServer().getPluginManager().registerEvents(new ParkourListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        new PAdminCommand(this);
        new ParkourCommand(this);
        BaseCommand.registerHelpMap("Parkour", "Parkour", "parkour.admin", "Parkour Help page");
    }

    @Override
    public void onDisable() {
        playerDataManager.shutdown();
    }

    public void reload() {
        for (final IConfig iConfig : configList) {
            iConfig.reloadConfig();
        }
    }

}
