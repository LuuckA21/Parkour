package me.luucka.parkour.managers;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.config.entities.LazyItem;
import me.luucka.parkour.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.luucka.parkour.utils.Color.colorize;

public class ParkourGameManager {

    private final ParkourPlugin plugin;

    private final Map<UUID, String> playersInParkour = new HashMap<>();

    private final PlayerRollbackManager rollbackManager;

    public ParkourGameManager(ParkourPlugin plugin) {
        this.plugin = plugin;
        this.rollbackManager = new PlayerRollbackManager();
    }

    public void playerJoin(final Player player, final String parkourName) {
        if (!plugin.getParkourCooldownManager().exists(player.getUniqueId(), parkourName)) {
            plugin.getParkourCooldownManager().createCooldown(player.getUniqueId(), parkourName);
        }
        if (!canJoin(player, parkourName)) return;

        player.sendMessage(colorize(plugin.getMessages().joinParkour(parkourName)));
        playersInParkour.put(player.getUniqueId(), parkourName);
        rollbackManager.save(player);
        player.teleport(plugin.getParkourDataManager().getStartLocation(parkourName));
        player.setFlying(false);
        setParkourItem(player);
    }

    public void playerQuit(final Player player, final boolean ended) {
        final String lastParkour = getParkourNamePlayerIsIn(player);
        playersInParkour.remove(player.getUniqueId());
        rollbackManager.restore(player);

        if (ended) {
            player.sendMessage(colorize(plugin.getMessages().completeParkour(lastParkour)));
            plugin.getParkourCooldownManager().updateCooldown(player.getUniqueId(), lastParkour, (System.currentTimeMillis() + plugin.getParkourDataManager().getCooldown(lastParkour) * 1000L));
            for (String cmd : plugin.getParkourDataManager().getCompleteConsoleCommands(lastParkour)) {
                cmd = cmd.replace("{PLAYER}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
            for (String cmd : plugin.getParkourDataManager().getCompletePlayerCommands(lastParkour)) {
                player.performCommand(cmd);
            }
        } else {
            for (String cmd : plugin.getSettings().getCommandsOnQuit()) {
                cmd = cmd.replace("{PLAYER}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
            player.sendMessage(colorize(plugin.getMessages().quitParkour(lastParkour)));
        }
    }

    public boolean isPlayerInParkour(final Player player) {
        return playersInParkour.containsKey(player.getUniqueId());
    }

    public String getParkourNamePlayerIsIn(final Player player) {
        return playersInParkour.get(player.getUniqueId());
    }

    private void setParkourItem(final Player player) {
        final LazyItem exitItem = plugin.getSettings().getExitParkourItem();

        ItemStack exit = new ItemBuilder(Material.matchMaterial(exitItem.material()))
                .setDisplayName(colorize(exitItem.name()))
                .setLore(colorize(exitItem.lore()))
                .setPersistentDataContainerValue(plugin, "parkour-item", "EXIT")
                .toItemStack();

        player.getInventory().setItem(8, exit);
    }

    private boolean canJoin(final Player player, final String parkourName) {
        if (player.hasPermission("parkour.bypass")) return true;
        if (plugin.getParkourDataManager().getCooldown(parkourName) <= 0L) return true;

        long systemTime = System.currentTimeMillis();
        long playerTime = plugin.getParkourCooldownManager().getCooldown(player.getUniqueId(), parkourName);
        if (systemTime < playerTime) {
            player.sendMessage(colorize(plugin.getMessages().waitBeforeJoin(parkourName, playerTime - systemTime)));
            return false;
        }
        return true;
    }
}
