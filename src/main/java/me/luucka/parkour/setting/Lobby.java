package me.luucka.parkour.setting;

import lombok.Getter;
import me.luucka.extendlibrary.util.IReload;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.config.BaseConfiguration;
import org.bukkit.Location;

import java.io.File;

public class Lobby implements IReload {

    private final BaseConfiguration config;

    @Getter
    private Location lobbyLocation;


    public Lobby(final ParkourPlugin plugin) {
        this.config = new BaseConfiguration(new File(plugin.getDataFolder(), "lobby.yml"), "/lobby.yml");
        reload();
    }

    @Override
    public void reload() {
        config.load();
        lobbyLocation = config.getLocation("lobby").location();
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
        config.setProperty("lobby", lobbyLocation);
        config.save();
    }
}
