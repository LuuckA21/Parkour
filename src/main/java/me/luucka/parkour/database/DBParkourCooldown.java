package me.luucka.parkour.database;

import me.luucka.parkour.ParkourPlugin;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DBParkourCooldown {

    private final ParkourPlugin plugin;

    private final DBConnection dbConnection;

    private static final String DB_INIT = "CREATE TABLE IF NOT EXISTS `parkour` (" +
            "`uuid` VARCHAR(36) NOT NULL," +
            "`parkour` VARCHAR(50) NOT NULL," +
            "`time` int64 NOT NULL," +
            "PRIMARY KEY (`uuid`))";

    public DBParkourCooldown(ParkourPlugin plugin) throws SQLException {
        this.plugin = plugin;
        this.dbConnection = new DBConnection(plugin);
        _init();
    }

    private void _init() throws SQLException {
        try (Connection conn = dbConnection.getDbSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(DB_INIT)
        ) {
            ps.executeUpdate();
        }
    }

    public boolean exists(final UUID uuid, final String parkourName) {
        try (Connection conn = dbConnection.getDbSource().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM parkour WHERE uuid=? AND parkour=?")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, parkourName);
            return ps.executeQuery().next();
        } catch (final SQLException ex) {
            return false;
        }
    }

    public void createCooldown(final UUID uuid, final String parkourName) {
        try (Connection conn = dbConnection.getDbSource().getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO parkour (uuid, parkour, time) VALUES(?,?,?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, parkourName);
            ps.setLong(3, 0L);
            ps.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateCooldown(final UUID uuid, final String parkourName, final long time) {
        try (Connection conn = dbConnection.getDbSource().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE parkour SET time=? WHERE uuid=? AND parkour=?")) {
            ps.setLong(1, time);
            ps.setString(2, uuid.toString());
            ps.setString(3, parkourName);
            ps.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    public long getCooldown(final UUID uuid, final String parkourName) {
        try (Connection conn = dbConnection.getDbSource().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT time FROM parkour WHERE uuid=? AND parkour=?")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, parkourName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong("time");
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return -1L;
    }

}
