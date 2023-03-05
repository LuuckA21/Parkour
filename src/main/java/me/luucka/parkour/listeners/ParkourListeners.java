package me.luucka.parkour.listeners;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entities.Parkour;
import me.luucka.parkour.managers.GameManager;
import me.luucka.parkour.utils.MaterialUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
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
    private final GameManager gameManager;

    public ParkourListeners(final ParkourPlugin plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (!gameManager.isPlayerInGame(player)) return;

        final Parkour parkour = gameManager.getParkourByPlayer(player);
        if (!parkour.getRegion().contains(event.getTo())) {
            player.teleport(parkour.getStartLocation());
        }
    }

    @EventHandler
    public void onBowShoot(final EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof final Player player)) return;
        if (gameManager.isPlayerInGame(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderPearlThrow(final ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof final Player player)) return;
        if (gameManager.isPlayerInGame(player)) {
            if (event.getEntityType() == EntityType.ENDER_PEARL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onParkourEnd(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!gameManager.isPlayerInGame(player)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        final Block targetBlock = event.getClickedBlock();
        if (targetBlock == null) return;
        if (!MaterialUtil.isWallSign(targetBlock.getType())) return;
        final Parkour parkour = gameManager.getParkourByPlayer(player);
        if (parkour.getEndLocation().equals(targetBlock.getLocation())) {
            gameManager.playerQuit(event.getPlayer(), true);
        }
    }

    @EventHandler
    public void onServerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (gameManager.isPlayerInGame(player)) {
            gameManager.playerQuit(player, false);
        }
    }

    @EventHandler
    public void onFlyChange(final PlayerToggleFlightEvent event) {
        if (!gameManager.isPlayerInGame(event.getPlayer())) return;
        if (event.isFlying()) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof final Player player)) return;
        if (!gameManager.isPlayerInGame(player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!gameManager.isPlayerInGame(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.hasItem()) return;

        final ItemStack item = event.getItem();
        if (item == null) return;

        final NamespacedKey key = new NamespacedKey(plugin, "parkour-item");
        final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            final String sKey = container.get(key, PersistentDataType.STRING);
            if (sKey == null) return;
            if (sKey.equalsIgnoreCase("EXIT")) {
                if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                gameManager.playerQuit(player, false);
                event.setCancelled(true);
            }
        }
    }

}
