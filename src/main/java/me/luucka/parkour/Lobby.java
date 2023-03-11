package me.luucka.parkour;

import lombok.Getter;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.config.IConfig;
import org.bukkit.Location;

import java.io.File;

public class Lobby implements IConfig {

    private final BaseConfiguration config;

    @Getter
    private Location lobbyLocation;


    public Lobby(final ParkourPlugin plugin) {
        this.config = new BaseConfiguration(new File(plugin.getDataFolder(), "lobby.yml"), "/lobby.yml");
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        config.load();
        lobbyLocation = config.getLocation("lobby").location();
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
        config.setProperty("lobby", lobbyLocation);
        config.save();
    }
}
