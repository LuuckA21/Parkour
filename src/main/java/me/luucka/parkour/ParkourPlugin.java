package me.luucka.parkour;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import me.luucka.extendlibrary.message.Message;
import me.luucka.extendlibrary.util.IReload;
import me.luucka.papergui.PaperGUI;
import me.luucka.parkour.command.PAdminCommand;
import me.luucka.parkour.command.ParkourArgument;
import me.luucka.parkour.command.ParkourCommand;
import me.luucka.parkour.listener.ParkourListeners;
import me.luucka.parkour.listener.PlayerListener;
import me.luucka.parkour.listener.SetupListeners;
import me.luucka.parkour.manager.DataManager;
import me.luucka.parkour.manager.GameManager;
import me.luucka.parkour.manager.PlayerDataManager;
import me.luucka.parkour.manager.SetupManager;
import me.luucka.parkour.setting.Items;
import me.luucka.parkour.setting.Lobby;
import me.luucka.parkour.setting.Setting;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ParkourPlugin extends JavaPlugin {

    public static PaperGUI paperGUI;

    @Getter
    private Setting setting;

    @Getter
    private Items items;

    @Getter
    private Lobby lobby;

    @Getter
    private Message messages;

    @Getter
    private DataManager dataManager;

    @Getter
    private GameManager gameManager;

    @Getter
    private SetupManager setupManager;

    @Getter
    private PlayerDataManager playerDataManager;

    private final List<IReload> reloadList = new ArrayList<>();

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .shouldHookPaperReload(true)
        );
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        paperGUI = new PaperGUI(this);

        setting = new Setting(this);
        reloadList.add(setting);

        items = new Items(this);
        reloadList.add(items);

        lobby = new Lobby(this);
        reloadList.add(lobby);

        messages = new Message(this, "messages");
        messages.addPrefix();
        reloadList.add(messages);

        dataManager = new DataManager(this);
        reloadList.add(dataManager);

        playerDataManager = new PlayerDataManager(this);

        gameManager = new GameManager(this);

        setupManager = new SetupManager(this);

        getServer().getPluginManager().registerEvents(new SetupListeners(this), this);
        getServer().getPluginManager().registerEvents(new ParkourListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        ParkourArgument.set(this);
        new PAdminCommand(this);
        new ParkourCommand(this);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        playerDataManager.shutdown();
    }

    public void reload() {
        for (final IReload iConfig : reloadList) {
            iConfig.reload();
        }
    }

}
