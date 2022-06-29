package me.luucka.parkour.config.serializers;

import me.luucka.parkour.config.entities.LazyLocation;
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

        return new LazyLocation(
                world,
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble()
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
    }
}
