package me.luucka.parkour.config.serializer;

import me.luucka.parkour.config.entity.LazyLocation;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class LocationTypeSerializer implements TypeSerializer<LazyLocation> {

    @Override
    public LazyLocation deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final String world = node.node("world").getString();
        if (world == null || world.isEmpty()) {
            throw new SerializationException("No world value present");
        }

        final float yaw = node.node("yaw").getFloat(0.0F);
        final float pitch = node.node("pitch").getFloat(0.0F);

        return new LazyLocation(
                world,
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble(),
                yaw,
                pitch
        );
    }

    @Override
    public void serialize(Type type, @Nullable LazyLocation value, ConfigurationNode node) throws SerializationException {
        if (value == null || value.world() == null) {
            node.raw(null);
            return;
        }

        node.node("world").set(String.class, value.world());
        node.node("x").set(Double.class, value.x());
        node.node("y").set(Double.class, value.y());
        node.node("z").set(Double.class, value.z());
        if (value.yaw() != 0F) node.node("yaw").set(Float.class, value.yaw());
        if (value.pitch() != 0F) node.node("pitch").set(Float.class, value.pitch());
    }
}
