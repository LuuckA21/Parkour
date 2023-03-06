package me.luucka.parkour.config.serializers;

import me.luucka.parkour.config.entities.LazyItem;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static me.luucka.parkour.utils.MMColor.toComponent;
import static me.luucka.parkour.utils.MMColor.toMMString;

public class LazyItemTypeSerializer implements TypeSerializer<LazyItem> {

    @Override
    public LazyItem deserialize(Type type, ConfigurationNode node) throws SerializationException {

        Material material;
        try {
            material = Material.valueOf(node.node("material").getString("BARRIER").toUpperCase());
        } catch (final IllegalArgumentException e) {
            material = Material.BARRIER;
        }

        return new LazyItem(
                material,
                toComponent(node.node("name").getString()),
                toComponent(node.node("lore").getList(String.class, new ArrayList<>()))
        );
    }

    @Override
    public void serialize(Type type, @Nullable LazyItem item, ConfigurationNode node) throws SerializationException {
        node.node("material").set(String.class, item.material().name());
        node.node("name").set(String.class, toMMString(item.name()));
        node.node("lore").setList(String.class, toMMString(item.lore()));
    }
}
