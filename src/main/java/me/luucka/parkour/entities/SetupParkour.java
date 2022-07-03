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

import static me.luucka.parkour.utils.Color.colorize;

public class SetupParkour {

    private final ParkourPlugin plugin;

    @Getter
    private final String parkourName;

    private final boolean isNew;

    @Setter
    private Location startLocation;

    @Setter
    private Location endLocation;

    @Setter
    private Location regionOne;

    @Setter
    private Location regionTwo;

    private List<String> completePlayerCommands = new ArrayList<>();

    private List<String> completeConsoleCommands = new ArrayList<>();

    @Setter
    private int cooldown = -1;

    public SetupParkour(final ParkourPlugin plugin, final String parkourName, final boolean isNew) {
        this.plugin = plugin;
        this.parkourName = parkourName;
        this.isNew = isNew;
        if (!isNew) loadExistParkour();
    }

    private void loadExistParkour() {
        startLocation = plugin.getParkourDataManager().getStartLocation(parkourName);
        endLocation = plugin.getParkourDataManager().getEndLocation(parkourName);
        regionOne = plugin.getParkourDataManager().getLocationMin(parkourName);
        regionTwo = plugin.getParkourDataManager().getLocationMax(parkourName);
        completePlayerCommands = plugin.getParkourDataManager().getCompletePlayerCommands(parkourName);
        completeConsoleCommands = plugin.getParkourDataManager().getCompleteConsoleCommands(parkourName);
        cooldown = plugin.getParkourDataManager().getCooldown(parkourName);
    }

    public void addPlayerCommands(final String string) {
        completePlayerCommands.addAll(Arrays.asList(string.split(";")));
    }

    public void clearCompletePlayerCommands() {
        completePlayerCommands.clear();
    }

    public void addConsoleCommands(final String string) {
        completeConsoleCommands.addAll(Arrays.asList(string.split(";")));
    }

    public void clearCompleteConsoleCommands() {
        completeConsoleCommands.clear();
    }

    public boolean canSave() {
        return startLocation != null
                && endLocation != null
                && regionOne != null
                && regionTwo != null
                && completePlayerCommands.size() >= 1
                && completeConsoleCommands.size() >= 1;
    }

    public void saveToConfig() {
        if (isNew) {
            plugin.getParkourDataManager().create(parkourName);
        }
        plugin.getParkourDataManager().setStartLoc(parkourName, startLocation);
        plugin.getParkourDataManager().setEndLoc(parkourName, endLocation);
        plugin.getParkourDataManager().setRegion(parkourName, regionOne, regionTwo);
        plugin.getParkourDataManager().setCompletePlayerCommands(parkourName, completePlayerCommands);
        plugin.getParkourDataManager().setCompleteConsoleCommands(parkourName, completeConsoleCommands);
        plugin.getParkourDataManager().setCooldown(parkourName, cooldown);

        Block endBlock = endLocation.getBlock();
        Sign sign = (Sign) endBlock.getState();
        final String[] lines = plugin.getSettings().getCompleteSign(parkourName);
        for (int i = 0; i < lines.length; i++) {
            sign.line(i, colorize(lines[i]));
        }
        sign.update();
    }
}
