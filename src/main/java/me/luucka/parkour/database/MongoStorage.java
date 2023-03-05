package me.luucka.parkour.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.Settings;
import me.luucka.parkour.database.models.User;
import me.luucka.parkour.entities.Parkour;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MongoStorage {

    private final Settings settings;
    private MongoClient client;
    private MongoDatabase database;

    public MongoStorage(final ParkourPlugin plugin) {
        this.settings = plugin.getSettings();
    }

    public void init() {
        ConnectionString connectionString = new ConnectionString(settings.getMongoDbConnectionUri());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        this.client = MongoClients.create(settings);
        this.database = client.getDatabase("parkour");
    }

    public void createPlayerData(final Player player) {
        MongoCollection<User> collection = database.getCollection("playersdata", User.class);
        final User user = new User(player.getUniqueId(), new HashMap<>());
        try {
            collection.insertOne(user);
        } catch (final MongoWriteException ignored) {
        }
    }

    public Long getLastPlayedTime(final UUID uuid, final Parkour parkour) {
        MongoCollection<User> collection = database.getCollection("playersdata", User.class);
        final User user = collection.find(Filters.eq("_id", uuid)).first();
        if (user == null) return -1L;
        return user.getLastPlayedTimes().getOrDefault(parkour.getName(), -1L);
    }

    public void updateLastPlayedTime(final UUID uuid, final String parkour, final Long lastPlayedTime) {
        MongoCollection<User> collection = database.getCollection("playersdata", User.class);
        final User user = collection.find(Filters.eq("_id", uuid)).first();
        if (user == null) return;
        user.updateParkourLastPlayedTime(parkour, lastPlayedTime);
        collection.replaceOne(Filters.eq("_id", uuid), user);
    }

    public void shutdown() {
        if (this.client != null) {
            this.client.close();
        }
    }
}
