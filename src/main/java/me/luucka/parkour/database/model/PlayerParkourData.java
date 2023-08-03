package me.luucka.parkour.database.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class PlayerParkourData {

    @BsonProperty(value = "last-played-time")
    @Getter
    @Setter
    private Long lastPlayedTime;

    @BsonProperty(value = "deaths")
    @Getter
    @Setter
    private Integer deaths;

    @BsonProperty(value = "time-to-complete")
    @Getter
    @Setter
    private Long timeToComplete;

    @BsonCreator
    public PlayerParkourData(@BsonProperty(value = "last-played-time") final Long lastPlayedTime, @BsonProperty(value = "deaths") final Integer deaths, @BsonProperty(value = "time-to-complete") final Long timeToComplete) {
        this.lastPlayedTime = lastPlayedTime;
        this.deaths = deaths;
        this.timeToComplete = timeToComplete;
    }
}
