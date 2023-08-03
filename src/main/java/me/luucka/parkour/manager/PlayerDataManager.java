package me.luucka.parkour.manager;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.database.MongoStorage;
import me.luucka.parkour.database.model.PlayerParkourData;
import me.luucka.parkour.entity.Parkour;
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

    public PlayerParkourData getPlayerParkourData(final UUID uuid, final Parkour parkour) {
        return mongoStorage.getPlayerParkourData(uuid, parkour);
    }

    public void updateParkourData(final UUID uuid, final String parkour, final PlayerParkourData parkourData) {
        mongoStorage.updateParkourData(uuid, parkour, parkourData);
    }

    public void shutdown() {
        mongoStorage.shutdown();
    }

}
