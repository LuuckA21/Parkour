package me.luucka.parkour.listener;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.manager.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final PlayerDataManager playerDataManager;

    public PlayerListener(ParkourPlugin plugin) {
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        playerDataManager.createPlayerData(event.getPlayer());
    }
}
