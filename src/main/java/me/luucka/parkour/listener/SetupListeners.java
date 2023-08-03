package me.luucka.parkour.listener;

import me.luucka.papergui.buttons.PGButton;
import me.luucka.papergui.menu.PGMenu;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entity.Checkpoint;
import me.luucka.parkour.entity.SetupParkour;
import me.luucka.parkour.manager.SetupManager;
import me.luucka.parkour.setting.Items;
import me.luucka.parkour.setting.Messages;
import me.luucka.parkour.util.MaterialUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static me.luucka.parkour.util.MMColor.toComponent;
import static me.luucka.parkour.util.MMColor.toLegacy;

public class SetupListeners implements Listener {

    private final ParkourPlugin plugin;
    private final SetupManager setupManager;
    private final Messages messages;
    private final Items items;

    public SetupListeners(final ParkourPlugin plugin) {
        this.plugin = plugin;
        this.setupManager = plugin.getSetupManager();
        this.messages = plugin.getMessages();
        this.items = plugin.getItems();
    }

    @EventHandler
    public void playerUseSetupItems(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!setupManager.isPlayerInSetup(player)) return;
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
                    player.sendRichMessage(messages.setupSetStartLoc(parkour.getName()));
                    event.setCancelled(true);
                }
                case "SETEND" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                    final Block targetBlock = event.getClickedBlock();
                    if (targetBlock == null) return;
                    if (!MaterialUtil.isWallSign(targetBlock.getType())) {
                        player.sendRichMessage(messages.setupTargetWallSign());
                        return;
                    }
                    parkour.setEndLocation(targetBlock.getLocation());
                    player.sendRichMessage(messages.setupSetEndLoc(parkour.getName()));
                    event.setCancelled(true);
                }
                case "WAND" -> {
                    final Block targetBlock;
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        targetBlock = event.getClickedBlock();
                        if (targetBlock == null) return;
                        parkour.setMinRegion(targetBlock.getLocation());
                        player.sendRichMessage(messages.setupSetPos1(parkour.getName()));
                        event.setCancelled(true);
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        targetBlock = event.getClickedBlock();
                        if (targetBlock == null) return;
                        parkour.setMaxRegion(event.getClickedBlock().getLocation());
                        player.sendRichMessage(messages.setupSetPos2(parkour.getName()));
                        event.setCancelled(true);
                    }
                }
                case "SAVE" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    if (!parkour.canSave()) {
                        player.sendRichMessage(messages.setupSetAllParameters());
                        event.setCancelled(true);
                        return;
                    }
                    parkour.save();
                    setupManager.playerQuit(player);
                    player.sendRichMessage(messages.setupSave(parkour.getName()));
                    event.setCancelled(true);
                }
                case "CANCEL" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    parkour.cancel();
                    setupManager.playerQuit(player);
                    player.sendRichMessage(messages.setupCancel(parkour.getName()));
                    event.setCancelled(true);
                }
                case "MORE-OPTIONS" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

                    PGMenu menu = ParkourPlugin.paperGUI.create(messages.moreOptionsMenuTitle(parkour.getName()), 3);
                    menu.setEnableAutomaticPagination(false);

                    PGButton completeCommandsButton = new PGButton(items.getCompleteCommands())
                            .withListener((InventoryClickEvent clickEvent) -> {
                                if (clickEvent.getClick() == ClickType.LEFT) {
                                    List<String> completeCommands = setupManager.getSetupParkourByPlayer(player).getCompleteCommands();
                                    String text = !completeCommands.isEmpty() ? String.join(";", completeCommands) : "...";
                                    new AnvilGUI.Builder()
                                            .title(toLegacy(toComponent(messages.completeCommandsGuiTitle())))
                                            .text(text)
                                            .itemLeft(new ItemStack(items.getCompleteCommands().getType()))
                                            .onClick((slot, stateSnapshot) -> {
                                                if (slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();
                                                if (stateSnapshot.getOutputItem().getType() == Material.AIR)
                                                    return Collections.emptyList();
                                                parkour.addConsoleCommands(stateSnapshot.getText());
                                                stateSnapshot.getPlayer().sendRichMessage(messages.setupAddCompleteCommands(parkour.getName()));
                                                return Collections.singletonList(AnvilGUI.ResponseAction.openInventory(menu.getInventory()));
                                            })
                                            .onClose(stateSnapshot -> Bukkit.getScheduler().runTask(plugin, () -> stateSnapshot.getPlayer().openInventory(menu.getInventory())))
                                            .plugin(plugin)
                                            .open(player);
                                } else if (clickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                                    parkour.clearConsoleCommands();
                                    player.sendRichMessage(messages.setupClearCompleteCommands(parkour.getName()));
                                }
                            });
                    menu.setButton(10, completeCommandsButton);

                    PGButton cooldownButton = new PGButton(items.getCooldownItem())
                            .withListener((InventoryClickEvent clickEvent) -> {
                                if (clickEvent.getClick() == ClickType.LEFT) {
                                    long textCooldown = setupManager.getSetupParkourByPlayer(player).getCooldown();
                                    String text = textCooldown >= 1L ? String.valueOf(textCooldown) : "...";
                                    new AnvilGUI.Builder()
                                            .title(toLegacy(toComponent(messages.setCooldownGuiTitle())))
                                            .text(text)
                                            .itemLeft(new ItemStack(items.getCooldownItem().getType()))
                                            .onClick((slot, stateSnapshot) -> {
                                                if (slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();
                                                if (stateSnapshot.getOutputItem().getType() == Material.AIR)
                                                    return Collections.emptyList();

                                                long cooldown;
                                                try {
                                                    cooldown = Long.parseLong(stateSnapshot.getText());
                                                } catch (final NumberFormatException e) {
                                                    return List.of(AnvilGUI.ResponseAction.replaceInputText(toLegacy(toComponent(messages.setupInserValidCooldown()))));
                                                }
                                                parkour.setCooldown(cooldown);
                                                stateSnapshot.getPlayer().sendRichMessage(messages.setupSetCooldown(parkour.getName()));
                                                return List.of(AnvilGUI.ResponseAction.openInventory(menu.getInventory()));
                                            })
                                            .onClose(stateSnapshot -> Bukkit.getScheduler().runTask(plugin, () -> stateSnapshot.getPlayer().openInventory(menu.getInventory())))
                                            .plugin(plugin)
                                            .open(player);
                                } else if (clickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                                    parkour.setCooldown(-1L);
                                    player.sendRichMessage(messages.setupResetCooldown(parkour.getName()));
                                }
                            });
                    menu.setButton(12, cooldownButton);

                    player.openInventory(menu.getInventory());
                }
                case "CHECKPOINT" -> {
                    if (event.getAction().isRightClick()) {
                        if (!player.isSneaking()) {
                            parkour.addCheckpoint(new Checkpoint(parkour.getCheckpoints().size(), player.getLocation(), player.getLocation().getBlock().getLocation()));
                            player.sendRichMessage(messages.getSetNewCheckpoint(parkour.getName(), parkour.getCheckpoints().size()));
                        } else {
                            PGMenu menu = ParkourPlugin.paperGUI.create(messages.getCheckpointMenuTitle(parkour.getName()), 3);
                            menu.setEnableAutomaticPagination(true);

                            LinkedHashSet<Checkpoint> sortCheckpoints = parkour.getCheckpoints().stream().sorted(Comparator.comparing(Checkpoint::getNumber)).collect(Collectors.toCollection(LinkedHashSet::new));
                            for (Checkpoint checkpoint : sortCheckpoints) {

                                PGButton button = new PGButton(items.getCheckpointListItem(checkpoint.getNumber() + 1, checkpoint.getBlockLocation()))
                                        .withListener((InventoryClickEvent clickEvent) -> {
                                            if (clickEvent.getClick() == ClickType.LEFT) {
                                                // tp
                                                player.teleport(checkpoint.getTpLocation());
                                                player.sendRichMessage(messages.getTpToCheckpoint(parkour.getName(), checkpoint.getNumber() + 1));
                                            } else if (clickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                                                // remove
                                                parkour.removeCheckpoint(checkpoint);
                                                parkour.getCheckpoints().forEach(checkp -> {
                                                    if (checkp.getNumber() > checkpoint.getNumber()) {
                                                        checkp.setNumber(checkp.getNumber() - 1);
                                                    }
                                                });
                                                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                                                player.sendRichMessage(messages.getRemoveCheckpoint(parkour.getName(), checkpoint.getNumber() + 1));
                                            } else if (clickEvent.getClick() == ClickType.MIDDLE) {
                                                // update
                                                checkpoint.updateLocation(player.getLocation(), player.getLocation().getBlock().getLocation());
                                                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                                                player.sendRichMessage(messages.getUpdateCheckpoint(parkour.getName(), checkpoint.getNumber() + 1));
                                            }
                                            clickEvent.setCancelled(true);
                                        });
                                menu.addButton(button);
                            }
                            player.openInventory(menu.getInventory());
                        }
                    } else if (event.getAction().isLeftClick() && player.isSneaking()) {
                        parkour.resetAllCheckpoints();
                        player.sendRichMessage(messages.getResetAllCheckpoints(parkour.getName()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        if (!setupManager.isPlayerInSetup(player)) return;
        final SetupParkour parkour = setupManager.getSetupParkourByPlayer(player);
        parkour.cancel();
        setupManager.playerQuit(player);
        player.sendRichMessage(messages.setupCancel(parkour.getName()));
    }

    @EventHandler
    public void onServerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (!setupManager.isPlayerInSetup(player)) return;
        setupManager.getSetupParkourByPlayer(player).cancel();
        setupManager.playerQuit(event.getPlayer());
    }

    @EventHandler
    public void onKickPlayer(final PlayerKickEvent event) {
        final Player player = event.getPlayer();
        if (!setupManager.isPlayerInSetup(player)) return;
        setupManager.getSetupParkourByPlayer(player).cancel();
        setupManager.playerQuit(event.getPlayer());
    }

}
