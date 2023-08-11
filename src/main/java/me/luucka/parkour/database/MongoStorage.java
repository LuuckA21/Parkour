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
import me.luucka.parkour.database.model.PlayerParkourData;
import me.luucka.parkour.database.model.User;
import me.luucka.parkour.model.Parkour;
import me.luucka.parkour.setting.Setting;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MongoStorage {

    private final Setting setting;
    private MongoClient client;
    private MongoDatabase database;

    public MongoStorage(final ParkourPlugin plugin) {
        this.setting = plugin.getSetting();
    }

    public void init() {
        ConnectionString connectionString = new ConnectionString(setting.getMongoDbConnectionUri());
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

    public PlayerParkourData getPlayerParkourData(final UUID uuid, final Parkour parkour) {
        MongoCollection<User> collection = database.getCollection("playersdata", User.class);
        final User user = collection.find(Filters.eq("_id", uuid)).first();
        if (user == null) return new PlayerParkourData(-1L, -1, -1L);
        return user.getPlayerParkourData().getOrDefault(parkour.getName(), new PlayerParkourData(-1L, -1, -1L));
    }

    public void updateParkourData(final UUID uuid, final String parkour, final PlayerParkourData parkourData) {
        MongoCollection<User> collection = database.getCollection("playersdata", User.class);
        final User user = collection.find(Filters.eq("_id", uuid)).first();
        if (user == null) return;
        user.updateParkourData(parkour, parkourData);
        collection.replaceOne(Filters.eq("_id", uuid), user);
    }

    public void shutdown() {
        if (this.client != null) {
            this.client.close();
        }
    }
}
