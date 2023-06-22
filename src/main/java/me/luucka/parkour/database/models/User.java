package me.luucka.parkour.database.models;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {

    @BsonProperty(value = "_id")
    @Getter
    private final UUID uuid;

    @BsonProperty(value = "parkour-data")
    @Getter
    private final Map<String, PlayerParkourData> playerParkourData;

    @BsonCreator
    public User(@BsonProperty(value = "_id") UUID uuid, @BsonProperty(value = "parkour-data") HashMap<String, PlayerParkourData> playerParkourData) {
        this.uuid = uuid;
        this.playerParkourData = playerParkourData;
    }

    public void updateParkourData(final String parkour, final PlayerParkourData parkourData) {
        if (!playerParkourData.containsKey(parkour)) {
            playerParkourData.put(parkour, parkourData);
            return;
        }
        PlayerParkourData old = playerParkourData.get(parkour);
        
        if (parkourData.getDeaths() < old.getDeaths()) {
            old.setDeaths(parkourData.getDeaths());
        }
        if (parkourData.getTimeToComplete() < old.getTimeToComplete()) {
            old.setTimeToComplete(parkourData.getTimeToComplete());
        }
        old.setLastPlayedTime(parkourData.getLastPlayedTime());

    }
}
