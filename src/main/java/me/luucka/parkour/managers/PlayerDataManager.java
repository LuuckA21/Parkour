package me.luucka.parkour.managers;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.database.MongoStorage;
import me.luucka.parkour.entities.Parkour;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerDataManager {

    private final MongoStorage mongoStorage;

    public PlayerDataManager(final ParkourPlugin plugin) {
        this.mongoStorage = new MongoStorage(plugin);
        this.mongoStorage.init();
    }

    public void createPlayerData(final Player player) {
        mongoStorage.createPlayerData(player);
    }

    public Long getLastPlayedTime(final UUID uuid, final Parkour parkour) {
        return mongoStorage.getLastPlayedTime(uuid, parkour);
    }

    public void updateLastPlayedTime(final UUID uuid, final String parkour, final Long lastPlayedTime) {
        mongoStorage.updateLastPlayedTime(uuid, parkour, lastPlayedTime);
    }

    public void shutdown() {
        mongoStorage.shutdown();
    }

}
