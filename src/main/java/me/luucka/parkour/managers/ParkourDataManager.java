package me.luucka.parkour.managers;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.config.IConfig;
import me.luucka.parkour.entities.Cuboid;
import org.bukkit.Location;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParkourDataManager implements IConfig {

    private static final Logger LOGGER = Logger.getLogger("Parkour");

    private final ParkourPlugin plugin;

    private final File dataFolder;

    private final Map<String, BaseConfiguration> parkourData = new HashMap<>();

    public ParkourDataManager(final ParkourPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(this.plugin.getDataFolder(), "parkours");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
        reloadConfig();
        LOGGER.log(Level.INFO, "Loaded " + parkourData.size() + " Parkours!");
    }

    public List<String> getAllParkoursName() {
        return new ArrayList<>(parkourData.keySet());
    }

    public boolean exists(final String name) {
        final BaseConfiguration configuration = parkourData.get(name);
        return configuration != null;
    }

    public void create(final String name) {
        BaseConfiguration configuration = parkourData.get(name);
        if (configuration == null) {
            final File file = new File(dataFolder, name + ".yml");
            if (file.exists()) {
                return;
            }
            configuration = new BaseConfiguration(file);
            configuration.load();
            configuration.save();
            parkourData.put(name, configuration);
        }
    }

    public void delete(final String name) {
        parkourData.remove(name);
        final File file = new File(dataFolder, name + ".yml");
        file.delete();
    }

    public void setStartLoc(final String name, final Location location) {
        final BaseConfiguration configuration = parkourData.get(name);
        configuration.setProperty("start-location", location);
        configuration.save();
    }

    public void setEndLoc(final String name, final Location location) {
        final BaseConfiguration configuration = parkourData.get(name);
        configuration.setProperty("end-location", location);
        configuration.save();
    }

    public void setRegion(final String name, final Location loc1, final Location loc2) {
        final BaseConfiguration configuration = parkourData.get(name);
        configuration.setProperty("region.min", new Location(loc1.getWorld(), Math.min(loc1.getX(), loc2.getX()), Math.min(loc1.getY(), loc2.getY()), Math.min(loc1.getZ(), loc2.getZ())));
        configuration.setProperty("region.max", new Location(loc1.getWorld(), Math.max(loc1.getX(), loc2.getX()), Math.max(loc1.getY(), loc2.getY()), Math.max(loc1.getZ(), loc2.getZ())));
        configuration.save();
    }

    public void setCompletePlayerCommands(final String name, final List<String> cmds) {
        final BaseConfiguration configuration = parkourData.get(name);
        configuration.setProperty("complete-commands.player", cmds);
        configuration.save();
    }

    public void setCompleteConsoleCommands(final String name, final List<String> cmds) {
        final BaseConfiguration configuration = parkourData.get(name);
        configuration.setProperty("complete-commands.console", cmds);
        configuration.save();
    }

    public Location getStartLocation(final String name) {
        return parkourData.get(name).getLocation("start-location").location();
    }

    public Location getEndLocation(final String name) {
        return parkourData.get(name).getLocation("end-location").location();
    }

    public List<String> getCompletePlayerCommands(final String name) {
        return parkourData.get(name).getList("complete-commands.player", String.class);
    }

    public List<String> getCompleteConsoleCommands(final String name) {
        return parkourData.get(name).getList("complete-commands.console", String.class);
    }

    public Location getLocationMin(final String name) {
        return parkourData.get(name).getLocation("region.min").location();
    }

    public Location getLocationMax(final String name) {
        return parkourData.get(name).getLocation("region.max").location();
    }

    public Cuboid getCuboid(final String name) {
        return new Cuboid(getLocationMin(name), getLocationMax(name));
    }

    @Override
    public void reloadConfig() {
        parkourData.clear();
        final File[] listOfFiles = dataFolder.listFiles();
        if (listOfFiles.length >= 1) {
            for (final File file : listOfFiles) {
                String fileName = file.getName();
                if (file.isFile() && fileName.endsWith(".yml")) {
                    try {
                        final BaseConfiguration configuration = new BaseConfiguration(file);
                        configuration.load();
                        parkourData.put(fileName.substring(0, fileName.length() - 4), configuration);
                    } catch (final Exception ex) {
                        LOGGER.log(Level.WARNING, "Parkour file " + fileName + " loading error!");
                    }
                }
            }
        }
    }
}
