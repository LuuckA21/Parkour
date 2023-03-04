package me.luucka.parkour.managers;

import me.luucka.helplib.item.ItemBuilder;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.config.entities.LazyItem;
import me.luucka.parkour.entities.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.luucka.helplib.color.MMColor.toComponent;

public class GameManager {

    private final ParkourPlugin plugin;

    private final Map<UUID, Parkour> playersInGame = new HashMap<>();

    private final PlayerRollbackManager rollbackManager;

    public GameManager(ParkourPlugin plugin) {
        this.plugin = plugin;
        this.rollbackManager = new PlayerRollbackManager();
    }

    public void playerJoin(final Player player, final Parkour parkour) {
//        if (!plugin.getParkourCooldownManager().exists(player.getUniqueId(), parkourName)) {
//            plugin.getParkourCooldownManager().createCooldown(player.getUniqueId(), parkourName);
//        }
//        if (!canJoin(player, parkourName)) return;

        player.sendMessage(toComponent(plugin.getMessages().joinParkour(parkour.getName())));
        playersInGame.put(player.getUniqueId(), parkour);
        rollbackManager.save(player);
        player.teleport(parkour.getStartLocation());
        player.setFlying(false);
        setParkourItem(player);
    }

    public void playerQuit(final Player player, final boolean ended) {
        final Parkour parkour = playersInGame.get(player.getUniqueId());
        playersInGame.remove(player.getUniqueId());
        rollbackManager.restore(player);

        if (ended) {
            player.sendMessage(toComponent(plugin.getMessages().completeParkour(parkour.getName())));
//            plugin.getParkourCooldownManager().updateCooldown(player.getUniqueId(), lastParkour, (System.currentTimeMillis() + plugin.getParkourDataManager().getCooldown(lastParkour) * 1000L));
            for (String cmd : parkour.getConsoleCommands()) {
                cmd = cmd.replace("{PLAYER}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
            for (String cmd : parkour.getPlayerCommands()) {
                player.performCommand(cmd);
            }
        } else {
            for (String cmd : plugin.getSettings().getCommandsOnQuit()) {
                player.performCommand(cmd);
            }
            player.sendMessage(toComponent(plugin.getMessages().quitParkour(parkour.getName())));
        }
    }

    public boolean isPlayerInParkourGame(final Player player) {
        return playersInGame.containsKey(player.getUniqueId());
    }

    public Parkour getPlayerParkour(final Player player) {
        return playersInGame.get(player.getUniqueId());
    }

    private void setParkourItem(final Player player) {
        final LazyItem exitItem = plugin.getSettings().getExitParkourItem();

        ItemStack exit = new ItemBuilder(exitItem.material())
                .setDisplayName(toComponent(exitItem.name()))
                .setLore(toComponent(exitItem.lore()))
                .setPersistentDataContainerValue(plugin, "parkour-item", "EXIT")
                .toItemStack();

        player.getInventory().setItem(8, exit);
    }

//    private boolean canJoin(final Player player, final String parkourName) {
//        if (player.hasPermission("parkour.bypass")) return true;
//        if (plugin.getParkourDataManager().getCooldown(parkourName) <= 0L) return true;
//
//        long systemTime = System.currentTimeMillis();
//        long playerTime = plugin.getParkourCooldownManager().getCooldown(player.getUniqueId(), parkourName);
//        if (systemTime < playerTime) {
//            player.sendMessage(colorize(plugin.getMessages().waitBeforeJoin(parkourName, playerTime - systemTime)));
//            return false;
//        }
//        return true;
//    }
}
