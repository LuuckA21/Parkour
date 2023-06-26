package me.luucka.parkour;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import me.luucka.papergui.PaperGUI;
import me.luucka.parkour.commands.PAdminCommand;
import me.luucka.parkour.commands.ParkourCommand;
import me.luucka.parkour.config.IConfig;
import me.luucka.parkour.listeners.ParkourListeners;
import me.luucka.parkour.listeners.PlayerListener;
import me.luucka.parkour.listeners.SetupListeners;
import me.luucka.parkour.managers.DataManager;
import me.luucka.parkour.managers.GameManager;
import me.luucka.parkour.managers.PlayerDataManager;
import me.luucka.parkour.managers.SetupManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ParkourPlugin extends JavaPlugin {

    public static PaperGUI paperGUI;

    @Getter
    private Settings settings;

    @Getter
    private Items items;

    @Getter
    private Lobby lobby;

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
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .shouldHookPaperReload(true)
        );
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        settings = new Settings(this);
        configList.add(settings);

        items = new Items(this);
        configList.add(items);

        lobby = new Lobby(this);
        configList.add(lobby);

        messages = new Messages(this);
        configList.add(messages);

        dataManager = new DataManager(this);
        configList.add(dataManager);

        playerDataManager = new PlayerDataManager(this);

        gameManager = new GameManager(this);

        setupManager = new SetupManager(this);

        getServer().getPluginManager().registerEvents(new SetupListeners(this), this);
        getServer().getPluginManager().registerEvents(new ParkourListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        new PAdminCommand(this);
        new ParkourCommand(this);

        paperGUI = new PaperGUI(this);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        playerDataManager.shutdown();
    }

    public void reload() {
        for (final IConfig iConfig : configList) {
            iConfig.reloadConfig();
        }
    }

}
