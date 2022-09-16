package me.luucka.parkour.listeners;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entities.Cuboid;
import me.luucka.parkour.utils.MaterialUtil;
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

import static me.luucka.parkour.utils.Color.colorize;

public class ParkourListeners implements Listener {

    private final ParkourPlugin plugin;

    public ParkourListeners(ParkourPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getParkourGameManager().isPlayerInParkour(player)) return;

        final String parkourName = plugin.getParkourGameManager().getParkourNamePlayerIsIn(player);
        Cuboid cuboid = plugin.getParkourDataManager().getCuboid(parkourName);
        if (!cuboid.contains(event.getTo())) {
            player.teleport(plugin.getParkourDataManager().getStartLocation(parkourName));
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (plugin.getParkourGameManager().isPlayerInParkour(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderPearlThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (plugin.getParkourGameManager().isPlayerInParkour(player)) {
            if (event.getEntityType() == EntityType.ENDER_PEARL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onParkourEnd(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getParkourGameManager().isPlayerInParkour(player)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!MaterialUtil.isWallSign(event.getClickedBlock().getType())) return;

        final String parkourName = plugin.getParkourGameManager().getParkourNamePlayerIsIn(player);
        if (plugin.getParkourDataManager().getEndLocation(parkourName).equals(event.getClickedBlock().getLocation())) {
            plugin.getParkourGameManager().playerQuit(event.getPlayer(), true);
        }
    }

    @EventHandler
    public void onServerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getParkourGameManager().isPlayerInParkour(player)) {
            plugin.getParkourGameManager().playerQuit(player, false);
        }
    }

    @EventHandler
    public void onFlyChange(PlayerToggleFlightEvent event) {
        if (!plugin.getParkourGameManager().isPlayerInParkour(event.getPlayer())) return;

        if (event.isFlying()) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getParkourGameManager().isPlayerInParkour(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getParkourGameManager().isPlayerInParkour(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.hasItem()) return;

        final ItemStack item = event.getItem();

        NamespacedKey key = new NamespacedKey(plugin, "parkour-item");
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            String sKey = container.get(key, PersistentDataType.STRING);

            final String parkourName = plugin.getParkourGameManager().getParkourNamePlayerIsIn(player);

            if (sKey.equalsIgnoreCase("EXIT")) {
                if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                plugin.getParkourGameManager().playerQuit(player, false);
                event.setCancelled(true);
            }
        }
    }

}
