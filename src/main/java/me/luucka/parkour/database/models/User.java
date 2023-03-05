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

    @BsonProperty(value = "lastplayedtimes")
    @Getter
    private final Map<String, Long> lastPlayedTimes;

    @BsonCreator
    public User(@BsonProperty(value = "_id") UUID uuid, @BsonProperty(value = "lastplayedtimes") HashMap<String, Long> lastPlayedTimes) {
        this.uuid = uuid;
        this.lastPlayedTimes = lastPlayedTimes;
    }

    public void updateParkourLastPlayedTime(final String parkour, final Long lastPlayedTime) {
        lastPlayedTimes.put(parkour, lastPlayedTime);
    }
}
