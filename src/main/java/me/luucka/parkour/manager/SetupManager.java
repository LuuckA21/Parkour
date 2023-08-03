package me.luucka.parkour.manager;

import me.luucka.parkour.setting.Items;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entity.SetupParkour;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupManager {
    private final Items items;
    private final PlayerRollbackManager rollbackManager;

    private final Map<UUID, SetupParkour> playerInSetup = new HashMap<>();

    public SetupManager(ParkourPlugin plugin) {
        this.items = plugin.getItems();
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
        player.getInventory().setItem(0, items.getStartItem());
        player.getInventory().setItem(1, items.getEndItem());
        player.getInventory().setItem(2, items.getWandItem());
        player.getInventory().setItem(3, items.getCheckpointItem());
        player.getInventory().setItem(4, items.getMoreOptions());
        player.getInventory().setItem(6, items.getSaveItem());
        player.getInventory().setItem(8, items.getCancelItem());
    }
}
