package me.luucka.parkour.managers;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.Settings;
import me.luucka.parkour.entities.SetupParkour;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupManager {
    private final Settings settings;
    private final PlayerRollbackManager rollbackManager;

    private final Map<UUID, SetupParkour> playerInSetup = new HashMap<>();

    public SetupManager(ParkourPlugin plugin) {
        this.settings = plugin.getSettings();
        this.rollbackManager = new PlayerRollbackManager();
    }

    public boolean isPlayerInSetup(final Player player) {
        return playerInSetup.containsKey(player.getUniqueId());
    }

    public SetupParkour getSetupParkourByPlayer(final Player player) {
        return playerInSetup.get(player.getUniqueId());
    }

    public void playerJoin(final Player player, final SetupParkour parkour) {
        if (isPlayerInSetup(player)) return;

        playerInSetup.put(player.getUniqueId(), parkour);
        rollbackManager.save(player);
        player.setGameMode(GameMode.CREATIVE);
        setItems(player);
    }

    public void playerQuit(final Player player) {
        if (!isPlayerInSetup(player)) return;
        playerInSetup.remove(player.getUniqueId());
        rollbackManager.restore(player);
    }

    private void setItems(final Player player) {
        player.getInventory().setItem(0, settings.getStartItem());
        player.getInventory().setItem(1, settings.getEndItem());
        player.getInventory().setItem(2, settings.getWandItem());
        player.getInventory().setItem(3, settings.getPlayerCommandsItem());
        player.getInventory().setItem(4, settings.getConsoleCommandsItem());
        player.getInventory().setItem(5, settings.getCooldownItem());
        player.getInventory().setItem(7, settings.getSaveItem());
        player.getInventory().setItem(8, settings.getCancelItem());
    }
}
