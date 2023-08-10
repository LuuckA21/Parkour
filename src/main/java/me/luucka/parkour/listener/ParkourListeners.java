package me.luucka.parkour.listener;

import me.luucka.extendlibrary.util.MaterialUtil;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.manager.GameManager;
import me.luucka.parkour.manager.ParkourSession;
import me.luucka.parkour.model.Checkpoint;
import me.luucka.parkour.model.Parkour;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class ParkourListeners implements Listener {

    private final ParkourPlugin plugin;
    private final GameManager gameManager;

    public ParkourListeners(final ParkourPlugin plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void playerUseExitItems(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!gameManager.isPlayerInParkourSession(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.hasItem()) return;

        final ItemStack item = event.getItem();
        if (item == null) return;

        final NamespacedKey key = new NamespacedKey(plugin, "parkour-item");
        final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            final String sKey = container.get(key, PersistentDataType.STRING);
            if (sKey == null) return;
            if (sKey.equalsIgnoreCase("LEAVE")) {
                if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                gameManager.playerQuit(player, false);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerGetCheckpoint(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!gameManager.isPlayerInParkourSession(player)) return;

        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            ParkourSession session = gameManager.getParkourSessionByPlayer(player);

            Optional<Checkpoint> optionClickedCheckpoint = session.getParkour().getCheckpointByLocation(event.getClickedBlock().getLocation());
            if (optionClickedCheckpoint.isEmpty()) return;
            Checkpoint clickedCheckpoint = optionClickedCheckpoint.get();

            if (session.getCurrentCheckpoint().getNumber() == clickedCheckpoint.getNumber()) return;

            Optional<Checkpoint> optionalNextCheckpoint = session.getNextCheckpoint();
            if (optionalNextCheckpoint.isEmpty()) return;
            Checkpoint nextCheckpoint = optionalNextCheckpoint.get();

            if (!clickedCheckpoint.equals(nextCheckpoint)) return;

            session.setCurrentCheckpoint(nextCheckpoint);
            player.sendRichMessage("You got the checkpoint number: " + (nextCheckpoint.getNumber() + 1));
        }
    }

    @EventHandler
    public void onParkourEnd(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!gameManager.isPlayerInParkourSession(player)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        final Block targetBlock = event.getClickedBlock();
        if (targetBlock == null) return;
        if (!MaterialUtil.isWallSign(targetBlock.getType())) return;
        final Parkour parkour = gameManager.getParkourSessionByPlayer(player).getParkour();
        if (targetBlock.getLocation().equals(parkour.getEndLocation())) {
            gameManager.playerQuit(player, true);
        }
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (!gameManager.isPlayerInParkourSession(player)) return;

        final ParkourSession session = gameManager.getParkourSessionByPlayer(player);
        final Parkour parkour = session.getParkour();
        if (!parkour.getRegion().contains(event.getTo())) {
            session.incrementDeaths();
            player.teleport(session.getCurrentCheckpoint().getTpLocation());
        }
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof final Player player)) return;
        if (!gameManager.isPlayerInParkourSession(player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    @EventHandler
    public void onServerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (gameManager.isPlayerInParkourSession(player)) {
            gameManager.playerQuit(player, false);
        }
    }

    @EventHandler
    public void onKickPlayer(final PlayerKickEvent event) {
        final Player player = event.getPlayer();
        if (gameManager.isPlayerInParkourSession(player)) {
            gameManager.playerQuit(player, false);
        }
    }

    @EventHandler
    public void onChangeWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        if (gameManager.isPlayerInParkourSession(player)) {
            gameManager.playerQuit(player, false);
        }
    }

    @EventHandler
    public void onBowShoot(final EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof final Player player)) return;
        if (!gameManager.isPlayerInParkourSession(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEnderPearlThrow(final ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof final Player player)) return;
        if (gameManager.isPlayerInParkourSession(player)) {
            if (event.getEntityType() == EntityType.ENDER_PEARL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(final PlayerDropItemEvent event) {
        if (!gameManager.isPlayerInParkourSession(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFlyChange(final PlayerToggleFlightEvent event) {
        if (!gameManager.isPlayerInParkourSession(event.getPlayer())) return;
        if (event.isFlying()) event.setCancelled(true);
    }

    @EventHandler
    public void onVelocityChange(final PlayerVelocityEvent event) {
        if (!gameManager.isPlayerInParkourSession(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onGameModeChange(final PlayerGameModeChangeEvent event) {
        if (!gameManager.isPlayerInParkourSession(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onSwapHandItems(final PlayerSwapHandItemsEvent event) {
        if (!gameManager.isPlayerInParkourSession(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!gameManager.isPlayerInParkourSession(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!gameManager.isPlayerInParkourSession(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInvClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!gameManager.isPlayerInParkourSession(player)) return;
        event.setCancelled(true);
    }

}
