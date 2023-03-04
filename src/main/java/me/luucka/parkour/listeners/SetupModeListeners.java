package me.luucka.parkour.listeners;

import me.luucka.helplib.item.ItemBuilder;
import me.luucka.helplib.utils.MaterialUtil;
import me.luucka.parkour.ParkourPlugin;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.luucka.helplib.color.MMColor.toComponent;

public class SetupModeListeners implements Listener {

    private final ParkourPlugin plugin;

    public SetupModeListeners(final ParkourPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getSetupManager().isPlayerInSetupMode(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.hasItem()) return;

        final ItemStack item = event.getItem();
        if (item == null) return;

        NamespacedKey key = new NamespacedKey(plugin, "setup-item");
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            final String sKey = container.get(key, PersistentDataType.STRING);
            if (sKey == null) return;

            final String parkourName = plugin.getSetupManager().getParkour(player).getName();


            switch (sKey) {
                case "SETSTART" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    plugin.getSetupManager().getParkour(player).setStartLocation(player.getLocation());
                    player.sendMessage(toComponent(plugin.getMessages().setStart(parkourName)));
                    event.setCancelled(true);
                }
                case "SETEND" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                    final Block targetBlock = event.getClickedBlock();
                    if (targetBlock == null) return;
                    if (!MaterialUtil.isWallSign(targetBlock.getType())) {
                        player.sendMessage(toComponent(plugin.getMessages().targetWallSign()));
                        return;
                    }
                    plugin.getSetupManager().getParkour(player).setEndLocation(targetBlock.getLocation());
                    player.sendMessage(toComponent(plugin.getMessages().setEnd(parkourName)));
                    event.setCancelled(true);
                }
                case "WAND" -> {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        plugin.getSetupManager().getParkour(player).setMinRegion(event.getClickedBlock().getLocation());
                        player.sendMessage(toComponent(plugin.getMessages().setPos1(parkourName)));
                        event.setCancelled(true);
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        plugin.getSetupManager().getParkour(player).setMaxRegion(event.getClickedBlock().getLocation());
                        player.sendMessage(toComponent(plugin.getMessages().setPos2(parkourName)));
                        event.setCancelled(true);
                    }
                }
                case "SAVE" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    if (!plugin.getSetupManager().getParkour(player).canSave()) {
                        player.sendMessage(toComponent(plugin.getMessages().setAllParameters()));
                        event.setCancelled(true);
                        return;
                    }
                    plugin.getSetupManager().getParkour(player).save();
                    plugin.getSetupManager().removePlayerFromSetupMode(player);
                    player.sendMessage(toComponent(plugin.getMessages().save(parkourName)));
                    event.setCancelled(true);
                }
                case "CANCEL" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    plugin.getSetupManager().getParkour(player).cancel();
                    plugin.getSetupManager().removePlayerFromSetupMode(player);
                    player.sendMessage(toComponent(plugin.getMessages().cancel(parkourName)));
                    event.setCancelled(true);
                }
                case "PLAYER-CMD" -> {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        new AnvilGUI.Builder()
                                .title(LegacyComponentSerializer.legacySection().serialize(toComponent(plugin.getMessages().waitingInput())))
                                .text("...")
                                .itemLeft(new ItemBuilder(Material.PAPER).setDisplayName(toComponent("Set Players Commands")).toItemStack())
                                .onComplete(completion -> {
                                    plugin.getSetupManager().getParkour(player).addPlayerCommands(completion.getText());
                                    player.sendMessage(toComponent(plugin.getMessages().addedPlayerCommands(parkourName)));
                                    return List.of(AnvilGUI.ResponseAction.close());
                                })
                                .plugin(plugin)
                                .open(player);
                    }
                    if (player.isSneaking() && event.getAction() == Action.LEFT_CLICK_AIR) {
                        plugin.getSetupManager().getParkour(player).clearPlayerCommands();
                        player.sendMessage(toComponent(plugin.getMessages().clearPlayerCommands(parkourName)));
                    }
                    event.setCancelled(true);
                }
                case "CONSOLE-CMD" -> {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        new AnvilGUI.Builder()
                                .title(LegacyComponentSerializer.legacySection().serialize(toComponent(plugin.getMessages().waitingInput())))
                                .text("...")
                                .itemLeft(new ItemBuilder(Material.MAP).setDisplayName(toComponent("Set Console Commands")).toItemStack())
                                .onComplete(completion -> {
                                    plugin.getSetupManager().getParkour(player).addConsoleCommands(completion.getText());
                                    player.sendMessage(toComponent(plugin.getMessages().addedConsoleCommands(parkourName)));
                                    return List.of(AnvilGUI.ResponseAction.close());
                                })
                                .plugin(plugin)
                                .open(player);
                    }
                    if (player.isSneaking() && event.getAction() == Action.LEFT_CLICK_AIR) {
                        plugin.getSetupManager().getParkour(player).clearConsoleCommands();
                        player.sendMessage(toComponent(plugin.getMessages().clearConsoleCommands(parkourName)));
                    }
                    event.setCancelled(true);
                }
                case "COOLDOWN" -> {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        new AnvilGUI.Builder()
                                .title(LegacyComponentSerializer.legacySection().serialize(toComponent(plugin.getMessages().waitingCooldownInput())))
                                .text("...")
                                .itemLeft(new ItemBuilder(Material.CLOCK).setDisplayName(toComponent("Set Cooldown")).toItemStack())
                                .onComplete(completion -> {
                                    int cooldown = 0;
                                    try {
                                        cooldown = Integer.parseInt(completion.getText());
                                    } catch (final NumberFormatException e) {
                                        return List.of(AnvilGUI.ResponseAction.replaceInputText("Please insert a integer value"));
                                    }
                                    plugin.getSetupManager().getParkour(player).setCooldown(cooldown);
                                    player.sendMessage(toComponent(plugin.getMessages().addedCooldown(parkourName)));
                                    return List.of(AnvilGUI.ResponseAction.close());
                                })
                                .plugin(plugin)
                                .open(player);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(final PlayerChangedWorldEvent event) {
        if (!plugin.getSetupManager().isPlayerInSetupMode(event.getPlayer())) return;
        plugin.getSetupManager().getParkour(event.getPlayer()).cancel();
        plugin.getSetupManager().removePlayerFromSetupMode(event.getPlayer());
        event.getPlayer().sendMessage(toComponent(plugin.getMessages().cancel(plugin.getSetupManager().getParkour(event.getPlayer()).getName())));
    }

    @EventHandler
    public void onServerQuit(final PlayerQuitEvent event) {
        if (!plugin.getSetupManager().isPlayerInSetupMode(event.getPlayer())) return;
        plugin.getSetupManager().getParkour(event.getPlayer()).cancel();
        plugin.getSetupManager().removePlayerFromSetupMode(event.getPlayer());
    }

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent event) {
        if (!plugin.getSetupManager().isPlayerInSetupMode(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onSwapHandItem(final PlayerSwapHandItemsEvent event) {
        if (!plugin.getSetupManager().isPlayerInSetupMode(event.getPlayer())) return;
        event.setCancelled(true);
    }

}
