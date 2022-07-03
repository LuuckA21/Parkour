package me.luucka.parkour.managers;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.config.entities.LazyItem;
import me.luucka.parkour.entities.SetupParkour;
import me.luucka.parkour.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.luucka.parkour.utils.Color.colorize;

public class ParkourSetupManager {

    private final ParkourPlugin plugin;

    private final PlayerRollbackManager rollbackManager;

    private final Map<UUID, SetupParkour> inSetupMode = new HashMap<>();

    public ParkourSetupManager(ParkourPlugin plugin) {
        this.plugin = plugin;
        this.rollbackManager = new PlayerRollbackManager();
    }

    public boolean isInSetupMode(final Player player) {
        return inSetupMode.containsKey(player.getUniqueId());
    }

    public SetupParkour getParkour(final Player player) {
        return inSetupMode.get(player.getUniqueId());
    }

    public void addToSetup(final Player player, final String parkourName) {
        if (isInSetupMode(player)) return;

        inSetupMode.put(player.getUniqueId(), new SetupParkour(plugin, parkourName, !plugin.getParkourDataManager().exists(parkourName)));
        rollbackManager.save(player);
        player.setGameMode(GameMode.CREATIVE);
        setSetupItem(player);
    }

    public void removeFromSetup(final Player player) {
        if (!isInSetupMode(player)) return;

        inSetupMode.remove(player.getUniqueId());

        rollbackManager.restore(player);
    }

    private void setSetupItem(final Player player) {
        final LazyItem setStartItem = plugin.getSettings().getSetStartItem();
        final LazyItem setEndItem = plugin.getSettings().getSetEndItem();
        final LazyItem wandItem = plugin.getSettings().getWandItem();
        final LazyItem saveItem = plugin.getSettings().getSaveItem();
        final LazyItem cancelItem = plugin.getSettings().getCancelItem();
        final LazyItem completePlayerCommandsItem = plugin.getSettings().getCompletePlayerCommands();
        final LazyItem completeConsoleCommandsItem = plugin.getSettings().getCompleteConsoleCommands();

        ItemStack setStart = new ItemBuilder(Material.matchMaterial(setStartItem.material()))
                .setDisplayName(colorize(setStartItem.name()))
                .setLore(colorize(setStartItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "SETSTART")
                .toItemStack();

        ItemStack setEnd = new ItemBuilder(Material.matchMaterial(setEndItem.material()))
                .setDisplayName(colorize(setEndItem.name()))
                .setLore(colorize(setEndItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "SETEND")
                .toItemStack();

        ItemStack setRegion = new ItemBuilder(Material.matchMaterial(wandItem.material()))
                .setDisplayName(colorize(wandItem.name()))
                .setLore(colorize(wandItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "WAND")
                .toItemStack();

        ItemStack save = new ItemBuilder(Material.matchMaterial(saveItem.material()))
                .setDisplayName(colorize(saveItem.name()))
                .setLore(colorize(saveItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "SAVE")
                .toItemStack();

        ItemStack cancel = new ItemBuilder(Material.matchMaterial(cancelItem.material()))
                .setDisplayName(colorize(cancelItem.name()))
                .setLore(colorize(cancelItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "CANCEL")
                .toItemStack();

        ItemStack completePlayerCommands = new ItemBuilder(Material.matchMaterial(completePlayerCommandsItem.material()))
                .setDisplayName(colorize(completePlayerCommandsItem.name()))
                .setLore(colorize(completePlayerCommandsItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "PLAYER-CMD")
                .toItemStack();

        ItemStack completeConsoleCommands = new ItemBuilder(Material.matchMaterial(completeConsoleCommandsItem.material()))
                .setDisplayName(colorize(completeConsoleCommandsItem.name()))
                .setLore(colorize(completeConsoleCommandsItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "CONSOLE-CMD")
                .toItemStack();

        player.getInventory().setItem(0, setStart);
        player.getInventory().setItem(1, setEnd);
        player.getInventory().setItem(2, setRegion);
        player.getInventory().setItem(3, completePlayerCommands);
        player.getInventory().setItem(4, completeConsoleCommands);
        player.getInventory().setItem(5, save);
        player.getInventory().setItem(8, cancel);
    }
}
