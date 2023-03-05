package me.luucka.parkour.managers;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.Settings;
import me.luucka.parkour.config.entities.LazyItem;
import me.luucka.parkour.entities.SetupParkour;
import me.luucka.parkour.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.luucka.parkour.utils.MMColor.toComponent;

public class SetupManager {

    private final ParkourPlugin plugin;
    private final Settings settings;
    private final PlayerRollbackManager rollbackManager;

    private final Map<UUID, SetupParkour> playerInSetup = new HashMap<>();

    public SetupManager(ParkourPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.rollbackManager = new PlayerRollbackManager();
    }

    public boolean isPlayerInSetupMode(final Player player) {
        return playerInSetup.containsKey(player.getUniqueId());
    }

    public SetupParkour getSetupParkourByPlayer(final Player player) {
        return playerInSetup.get(player.getUniqueId());
    }

    public void addPlayerToSetupMode(final Player player, final SetupParkour parkour) {
        if (isPlayerInSetupMode(player)) return;

        playerInSetup.put(player.getUniqueId(), parkour);
        rollbackManager.save(player);
        player.setGameMode(GameMode.CREATIVE);
        setSetupItems(player);
    }

    public void removePlayerFromSetupMode(final Player player) {
        if (!isPlayerInSetupMode(player)) return;
        playerInSetup.remove(player.getUniqueId());
        rollbackManager.restore(player);
    }

    private void setSetupItems(final Player player) {
        final LazyItem setStartItem = settings.getSetStartItem();
        final LazyItem setEndItem = settings.getSetEndItem();
        final LazyItem wandItem = settings.getWandItem();
        final LazyItem saveItem = settings.getSaveItem();
        final LazyItem cancelItem = settings.getCancelItem();
        final LazyItem completePlayerCommandsItem = settings.getCompletePlayerCommands();
        final LazyItem completeConsoleCommandsItem = settings.getCompleteConsoleCommands();
        final LazyItem setCooldownItem = settings.getSetCooldown();

        ItemStack setStart = new ItemBuilder(setStartItem.material())
                .setDisplayName(toComponent(setStartItem.name()))
                .setLore(toComponent(setStartItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "SETSTART")
                .toItemStack();

        ItemStack setEnd = new ItemBuilder(setEndItem.material())
                .setDisplayName(toComponent(setEndItem.name()))
                .setLore(toComponent(setEndItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "SETEND")
                .toItemStack();

        ItemStack setRegion = new ItemBuilder(wandItem.material())
                .setDisplayName(toComponent(wandItem.name()))
                .setLore(toComponent(wandItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "WAND")
                .toItemStack();

        ItemStack save = new ItemBuilder(saveItem.material())
                .setDisplayName(toComponent(saveItem.name()))
                .setLore(toComponent(saveItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "SAVE")
                .toItemStack();

        ItemStack cancel = new ItemBuilder(cancelItem.material())
                .setDisplayName(toComponent(cancelItem.name()))
                .setLore(toComponent(cancelItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "CANCEL")
                .toItemStack();

        ItemStack completePlayerCommands = new ItemBuilder(completePlayerCommandsItem.material())
                .setDisplayName(toComponent(completePlayerCommandsItem.name()))
                .setLore(toComponent(completePlayerCommandsItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "PLAYER-CMD")
                .toItemStack();

        ItemStack completeConsoleCommands = new ItemBuilder(completeConsoleCommandsItem.material())
                .setDisplayName(toComponent(completeConsoleCommandsItem.name()))
                .setLore(toComponent(completeConsoleCommandsItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "CONSOLE-CMD")
                .toItemStack();

        ItemStack setCooldown = new ItemBuilder(setCooldownItem.material())
                .setDisplayName(toComponent(setCooldownItem.name()))
                .setLore(toComponent(setCooldownItem.lore()))
                .setPersistentDataContainerValue(plugin, "setup-item", "COOLDOWN")
                .toItemStack();

        player.getInventory().setItem(0, setStart);
        player.getInventory().setItem(1, setEnd);
        player.getInventory().setItem(2, setRegion);
        player.getInventory().setItem(3, completePlayerCommands);
        player.getInventory().setItem(4, completeConsoleCommands);
        player.getInventory().setItem(5, setCooldown);
        player.getInventory().setItem(7, save);
        player.getInventory().setItem(8, cancel);
    }
}
