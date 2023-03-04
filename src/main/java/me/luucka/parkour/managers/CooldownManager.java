//package me.luucka.parkour.managers;
//
//import me.luucka.parkour.ParkourPlugin;
//import me.luucka.parkour.database.DBParkourCooldown;
//import org.bukkit.Bukkit;
//
//import java.sql.SQLException;
//import java.util.UUID;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class CooldownManager {
//
//    private static final Logger LOGGER = Logger.getLogger("Parkour");
//
//    private final ParkourPlugin plugin;
//
//    private DBParkourCooldown dbParkourCooldown;
//
//    public CooldownManager(ParkourPlugin plugin) {
//        this.plugin = plugin;
//        try {
//            this.dbParkourCooldown = new DBParkourCooldown(plugin);
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Cannot load Cooldown manager, disabling plugin...");
//            Bukkit.getPluginManager().disablePlugin(plugin);
//        }
//    }
//
//    public boolean exists(final UUID uuid, final String parkourName) {
//        return dbParkourCooldown.exists(uuid, parkourName.toLowerCase());
//    }
//
//    public void createCooldown(final UUID uuid, final String parkourName) {
//        dbParkourCooldown.createCooldown(uuid, parkourName.toLowerCase());
//    }
//
//    public void updateCooldown(final UUID uuid, final String parkourName, final long time) {
//        dbParkourCooldown.updateCooldown(uuid, parkourName.toLowerCase(), time);
//    }
//
//    public long getCooldown(final UUID uuid, final String parkourName) {
//        return dbParkourCooldown.getCooldown(uuid, parkourName.toLowerCase());
//    }
//
//}
