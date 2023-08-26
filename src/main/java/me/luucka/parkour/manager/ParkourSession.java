package me.luucka.parkour.manager;

import lombok.Getter;
import lombok.Setter;
import me.luucka.extendlibrary.message.Message;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.model.Checkpoint;
import me.luucka.parkour.model.Parkour;
import me.luucka.parkour.setting.Items;
import me.luucka.parkour.setting.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ParkourSession extends BukkitRunnable {

    private final ParkourPlugin plugin;
    private final Lobby lobby;
    private final Message messages;
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
        player.showTitle(Title.title(
                messages.from("parkour-join").with("parkour", parkour.getName()).build(),
                Component.text("")
        ));
        player.teleport(parkour.getStartLocation());
        player.setFlying(false);
        player.getInventory().setItem(8, items.getLeaveItem());
        startTime = System.currentTimeMillis();
        runTaskTimerAsynchronously(plugin, 0L, 2L);
    }

    public void end(final boolean parkourEnded) {
        cancel();
        if (parkourEnded) {
            player.showTitle(Title.title(
                    messages.from("parkour-completed").with("parkour", parkour.getName()).build(),
                    Component.text("")
            ));
//            playerDataManager.updateParkourData(
//                    player.getUniqueId(),
//                    parkour.getName(),
//                    new PlayerParkourData(System.currentTimeMillis(), deaths, parkourTime)
//            );
            for (String cmd : parkour.getCompleteCommands()) {
                cmd = cmd.replace("{PLAYER}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        } else {
            player.showTitle(Title.title(
                    messages.from("parkour-leave").with("parkour", parkour.getName()).build(),
                    Component.text("")
            ));
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
        player.sendActionBar(
                messages.from("parkour-time-deaths-layout")
                        .with("time", formatTime(parkourTime))
                        .withNumber("deaths", deaths)
                        .build()
        );
    }

    private String formatTime(final long time) {
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(datetime);
    }
}
