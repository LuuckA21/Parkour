//package me.luucka.parkour.database;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import lombok.Getter;
//import me.luucka.parkour.ParkourPlugin;
//
//public class DBConnection {
//
//    private final ParkourPlugin plugin;
//
//    @Getter
//    private HikariDataSource dbSource;
//
//    public DBConnection(ParkourPlugin plugin) {
//        this.plugin = plugin;
//        _loadDbSource();
//    }
//
//    private void _loadDbSource() {
//        final HikariConfig config = new HikariConfig();
//        config.setPoolName("Parkour");
//        config.setDriverClassName("org.sqlite.JDBC");
//        String dbFile = plugin.getDataFolder().getAbsolutePath() + "/";
//        config.setJdbcUrl(String.format("jdbc:%s:%s", "sqlite", dbFile + "parkour.db"));
//        dbSource = new HikariDataSource(config);
//    }
//
//}
