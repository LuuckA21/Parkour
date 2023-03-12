package me.luucka.parkour.listeners;

import me.luucka.papergui.buttons.PGButton;
import me.luucka.papergui.menu.PGMenu;
import me.luucka.parkour.Items;
import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entities.SetupParkour;
import me.luucka.parkour.managers.SetupManager;
import me.luucka.parkour.utils.MaterialUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.luucka.parkour.utils.MMColor.toComponent;
import static me.luucka.parkour.utils.MMColor.toLegacy;

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
                    player.sendMessage(toComponent(messages.setupSetStartLoc(parkour.getName())));
                    event.setCancelled(true);
                }
                case "SETEND" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                    final Block targetBlock = event.getClickedBlock();
                    if (targetBlock == null) return;
                    if (!MaterialUtil.isWallSign(targetBlock.getType())) {
                        player.sendMessage(toComponent(messages.setupTargetWallSign()));
                        return;
                    }
                    parkour.setEndLocation(targetBlock.getLocation());
                    player.sendMessage(toComponent(messages.setupSetEndLoc(parkour.getName())));
                    event.setCancelled(true);
                }
                case "WAND" -> {
                    final Block targetBlock;
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        targetBlock = event.getClickedBlock();
                        if (targetBlock == null) return;
                        parkour.setMinRegion(targetBlock.getLocation());
                        player.sendMessage(toComponent(messages.setupSetPos1(parkour.getName())));
                        event.setCancelled(true);
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        targetBlock = event.getClickedBlock();
                        if (targetBlock == null) return;
                        parkour.setMaxRegion(event.getClickedBlock().getLocation());
                        player.sendMessage(toComponent(messages.setupSetPos2(parkour.getName())));
                        event.setCancelled(true);
                    }
                }
                case "SAVE" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    if (!parkour.canSave()) {
                        player.sendMessage(toComponent(messages.setupSetAllParameters()));
                        event.setCancelled(true);
                        return;
                    }
                    parkour.save();
                    setupManager.playerQuit(player);
                    player.sendMessage(toComponent(messages.setupSave(parkour.getName())));
                    event.setCancelled(true);
                }
                case "CANCEL" -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
                    parkour.cancel();
                    setupManager.playerQuit(player);
                    player.sendMessage(toComponent(messages.setupCancel(parkour.getName())));
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
                                    String text = completeCommands.size() >= 1 ? String.join(";", completeCommands) : "...";
                                    new AnvilGUI.Builder()
                                            .title(toLegacy(toComponent(messages.completeCommandsGuiTitle())))
                                            .text(text)
                                            .itemLeft(new ItemStack(items.getCompleteCommands().getType()))
                                            .onComplete(completion -> {
                                                parkour.addConsoleCommands(completion.getText());
                                                player.sendMessage(toComponent(messages.setupAddCompleteCommands(parkour.getName())));
                                                return List.of(AnvilGUI.ResponseAction.openInventory(menu.getInventory()));
                                            })
                                            .onClose(anvilPlayer -> Bukkit.getScheduler().runTask(plugin, () -> anvilPlayer.openInventory(menu.getInventory())))
                                            .plugin(plugin)
                                            .open(player);
                                } else if (clickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                                    parkour.clearConsoleCommands();
                                    player.sendMessage(toComponent(messages.setupClearCompleteCommands(parkour.getName())));
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
                                            .onComplete(completion -> {
                                                long cooldown = -1;
                                                try {
                                                    cooldown = Long.parseLong(completion.getText());
                                                } catch (final NumberFormatException e) {
                                                    return List.of(AnvilGUI.ResponseAction.replaceInputText(toLegacy(toComponent(messages.setupInserValidCooldown()))));
                                                }
                                                parkour.setCooldown(cooldown);
                                                player.sendMessage(toComponent(messages.setupSetCooldown(parkour.getName())));
                                                return List.of(AnvilGUI.ResponseAction.openInventory(menu.getInventory()));
                                            })
                                            .onClose(anvilPlayer -> Bukkit.getScheduler().runTask(plugin, () -> anvilPlayer.openInventory(menu.getInventory())))
                                            .plugin(plugin)
                                            .open(player);
                                }
                            });
                    menu.setButton(12, cooldownButton);

                    player.openInventory(menu.getInventory());
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
        player.sendMessage(toComponent(messages.setupCancel(parkour.getName())));
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
