package me.luucka.parkour.managers;

import me.luucka.parkour.ParkourPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParkourGameManager {

    private final ParkourPlugin plugin;

    private final Map<UUID, String> playersInParkour = new HashMap<>();

    public ParkourGameManager(ParkourPlugin PLUGIN) {
        this.plugin = PLUGIN;
    }

    public void playerJoin(final Player player, final String parkourName) {
        playersInParkour.put(player.getUniqueId(), parkourName);
        player.teleport(plugin.getParkourDataManager().getStartLocation(parkourName));
        player.setFlying(false);
    }

    public void playerQuit(final Player player, final boolean ended) {
        final String lastParkour = getParkourNamePlayerIsIn(player);
        playersInParkour.remove(player.getUniqueId());

        if (ended) {
            for (String cmd : plugin.getParkourDataManager().getCompleteConsoleCommands(lastParkour)) {
                cmd = cmd.replace("{PLAYER}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
            for (String cmd : plugin.getParkourDataManager().getCompletePlayerCommands(lastParkour)) {
                player.performCommand(cmd);
            }
        }
    }

    public boolean isPlayerInParkour(final Player player) {
        return playersInParkour.containsKey(player.getUniqueId());
    }

    public String getParkourNamePlayerIsIn(final Player player) {
        return playersInParkour.get(player.getUniqueId());
    }
}
