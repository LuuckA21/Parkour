package me.luucka.parkour.managers;

import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.Settings;
import me.luucka.parkour.entities.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.luucka.parkour.utils.MMColor.toComponent;

public class GameManager {

    private final ParkourPlugin plugin;
    private final Messages messages;
    private final PlayerDataManager playerDataManager;
    private final Settings settings;
    private final PlayerRollbackManager rollbackManager;

    private final Map<UUID, Parkour> playersInGame = new HashMap<>();

    public GameManager(ParkourPlugin plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.settings = plugin.getSettings();
        this.rollbackManager = new PlayerRollbackManager();
    }

    public void playerJoin(final Player player, final Parkour parkour) {
        player.sendMessage(toComponent(messages.joinParkour(parkour.getName())));
        playersInGame.put(player.getUniqueId(), parkour);
        rollbackManager.save(player);
        player.teleport(parkour.getStartLocation());
        player.setFlying(false);
        player.getInventory().setItem(8, settings.getExitItem());
    }

    public void playerQuit(final Player player, final boolean ended) {
        final Parkour parkour = playersInGame.get(player.getUniqueId());
        playersInGame.remove(player.getUniqueId());
        rollbackManager.restore(player);

        if (ended) {
            player.sendMessage(toComponent(messages.completeParkour(parkour.getName())));
            playerDataManager.updateLastPlayedTime(
                    player.getUniqueId(),
                    parkour.getName(),
                    System.currentTimeMillis()
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

    public boolean isPlayerInGame(final Player player) {
        return playersInGame.containsKey(player.getUniqueId());
    }

    public Parkour getParkourByPlayer(final Player player) {
        return playersInGame.get(player.getUniqueId());
    }
}
