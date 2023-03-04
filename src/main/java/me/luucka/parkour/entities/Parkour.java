package me.luucka.parkour.entities;

import lombok.Getter;
import me.luucka.parkour.config.BaseConfiguration;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Parkour {

    public String getName() {
        return configuration.getFile().getName().substring(0, configuration.getFile().getName().length() - 4);
    }

    private final BaseConfiguration configuration;

    @Getter
    private Location startLocation;

    @Getter
    private Location endLocation;

    @Getter
    private Location minRegion;

    @Getter
    private Location maxRegion;

    public Cuboid getRegion() {
        return new Cuboid(minRegion, maxRegion);
    }

    @Getter
    private List<String> playerCommands = new ArrayList<>();

    @Getter
    private List<String> consoleCommands = new ArrayList<>();

    @Getter
    private long cooldown;

    @Getter
    private Status status;

    public void setupMode() {
        if (this.status == Status.SETUP) return;
        this.status = Status.SETUP;
    }

    public void playMode() {
        if (this.status == Status.PLAY) return;
        this.status = Status.PLAY;
    }

    public Parkour(final BaseConfiguration configuration) {
        this.configuration = configuration;
        this.configuration.load();
        this.startLocation = configuration.getLocation("start-location").location();
        this.endLocation = configuration.getLocation("start-location").location();
        this.minRegion = configuration.getLocation("region.min").location();
        this.maxRegion = configuration.getLocation("region.max").location();
        this.playerCommands.addAll(configuration.getList("complete-commands.player", String.class));
        this.consoleCommands.addAll(configuration.getList("complete-commands.console", String.class));
        this.cooldown = configuration.getLong("cooldown", -1L);
        this.status = Status.PLAY;
    }

    public Parkour(final BaseConfiguration configuration, final SetupParkour setupParkour) {
        this.configuration = configuration;
        this.configuration.load();
        this.startLocation = setupParkour.getStartLocation();
        this.endLocation = setupParkour.getEndLocation();
        this.minRegion = setupParkour.getMinRegion();
        this.maxRegion = setupParkour.getMaxRegion();
        this.playerCommands = setupParkour.getPlayerCommands();
        this.consoleCommands = setupParkour.getConsoleCommands();
        this.cooldown = setupParkour.getCooldown();
        save();
    }

    public void update(final SetupParkour setupParkour) {
        if (this.status != Status.SETUP) return;
        this.startLocation = setupParkour.getStartLocation();
        this.endLocation = setupParkour.getEndLocation();
        this.minRegion = setupParkour.getMinRegion();
        this.maxRegion = setupParkour.getMaxRegion();
        this.playerCommands = setupParkour.getPlayerCommands();
        this.consoleCommands = setupParkour.getConsoleCommands();
        this.cooldown = setupParkour.getCooldown();
        save();
    }

    public void save() {
        configuration.setProperty("start-location", startLocation);
        configuration.setProperty("end-location", endLocation);
        configuration.setProperty("region.min", minRegion);
        configuration.setProperty("region.max", maxRegion);
        configuration.setProperty("complete-commands.player", playerCommands);
        configuration.setProperty("complete-commands.console", consoleCommands);
        configuration.setProperty("cooldown", cooldown);
        configuration.save();
        playMode();
    }

    public void delete() {
        configuration.getFile().delete();
    }

    public enum Status {
        PLAY,
        SETUP;
    }

}
