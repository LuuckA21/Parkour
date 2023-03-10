package me.luucka.parkour.managers;

import lombok.Getter;
import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.Settings;
import me.luucka.parkour.database.models.PlayerParkourData;
import me.luucka.parkour.entities.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static me.luucka.parkour.utils.MMColor.toComponent;

public class ParkourSession extends BukkitRunnable {

    private final ParkourPlugin plugin;
    private final Settings settings;
    private final Messages messages;
    private final PlayerDataManager playerDataManager;

    @Getter
    private final Player player;
    @Getter
    private final Parkour parkour;

    private int deaths = 0;

    private long parkourTime;

    private long startTime;

    public ParkourSession(final ParkourPlugin plugin, final Player player, final Parkour parkour) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.messages = plugin.getMessages();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.player = player;
        this.parkour = parkour;
    }

    public void start() {
        player.sendMessage(toComponent(messages.joinParkour(parkour.getName())));
        player.teleport(parkour.getStartLocation());
        player.setFlying(false);
        player.getInventory().setItem(8, settings.getLeaveItem());
        startTime = System.currentTimeMillis();
        runTaskTimerAsynchronously(plugin, 0L, 2L);
    }

    public void end(final boolean parkourEnded) {
        cancel();
        if (parkourEnded) {
            player.sendMessage(toComponent(messages.completeParkour(parkour.getName())));
            playerDataManager.updateParkourData(
                    player.getUniqueId(),
                    parkour.getName(),
                    new PlayerParkourData(System.currentTimeMillis(), deaths, parkourTime)
            );
            for (String cmd : parkour.getConsoleCommands()) {
                cmd = cmd.replace("{PLAYER}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
            for (String cmd : parkour.getPlayerCommands()) {
                player.performCommand(cmd);
            }
        } else {
            for (String cmd : settings.getCommandsOnQuit()) {
                player.performCommand(cmd);
            }
            player.sendMessage(toComponent(messages.quitParkour(parkour.getName())));
        }
    }

    public void incrementDeaths() {
        deaths++;
    }

    @Override
    public void run() {
        parkourTime = System.currentTimeMillis() - startTime;
        Instant instant = Instant.ofEpochMilli(parkourTime);
        LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        player.sendActionBar(toComponent("<#c2c2c2>" + DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(datetime)));
    }
}
