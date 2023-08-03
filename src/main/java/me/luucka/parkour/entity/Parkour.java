package me.luucka.parkour.entity;

import lombok.Getter;
import me.luucka.parkour.config.BaseConfiguration;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

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
    private List<String> completeCommands = new ArrayList<>();

    @Getter
    private long cooldown;

    @Getter
    private Status status;

    @Getter
    private Set<Checkpoint> checkpoints = new HashSet<>();

    public void setupMode() {
        if (this.status == Status.SETUP) return;
        this.status = Status.SETUP;
    }

    public void playMode() {
        if (this.status == Status.PLAY) return;
        this.status = Status.PLAY;
    }


    public Optional<Checkpoint> getCheckpointByNumber(int number) {
        return checkpoints.stream().filter(checkpoint -> checkpoint.getNumber() == number).findFirst();
    }

    public Optional<Checkpoint> getCheckpointByLocation(final Location location) {
        return checkpoints.stream().filter(checkpoint -> checkpoint.getBlockLocation().equals(location)).findFirst();
    }

    public Parkour(final BaseConfiguration configuration) {
        this.configuration = configuration;
        this.configuration.load();
        this.startLocation = configuration.getLocation("start-location").location();
        this.endLocation = configuration.getLocation("end-location").location();
        this.minRegion = configuration.getLocation("region.min").location();
        this.maxRegion = configuration.getLocation("region.max").location();
        this.completeCommands.addAll(configuration.getList("complete-commands", String.class));
        this.cooldown = configuration.getLong("cooldown", -1L);
        this.status = Status.PLAY;
        if (configuration.hasProperty("checkpoints")) {
            Set<String> keys = configuration.getKeys("checkpoints");
            for (final String chkp : keys) {
                checkpoints.add(new Checkpoint(Integer.parseInt(chkp), configuration.getLocation("checkpoints." + chkp + ".tp").location(), configuration.getLocation("checkpoints." + chkp + ".block").location()));
            }
        }
    }

    public Parkour(final BaseConfiguration configuration, final SetupParkour setupParkour) {
        this.configuration = configuration;
        this.configuration.load();
        this.startLocation = setupParkour.getStartLocation();
        this.endLocation = setupParkour.getEndLocation();
        this.minRegion = setupParkour.getMinRegion();
        this.maxRegion = setupParkour.getMaxRegion();
        this.completeCommands = setupParkour.getCompleteCommands();
        this.cooldown = setupParkour.getCooldown();
        this.checkpoints = setupParkour.getCheckpoints();
        save();
    }

    public void update(final SetupParkour setupParkour) {
        if (this.status != Status.SETUP) return;
        this.startLocation = setupParkour.getStartLocation();
        this.endLocation = setupParkour.getEndLocation();
        this.minRegion = setupParkour.getMinRegion();
        this.maxRegion = setupParkour.getMaxRegion();
        this.completeCommands = setupParkour.getCompleteCommands();
        this.cooldown = setupParkour.getCooldown();
        this.checkpoints = setupParkour.getCheckpoints();
        save();
    }

    public void save() {
        configuration.setProperty("start-location", startLocation);
        configuration.setProperty("end-location", endLocation);
        configuration.setProperty("region.min", minRegion);
        configuration.setProperty("region.max", maxRegion);
        configuration.setProperty("complete-commands", completeCommands);
        configuration.setProperty("cooldown", cooldown);
        configuration.removeProperty("checkpoints");
        checkpoints.stream().sorted(Comparator.comparing(Checkpoint::getNumber)).collect(Collectors.toCollection(LinkedHashSet::new))
                .forEach(checkpoint -> {
                    configuration.setProperty("checkpoints." + checkpoint.getNumber() + ".tp", checkpoint.getTpLocation());
                    configuration.setProperty("checkpoints." + checkpoint.getNumber() + ".block", checkpoint.getBlockLocation());
                });
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
