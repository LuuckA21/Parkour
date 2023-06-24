package me.luucka.parkour.entities;

import lombok.Getter;
import lombok.Setter;
import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.managers.DataManager;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;

import java.util.*;

import static me.luucka.parkour.utils.MMColor.toComponent;

public class SetupParkour {
    private final DataManager dataManager;
    private final Messages messages;

    @Getter
    private String name;

    private Parkour parkour;

    @Getter
    @Setter
    private Location startLocation;

    @Getter
    @Setter
    private Location endLocation;

    @Getter
    @Setter
    private Location minRegion;

    @Getter
    @Setter
    private Location maxRegion;

    @Getter
    private List<String> completeCommands = new ArrayList<>();

    @Getter
    @Setter
    private long cooldown = -1L;


    @Getter
    private Set<Checkpoint> checkpoints = new HashSet<>();

    public SetupParkour(final ParkourPlugin plugin, final Parkour parkour) {
        this.dataManager = plugin.getDataManager();
        this.messages = plugin.getMessages();
        this.name = parkour.getName();
        this.parkour = parkour;
        this.startLocation = parkour.getStartLocation();
        this.endLocation = parkour.getEndLocation();
        this.minRegion = parkour.getMinRegion();
        this.maxRegion = parkour.getMaxRegion();
        this.completeCommands = parkour.getCompleteCommands();
        this.cooldown = parkour.getCooldown();
        this.checkpoints = parkour.getCheckpoints();
        parkour.setupMode();
    }

    public SetupParkour(final ParkourPlugin plugin, final String name) {
        this.dataManager = plugin.getDataManager();
        this.messages = plugin.getMessages();
        this.name = name;
    }

    public void addConsoleCommands(final String string) {
        completeCommands.clear();
        completeCommands.addAll(Arrays.asList(string.split(";")));
    }

    public void clearConsoleCommands() {
        completeCommands.clear();
    }

    public void addCheckpoint(final Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
    }

    public void removeCheckpoint(final Checkpoint checkpoint) {
        checkpoints.remove(checkpoint);
    }

    public void resetAllCheckpoints() {
        checkpoints.clear();
    }

    public boolean canSave() {
        return startLocation != null
                && endLocation != null
                && minRegion != null
                && maxRegion != null
                && completeCommands.size() >= 1;
    }

    public void save() {
        if (this.parkour == null) {
            dataManager.create(this);
        } else {
            this.parkour.update(this);
        }

        Sign sign = (Sign) endLocation.getBlock().getState();
        final String[] lines = messages.completeSign(name);
        for (int i = 0; i < lines.length; i++) {
            sign.getSide(Side.FRONT).line(i, toComponent(lines[i]));
        }
        sign.update();
    }

    public void cancel() {
        if (this.parkour != null) this.parkour.playMode();
    }
}
