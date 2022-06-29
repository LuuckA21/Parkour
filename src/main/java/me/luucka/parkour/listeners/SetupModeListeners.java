package me.luucka.parkour.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.utils.MaterialUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.luucka.parkour.utils.Color.colorize;

public class SetupModeListeners implements Listener {

    private final ParkourPlugin plugin;

    private final Map<UUID, String> waitingChatInput = new HashMap<>();

    public SetupModeListeners(ParkourPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerQuit(PlayerQuitEvent event) {
        if (!plugin.getParkourSetupManager().isInSetupMode(event.getPlayer())) return;
        plugin.getParkourSetupManager().removeFromSetup(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getParkourSetupManager().isInSetupMode(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.hasItem()) return;

        final ItemStack item = event.getItem();

        NamespacedKey key = new NamespacedKey(plugin, "setup-item");
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            String sKey = container.get(key, PersistentDataType.STRING);

            final String parkourName = plugin.getParkourSetupManager().getParkour(player).getParkourName();

            if (sKey.equalsIgnoreCase("SETSTART")) {
                if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                plugin.getParkourSetupManager().getParkour(player).setStartLocation(player.getLocation());
                player.sendMessage(colorize(plugin.getMessages().setStart(parkourName)));
                event.setCancelled(true);


            } else if (sKey.equalsIgnoreCase("SETEND")) {
                if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                Block targetBlock = event.getClickedBlock();
                if (!MaterialUtil.isWallSign(targetBlock.getType())) {
                    player.sendMessage(colorize(plugin.getMessages().targetWallSign()));
                    return;
                }

                plugin.getParkourSetupManager().getParkour(player).setEndLocation(targetBlock.getLocation());
                player.sendMessage(colorize(plugin.getMessages().setEnd(parkourName)));
                event.setCancelled(true);


            } else if (sKey.equalsIgnoreCase("WAND")) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    plugin.getParkourSetupManager().getParkour(player).setRegionOne(event.getClickedBlock().getLocation());
                    player.sendMessage(colorize(plugin.getMessages().setPos1(parkourName)));
                    event.setCancelled(true);
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    plugin.getParkourSetupManager().getParkour(player).setRegionTwo(event.getClickedBlock().getLocation());
                    player.sendMessage(colorize(plugin.getMessages().setPos2(parkourName)));
                    event.setCancelled(true);
                }


            } else if (sKey.equalsIgnoreCase("SAVE")) {
                if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

                if (!plugin.getParkourSetupManager().getParkour(player).canSave()) {
                    player.sendMessage(colorize(plugin.getMessages().setAllParameters()));
                    event.setCancelled(true);
                    return;
                }

                plugin.getParkourSetupManager().getParkour(player).saveToConfig();

                plugin.getParkourSetupManager().removeFromSetup(player);

                player.sendMessage(colorize(plugin.getMessages().save(parkourName)));

                event.setCancelled(true);

            } else if (sKey.equalsIgnoreCase("CANCEL")) {
                if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

                plugin.getParkourSetupManager().removeFromSetup(player);

                player.sendMessage(colorize(plugin.getMessages().cancel(parkourName)));

                event.setCancelled(true);


            } else if (sKey.equalsIgnoreCase("PLAYER-CMD")) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    waitingChatInput.put(player.getUniqueId(), "PLAYER-CMD");
                    player.sendMessage(colorize(plugin.getMessages().waitingInput()));
                }

                if (player.isSneaking() && event.getAction() == Action.LEFT_CLICK_AIR) {
                    plugin.getParkourSetupManager().getParkour(player).clearCompletePlayerCommands();
                    player.sendMessage(colorize(plugin.getMessages().clearPlayerCommands(parkourName)));
                }

                event.setCancelled(true);


            } else if (sKey.equalsIgnoreCase("CONSOLE-CMD")) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    waitingChatInput.put(player.getUniqueId(), "CONSOLE-CMD");
                    player.sendMessage(colorize(plugin.getMessages().waitingInput()));
                }

                if (player.isSneaking() && event.getAction() == Action.LEFT_CLICK_AIR) {
                    plugin.getParkourSetupManager().getParkour(player).clearCompleteConsoleCommands();
                    player.sendMessage(colorize(plugin.getMessages().clearConsoleCommands(parkourName)));
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (!plugin.getParkourSetupManager().isInSetupMode(event.getPlayer())) return;
        plugin.getParkourSetupManager().removeFromSetup(event.getPlayer());
        event.getPlayer().sendMessage(colorize(plugin.getMessages().cancel(plugin.getParkourSetupManager().getParkour(event.getPlayer()).getParkourName())));
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (!plugin.getParkourSetupManager().isInSetupMode(event.getPlayer())) return;
        if (!waitingChatInput.containsKey(event.getPlayer().getUniqueId())) return;

        final String msg = MiniMessage.miniMessage().serialize(event.originalMessage());
        final String parkourName = plugin.getParkourSetupManager().getParkour(event.getPlayer()).getParkourName();

        if (msg.equalsIgnoreCase("cancel")) {
            waitingChatInput.remove(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(colorize(plugin.getMessages().cancelInput()));
            event.setCancelled(true);
            return;
        }

        if (waitingChatInput.get(event.getPlayer().getUniqueId()).equalsIgnoreCase("PLAYER-CMD")) {
            plugin.getParkourSetupManager().getParkour(event.getPlayer()).addPlayerCommands(msg);
            event.getPlayer().sendMessage(colorize(plugin.getMessages().addedPlayerCommands(parkourName)));
        } else if (waitingChatInput.get(event.getPlayer().getUniqueId()).equalsIgnoreCase("CONSOLE-CMD")) {
            plugin.getParkourSetupManager().getParkour(event.getPlayer()).addConsoleCommands(msg);
            event.getPlayer().sendMessage(colorize(plugin.getMessages().addedConsoleCommands(parkourName)));
        }

        event.setCancelled(true);
    }

}
