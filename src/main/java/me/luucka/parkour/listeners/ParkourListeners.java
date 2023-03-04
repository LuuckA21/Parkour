package me.luucka.parkour.listeners;

import me.luucka.helplib.utils.MaterialUtil;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entities.Parkour;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ParkourListeners implements Listener {

    private final ParkourPlugin plugin;

    public ParkourListeners(final ParkourPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getGameManager().isPlayerInParkourGame(player)) return;

        final Parkour parkour = plugin.getGameManager().getPlayerParkour(player);
        if (!parkour.getRegion().contains(event.getTo())) {
            player.teleport(parkour.getStartLocation());
        }
    }

    @EventHandler
    public void onBowShoot(final EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof final Player player)) return;
        if (plugin.getGameManager().isPlayerInParkourGame(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderPearlThrow(final ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof final Player player)) return;
        if (plugin.getGameManager().isPlayerInParkourGame(player)) {
            if (event.getEntityType() == EntityType.ENDER_PEARL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onParkourEnd(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getGameManager().isPlayerInParkourGame(player)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!MaterialUtil.isWallSign(event.getClickedBlock().getType())) return;

        final Parkour parkour = plugin.getGameManager().getPlayerParkour(player);
        if (parkour.getEndLocation().equals(event.getClickedBlock().getLocation())) {
            plugin.getGameManager().playerQuit(event.getPlayer(), true);
        }
    }

    @EventHandler
    public void onServerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getGameManager().isPlayerInParkourGame(player)) {
            plugin.getGameManager().playerQuit(player, false);
        }
    }

    @EventHandler
    public void onFlyChange(final PlayerToggleFlightEvent event) {
        if (!plugin.getGameManager().isPlayerInParkourGame(event.getPlayer())) return;

        if (event.isFlying()) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof final Player player)) return;
        if (!plugin.getGameManager().isPlayerInParkourGame(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getGameManager().isPlayerInParkourGame(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.hasItem()) return;

        final ItemStack item = event.getItem();

        final NamespacedKey key = new NamespacedKey(plugin, "parkour-item");
        final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            final String sKey = container.get(key, PersistentDataType.STRING);

            if (sKey.equalsIgnoreCase("EXIT")) {
                if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                plugin.getGameManager().playerQuit(player, false);
                event.setCancelled(true);
            }
        }
    }

}
