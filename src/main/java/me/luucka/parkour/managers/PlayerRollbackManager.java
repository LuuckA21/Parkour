package me.luucka.parkour.managers;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRollbackManager {

    private final Map<UUID, GameMode> previousGameMode = new HashMap<>();
    private final Map<UUID, ItemStack[]> previousInventory = new HashMap<>();
    private final Map<UUID, ItemStack[]> previousArmor = new HashMap<>();
    private final Map<UUID, Integer> previousHungerValue = new HashMap<>();
    private final Map<UUID, Integer> previousXpLevel = new HashMap<>();

    public void save(Player player) {
        UUID uuid = player.getUniqueId();
        previousGameMode.put(uuid, player.getGameMode());
        previousInventory.put(uuid, player.getInventory().getContents());
        previousArmor.put(uuid, player.getInventory().getArmorContents());
        previousHungerValue.put(uuid, player.getFoodLevel());
        previousXpLevel.put(uuid, player.getLevel());

        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);
    }

    public void restore(Player player) {
        UUID uuid = player.getUniqueId();
        player.getInventory().clear();

        GameMode previousGameMode = this.previousGameMode.get(uuid);
        if (previousGameMode != null) {
            player.setGameMode(previousGameMode);
        }

        ItemStack[] inventoryContent = previousInventory.get(uuid);
        if (inventoryContent != null) {
            player.getInventory().setContents(inventoryContent);
        }

        ItemStack[] armorContent = previousArmor.get(uuid);
        if (armorContent != null) {
            player.getInventory().setArmorContents(armorContent);
        }

        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));

        player.setFoodLevel(previousHungerValue.getOrDefault(uuid, 20));

        player.setLevel(previousXpLevel.get(uuid));
        
        this.previousGameMode.remove(uuid);
        previousInventory.remove(uuid);
        previousArmor.remove(uuid);
        previousHungerValue.remove(uuid);
        previousXpLevel.remove(uuid);
    }

}
