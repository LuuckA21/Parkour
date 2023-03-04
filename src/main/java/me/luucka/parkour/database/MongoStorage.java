package me.luucka.parkour.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import me.luucka.parkour.ParkourPlugin;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MongoStorage {

    private final ParkourPlugin plugin;

    private MongoClient client;

    private MongoDatabase database;

    public MongoStorage(final ParkourPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        ConnectionString connectionString = new ConnectionString(plugin.getSettings().getMongoDbConnectionUri());
        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                .applyConnectionString(connectionString)
                .build();
        this.client = MongoClients.create(settings);
        this.database = client.getDatabase("parkour");
    }

    public void createPlayerData(final Player player) {
        MongoCollection<Document> collection = database.getCollection("playersdata");
        final Document document = new Document();
        document.put("_id", player.getUniqueId());
        document.put("parkoursdata", new HashMap<String, Long>());
        collection.insertOne(document);
    }

    public void updateCooldown(final UUID uuid, final String parkour, final long time) {
        MongoCollection<Document> collection = database.getCollection("playersdata");
        try (MongoCursor<Document> cursor = collection.find(Filters.eq("_id", uuid)).cursor()) {
            while (cursor.hasNext()) {
                final Document doc = cursor.next();
            }
        }
    }

    public void shutdown() {
        if (this.client != null) {
            this.client.close();
        }
    }
}
