package me.luucka.parkour.config.serializers;

import me.luucka.parkour.config.entities.LazyItem;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class LazyItemTypeSerializer implements TypeSerializer<LazyItem> {

    @Override
    public LazyItem deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new LazyItem(
                node.node("material").getString(),
                node.node("name").getString(),
                node.node("lore").getList(String.class)
        );
    }

    @Override
    public void serialize(Type type, @Nullable LazyItem item, ConfigurationNode node) throws SerializationException {
        node.node("material").set(String.class, item.material());
        node.node("name").set(String.class, item.name());
        node.node("lore").setList(String.class, item.lore());
    }
}
