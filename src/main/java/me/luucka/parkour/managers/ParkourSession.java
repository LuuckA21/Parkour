package me.luucka.parkour.managers;

import lombok.Getter;
import me.luucka.parkour.Items;
import me.luucka.parkour.Lobby;
import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.database.models.PlayerParkourData;
import me.luucka.parkour.entities.Parkour;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.luucka.parkour.utils.MMColor.toComponent;

public class ParkourSession extends BukkitRunnable {

    private final ParkourPlugin plugin;
    private final Lobby lobby;
    private final Messages messages;
    private final PlayerDataManager playerDataManager;
    private final Items items;

    @Getter
    private final Player player;
    @Getter
    private final Parkour parkour;

    private int deaths = 0;

    private long parkourTime;

    private long startTime;

    public ParkourSession(final ParkourPlugin plugin, final Player player, final Parkour parkour) {
        this.plugin = plugin;
        this.lobby = plugin.getLobby();
        this.messages = plugin.getMessages();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.items = plugin.getItems();
        this.player = player;
        this.parkour = parkour;
    }

    public void start() {
        player.showTitle(Title.title(toComponent(messages.parkourJoin(parkour.getName())), toComponent("")));
        player.teleport(parkour.getStartLocation());
        player.setFlying(false);
        player.getInventory().setItem(8, items.getLeaveItem());
        startTime = System.currentTimeMillis();
        runTaskTimerAsynchronously(plugin, 0L, 2L);
    }

    public void end(final boolean parkourEnded) {
        cancel();
        if (parkourEnded) {
            player.showTitle(Title.title(toComponent(messages.parkourCompleted(parkour.getName())), toComponent("")));
            playerDataManager.updateParkourData(
                    player.getUniqueId(),
                    parkour.getName(),
                    new PlayerParkourData(System.currentTimeMillis(), deaths, parkourTime)
            );
            for (String cmd : parkour.getCompleteCommands()) {
                cmd = cmd.replace("{PLAYER}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        } else {
            player.showTitle(Title.title(toComponent(messages.parkourLeave(parkour.getName())), toComponent("")));
        }
        player.teleport(lobby.getLobbyLocation());
    }

    public void incrementDeaths() {
        deaths++;
    }

    @Override
    public void run() {
        parkourTime = System.currentTimeMillis() - startTime;
        player.sendActionBar(toComponent(messages.timeDeathsLayout(parkourTime, deaths)));
    }
}
