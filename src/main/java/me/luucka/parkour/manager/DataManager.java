package me.luucka.parkour.manager;

import me.luucka.extendlibrary.util.IReload;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.model.Parkour;
import me.luucka.parkour.model.SetupParkour;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataManager implements IReload {

    private static final Logger LOGGER = Logger.getLogger("Parkour");
    private final File dataFolder;
    private final Set<Parkour> parkours = new HashSet<>();

    public DataManager(final ParkourPlugin plugin) {
        this.dataFolder = new File(plugin.getDataFolder(), "parkours");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
        reload();
    }

    public List<String> getAllParkoursName() {
        return parkours.stream().map(Parkour::getName).toList();
    }

    public boolean exists(final String name) {
        return parkours.stream().anyMatch(parkour -> parkour.getName().equalsIgnoreCase(name));
    }

    public Optional<Parkour> getPlayableParkour(final String name) {
        return parkours.stream().filter(parkour -> parkour.getName().equalsIgnoreCase(name) && parkour.getStatus() == Parkour.Status.PLAY).findFirst();
    }

    public Optional<Parkour> getParkour(final String name) {
        return parkours.stream().filter(parkour -> parkour.getName().equalsIgnoreCase(name)).findFirst();
    }

    public void create(final SetupParkour setupParkour) {
        if (!exists(setupParkour.getName())) {
            final File file = new File(dataFolder, setupParkour.getName() + ".yml");
            final BaseConfiguration configuration = new BaseConfiguration(file);
            parkours.add(new Parkour(configuration, setupParkour));
        }
    }

    public void delete(final Parkour parkour) {
        parkours.remove(parkour);
        parkour.delete();
    }

    @Override
    public void reload() {
        parkours.clear();
        final File[] fileList = dataFolder.listFiles();
        if (fileList.length >= 1) {
            for (final File file : fileList) {
                final String fileName = file.getName();
                if (file.isFile() && fileName.endsWith(".yml")) {
                    try {
                        parkours.add(new Parkour(new BaseConfiguration(file)));
                    } catch (final Exception ex) {
                        LOGGER.log(Level.WARNING, "Parkour file '" + fileName + "' loading error!");
                    }
                }
            }
        }
    }
}
