package me.luucka.parkour.listeners;

import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.Settings;
import me.luucka.parkour.entities.SetupParkour;
import me.luucka.parkour.managers.SetupManager;
import me.luucka.parkour.utils.ItemBuilder;
import me.luucka.parkour.utils.MaterialUtil;
import net.wesjd.anvilgui.AnvilGUI;
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

import static me.luucka.parkour.utils.MMColor.toComponent;
import static me.luucka.parkour.utils.MMColor.toLegacy;

public class SetupModeListeners implements Listener {

    private final ParkourPlugin plugin;
    private final SetupManager setupManager;
    private final Messages messages;
    private final Settings settings;

    public SetupModeListeners(final ParkourPlugin plugin) {
        this.plugin = plugin;
        this.setupManager = plugin.getSetupManager();
        this.messages = plugin.getMessages();
        this.settings = plugin.getSettings();
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!setupManager.isPlayerInSetupMode(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.hasItem()) return;

        final ItemStack item = event.getItem();
        if (item == null) return;

        NamespacedKey key = new NamespacedKey(plugin, "setup-item");
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            final String sKey = container.get(key, PersistentDataType.STRING);
            if (sKey == null) return;

            final SetupParkour parkour = setupManager.getSetupParkourByPlayer(player);

            switch (sKey) {
                case "SETSTART" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    parkour.setStartLocation(player.getLocation());
                    player.sendMessage(toComponent(messages.setStart(parkour.getName())));
                    event.setCancelled(true);
                }
                case "SETEND" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                    final Block targetBlock = event.getClickedBlock();
                    if (targetBlock == null) return;
                    if (!MaterialUtil.isWallSign(targetBlock.getType())) {
                        player.sendMessage(toComponent(messages.targetWallSign()));
                        return;
                    }
                    parkour.setEndLocation(targetBlock.getLocation());
                    player.sendMessage(toComponent(messages.setEnd(parkour.getName())));
                    event.setCancelled(true);
                }
                case "WAND" -> {
                    final Block targetBlock;
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        targetBlock = event.getClickedBlock();
                        if (targetBlock == null) return;
                        parkour.setMinRegion(targetBlock.getLocation());
                        player.sendMessage(toComponent(messages.setPos1(parkour.getName())));
                        event.setCancelled(true);
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        targetBlock = event.getClickedBlock();
                        if (targetBlock == null) return;
                        parkour.setMaxRegion(event.getClickedBlock().getLocation());
                        player.sendMessage(toComponent(messages.setPos2(parkour.getName())));
                        event.setCancelled(true);
                    }
                }
                case "PLAYER-CMD" -> {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        new AnvilGUI.Builder()
                                .title(toLegacy(toComponent(messages.waitingInput())))
                                .text("...")
                                .itemLeft(new ItemBuilder(settings.getPlayerCommandsItem().getType()).setDisplayName(settings.getPlayerCommandsItem().displayName()).toItemStack())
                                .onComplete(completion -> {
                                    parkour.addPlayerCommands(completion.getText());
                                    player.sendMessage(toComponent(messages.addedPlayerCommands(parkour.getName())));
                                    return List.of(AnvilGUI.ResponseAction.close());
                                })
                                .plugin(plugin)
                                .open(player);
                    }
                    if (player.isSneaking() && event.getAction() == Action.LEFT_CLICK_AIR) {
                        parkour.clearPlayerCommands();
                        player.sendMessage(toComponent(messages.clearPlayerCommands(parkour.getName())));
                    }
                    event.setCancelled(true);
                }
                case "CONSOLE-CMD" -> {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        new AnvilGUI.Builder()
                                .title(toLegacy(toComponent(messages.waitingInput())))
                                .text("...")
                                .itemLeft(new ItemBuilder(settings.getConsoleCommandsItem().getType()).setDisplayName(settings.getConsoleCommandsItem().displayName()).toItemStack())
                                .onComplete(completion -> {
                                    parkour.addConsoleCommands(completion.getText());
                                    player.sendMessage(toComponent(messages.addedConsoleCommands(parkour.getName())));
                                    return List.of(AnvilGUI.ResponseAction.close());
                                })
                                .plugin(plugin)
                                .open(player);
                    }
                    if (player.isSneaking() && event.getAction() == Action.LEFT_CLICK_AIR) {
                        parkour.clearConsoleCommands();
                        player.sendMessage(toComponent(messages.clearConsoleCommands(parkour.getName())));
                    }
                    event.setCancelled(true);
                }
                case "COOLDOWN" -> {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                        new AnvilGUI.Builder()
                                .title(toLegacy(toComponent(messages.waitingCooldownInput())))
                                .text("...")
                                .itemLeft(new ItemBuilder(settings.getCooldownItem().getType()).setDisplayName(settings.getCooldownItem().displayName()).toItemStack())
                                .onComplete(completion -> {
                                    long cooldown = -1;
                                    try {
                                        cooldown = Long.parseLong(completion.getText());
                                    } catch (final NumberFormatException e) {
                                        return List.of(AnvilGUI.ResponseAction.replaceInputText(toLegacy(toComponent(messages.getInsertValidCooldownValue()))));
                                    }
                                    parkour.setCooldown(cooldown);
                                    player.sendMessage(toComponent(messages.addedCooldown(parkour.getName())));
                                    return List.of(AnvilGUI.ResponseAction.close());
                                })
                                .plugin(plugin)
                                .open(player);
                    }
                    event.setCancelled(true);
                }
                case "SAVE" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    if (!parkour.canSave()) {
                        player.sendMessage(toComponent(messages.setAllParameters()));
                        event.setCancelled(true);
                        return;
                    }
                    parkour.save();
                    setupManager.removePlayerFromSetupMode(player);
                    player.sendMessage(toComponent(messages.save(parkour.getName())));
                    event.setCancelled(true);
                }
                case "CANCEL" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    parkour.cancel();
                    setupManager.removePlayerFromSetupMode(player);
                    player.sendMessage(toComponent(messages.cancel(parkour.getName())));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        if (!setupManager.isPlayerInSetupMode(player)) return;
        final SetupParkour parkour = setupManager.getSetupParkourByPlayer(player);
        parkour.cancel();
        setupManager.removePlayerFromSetupMode(player);
        player.sendMessage(toComponent(messages.cancel(parkour.getName())));
    }

    @EventHandler
    public void onServerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (!setupManager.isPlayerInSetupMode(player)) return;
        final SetupParkour parkour = setupManager.getSetupParkourByPlayer(player);
        parkour.cancel();
        setupManager.removePlayerFromSetupMode(event.getPlayer());
    }

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent event) {
        if (!setupManager.isPlayerInSetupMode(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onSwapHandItem(final PlayerSwapHandItemsEvent event) {
        if (!setupManager.isPlayerInSetupMode(event.getPlayer())) return;
        event.setCancelled(true);
    }

}
