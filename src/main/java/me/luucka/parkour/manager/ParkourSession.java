package me.luucka.parkour.manager;

import lombok.Getter;
import lombok.Setter;
import me.luucka.parkour.setting.Items;
import me.luucka.parkour.setting.Lobby;
import me.luucka.parkour.setting.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.database.model.PlayerParkourData;
import me.luucka.parkour.entity.Checkpoint;
import me.luucka.parkour.entity.Parkour;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

import static me.luucka.parkour.util.MMColor.toComponent;

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
    @Getter
    @Setter
    private Checkpoint currentCheckpoint;

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
        this.currentCheckpoint = new Checkpoint(-1, parkour.getStartLocation(), parkour.getStartLocation());
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

    public Optional<Checkpoint> getNextCheckpoint() {
        return parkour.getCheckpointByNumber(currentCheckpoint.getNumber() + 1);
    }

    @Override
    public void run() {
        parkourTime = System.currentTimeMillis() - startTime;
        player.sendActionBar(toComponent(messages.timeDeathsLayout(parkourTime, deaths)));
    }
}
