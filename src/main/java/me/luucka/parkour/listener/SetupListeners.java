package me.luucka.parkour.listener;

import me.luucka.extendlibrary.message.Message;
import me.luucka.extendlibrary.util.MaterialUtil;
import me.luucka.papergui.buttons.PGButton;
import me.luucka.papergui.menu.PGMenu;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.manager.SetupManager;
import me.luucka.parkour.model.Checkpoint;
import me.luucka.parkour.model.SetupParkour;
import me.luucka.parkour.setting.Items;
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

import static me.luucka.extendlibrary.util.MMColor.toLegacy;
import static me.luucka.extendlibrary.util.MMColor.toMMString;

public class SetupListeners implements Listener {

    private final ParkourPlugin plugin;
    private final SetupManager setupManager;
    private final Message messages;
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
                    messages.from("setup-set-start-loc")
                            .with("parkour", parkour.getName())
                            .send(player);
                    event.setCancelled(true);
                }
                case "SETEND" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                    final Block targetBlock = event.getClickedBlock();
                    if (targetBlock == null) return;
                    if (!MaterialUtil.isWallSign(targetBlock.getType())) {
                        messages.from("setup-target-wall-sign").send(player);
                        return;
                    }
                    parkour.setEndLocation(targetBlock.getLocation());
                    messages.from("setup-set-end-loc")
                            .with("parkour", parkour.getName())
                            .send(player);
                    event.setCancelled(true);
                }
                case "WAND" -> {
                    final Block targetBlock;
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        targetBlock = event.getClickedBlock();
                        if (targetBlock == null) return;
                        parkour.setMinRegion(targetBlock.getLocation());
                        messages.from("setup-set-pos1")
                                .with("parkour", parkour.getName())
                                .send(player);
                        event.setCancelled(true);
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        targetBlock = event.getClickedBlock();
                        if (targetBlock == null) return;
                        parkour.setMaxRegion(event.getClickedBlock().getLocation());
                        messages.from("setup-set-pos2")
                                .with("parkour", parkour.getName())
                                .send(player);
                        event.setCancelled(true);
                    }
                }
                case "SAVE" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    if (!parkour.canSave()) {
                        messages.from("setup-set-all-parameters").send(player);
                        event.setCancelled(true);
                        return;
                    }
                    parkour.save();
                    setupManager.playerQuit(player);
                    messages.from("setup-save")
                            .with("parkour", parkour.getName())
                            .send(player);
                    event.setCancelled(true);
                }
                case "CANCEL" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    parkour.cancel();
                    setupManager.playerQuit(player);
                    messages.from("setup-cancel")
                            .with("parkour", parkour.getName())
                            .send(player);
                    event.setCancelled(true);
                }
                case "MORE-OPTIONS" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

                    PGMenu menu = ParkourPlugin.paperGUI.create(
                            toMMString(messages.from("setup-more-options-menu-title").with("parkour", parkour.getName()).build()),
                            3
                    );
                    menu.setEnableAutomaticPagination(false);

                    PGButton completeCommandsButton = new PGButton(items.getCompleteCommands())
                            .withListener((InventoryClickEvent clickEvent) -> {
                                if (clickEvent.getClick() == ClickType.LEFT) {
                                    List<String> completeCommands = setupManager.getSetupParkourByPlayer(player).getCompleteCommands();
                                    String text = !completeCommands.isEmpty() ? String.join(";", completeCommands) : "...";
                                    new AnvilGUI.Builder()
                                            .title(toLegacy(messages.from("setup-complete-commands-gui-title").build()))
                                            .text(text)
                                            .itemLeft(new ItemStack(items.getCompleteCommands().getType()))
                                            .onClick((slot, stateSnapshot) -> {
                                                if (slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();
                                                if (stateSnapshot.getOutputItem().getType() == Material.AIR)
                                                    return Collections.emptyList();
                                                parkour.addConsoleCommands(stateSnapshot.getText());
                                                messages.from("setup-add-complete-commands")
                                                        .with("parkour", parkour.getName())
                                                        .send(stateSnapshot.getPlayer());
                                                return Collections.singletonList(AnvilGUI.ResponseAction.openInventory(menu.getInventory()));
                                            })
                                            .onClose(stateSnapshot -> Bukkit.getScheduler().runTask(plugin, () -> stateSnapshot.getPlayer().openInventory(menu.getInventory())))
                                            .plugin(plugin)
                                            .open(player);
                                } else if (clickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                                    parkour.clearConsoleCommands();
                                    messages.from("setup-clear-complete-commands")
                                            .with("parkour", parkour.getName())
                                            .send(player);
                                }
                            });
                    menu.setButton(10, completeCommandsButton);

                    PGButton cooldownButton = new PGButton(items.getCooldownItem())
                            .withListener((InventoryClickEvent clickEvent) -> {
                                if (clickEvent.getClick() == ClickType.LEFT) {
                                    long textCooldown = setupManager.getSetupParkourByPlayer(player).getCooldown();
                                    String text = textCooldown >= 1L ? String.valueOf(textCooldown) : "...";
                                    new AnvilGUI.Builder()
                                            .title(toLegacy(messages.from("setup-set-cooldown-gui-title").build()))
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
                                                    return List.of(AnvilGUI.ResponseAction.replaceInputText(
                                                            toLegacy(messages.from("setup-insert-valid-cooldown-value").build())
                                                    ));
                                                }
                                                parkour.setCooldown(cooldown);
                                                messages.from("setup-set-cooldown")
                                                        .with("parkour", parkour.getName())
                                                        .send(stateSnapshot.getPlayer());
                                                return List.of(AnvilGUI.ResponseAction.openInventory(menu.getInventory()));
                                            })
                                            .onClose(stateSnapshot -> Bukkit.getScheduler().runTask(plugin, () -> stateSnapshot.getPlayer().openInventory(menu.getInventory())))
                                            .plugin(plugin)
                                            .open(player);
                                } else if (clickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                                    parkour.setCooldown(-1L);
                                    messages.from("setup-reset-cooldown")
                                            .with("parkour", parkour.getName())
                                            .send(player);
                                }
                            });
                    menu.setButton(12, cooldownButton);

                    player.openInventory(menu.getInventory());
                }
                case "CHECKPOINT" -> {
                    if (event.getAction().isRightClick()) {
                        if (!player.isSneaking()) {
                            parkour.addCheckpoint(new Checkpoint(parkour.getCheckpoints().size(), player.getLocation(), player.getLocation().getBlock().getLocation()));
                            messages.from("setup-set-new-checkpoint")
                                    .with("parkour", parkour.getName())
                                    .withNumber("number", parkour.getCheckpoints().size())
                                    .send(player);
                        } else {
                            PGMenu menu = ParkourPlugin.paperGUI.create(
                                    toMMString(messages.from("setup-checkpoint-menu-title").with("parkour", parkour.getName()).build()),
                                    3
                            );
                            menu.setEnableAutomaticPagination(true);

                            LinkedHashSet<Checkpoint> sortCheckpoints = parkour.getCheckpoints().stream().sorted(Comparator.comparing(Checkpoint::getNumber)).collect(Collectors.toCollection(LinkedHashSet::new));
                            for (Checkpoint checkpoint : sortCheckpoints) {

                                PGButton button = new PGButton(items.getCheckpointListItem(checkpoint.getNumber() + 1, checkpoint.getBlockLocation()))
                                        .withListener((InventoryClickEvent clickEvent) -> {
                                            if (clickEvent.getClick() == ClickType.LEFT) {
                                                // tp
                                                player.teleport(checkpoint.getTpLocation());
                                                messages.from("setup-tp-to-checkpoint")
                                                        .with("parkour", parkour.getName())
                                                        .withNumber("number", checkpoint.getNumber() + 1)
                                                        .send(player);
                                            } else if (clickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                                                // remove
                                                parkour.removeCheckpoint(checkpoint);
                                                parkour.getCheckpoints().forEach(checkp -> {
                                                    if (checkp.getNumber() > checkpoint.getNumber()) {
                                                        checkp.setNumber(checkp.getNumber() - 1);
                                                    }
                                                });
                                                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                                                messages.from("setup-remove-checkpoint")
                                                        .with("parkour", parkour.getName())
                                                        .withNumber("number", checkpoint.getNumber() + 1)
                                                        .send(player);
                                            } else if (clickEvent.getClick() == ClickType.MIDDLE) {
                                                // update
                                                checkpoint.updateLocation(player.getLocation(), player.getLocation().getBlock().getLocation());
                                                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                                                messages.from("setup-update-checkpoint")
                                                        .with("parkour", parkour.getName())
                                                        .withNumber("number", checkpoint.getNumber() + 1)
                                                        .send(player);
                                            }
                                            clickEvent.setCancelled(true);
                                        });
                                menu.addButton(button);
                            }
                            player.openInventory(menu.getInventory());
                        }
                    } else if (event.getAction().isLeftClick() && player.isSneaking()) {
                        parkour.resetAllCheckpoints();
                        messages.from("setup-reset-all-checkpoints")
                                .with("parkour", parkour.getName())
                                .send(player);
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
        messages.from("setup-cancel").with("parkour", parkour.getName()).send(player);
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
