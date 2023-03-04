package me.luucka.parkour.entities;

import lombok.Getter;
import lombok.Setter;
import me.luucka.parkour.ParkourPlugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.luucka.helplib.color.MMColor.toComponent;

public class SetupParkour {

    private final ParkourPlugin plugin;

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
    private List<String> playerCommands = new ArrayList<>();

    @Getter
    private List<String> consoleCommands = new ArrayList<>();

    @Getter
    @Setter
    private long cooldown = -1L;

    public SetupParkour(final ParkourPlugin plugin, final Parkour parkour) {
        this.plugin = plugin;
        this.name = parkour.getName();
        this.parkour = parkour;
        this.startLocation = parkour.getStartLocation();
        this.endLocation = parkour.getEndLocation();
        this.minRegion = parkour.getMinRegion();
        this.maxRegion = parkour.getMaxRegion();
        this.playerCommands = parkour.getPlayerCommands();
        this.consoleCommands = parkour.getConsoleCommands();
        this.cooldown = parkour.getCooldown();
        parkour.setupMode();
    }

    public SetupParkour(final ParkourPlugin plugin, final String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public void addPlayerCommands(final String string) {
        playerCommands.addAll(Arrays.asList(string.split(";")));
    }

    public void clearPlayerCommands() {
        playerCommands.clear();
    }

    public void addConsoleCommands(final String string) {
        consoleCommands.addAll(Arrays.asList(string.split(";")));
    }

    public void clearConsoleCommands() {
        consoleCommands.clear();
    }

    public boolean canSave() {
        return startLocation != null
                && endLocation != null
                && minRegion != null
                && maxRegion != null
                && playerCommands.size() >= 1
                && consoleCommands.size() >= 1;
    }

    public void save() {
        if (this.parkour == null) {
            plugin.getDataManager().create(this);
        } else {
            this.parkour.update(this);
        }

        Block endBlock = endLocation.getBlock();
        Sign sign = (Sign) endBlock.getState();
        final String[] lines = plugin.getSettings().getCompleteSign(name);
        for (int i = 0; i < lines.length; i++) {
            sign.line(i, toComponent(lines[i]));
        }
        sign.update();
    }

    public void cancel() {
        if (this.parkour != null) this.parkour.playMode();
    }
}
