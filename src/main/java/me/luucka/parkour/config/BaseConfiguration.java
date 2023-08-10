package me.luucka.parkour.config;

import me.luucka.parkour.config.model.LazyItem;
import me.luucka.parkour.config.model.LazyLocation;
import me.luucka.parkour.config.serializer.LazyItemTypeSerializer;
import me.luucka.parkour.config.serializer.LocationTypeSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.luucka.extendlibrary.util.MMColor.toComponent;

public final class BaseConfiguration {

    private static final Logger LOGGER = Logger.getLogger("Parkour");

    private Class<?> resourceClass = BaseConfiguration.class;
    private final File configFile;
    private final YamlConfigurationLoader loader;
    private final String templateName;
    private CommentedConfigurationNode configurationNode;

    public BaseConfiguration(final File configFile) {
        this(configFile, null);
    }

    public BaseConfiguration(final File configFile, final String templateName, final Class<?> resourceClass) {
        this(configFile, templateName);
        this.resourceClass = resourceClass;
    }

    public BaseConfiguration(final File configFile, final String templateName) {
        this.configFile = configFile;
        this.loader = YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> {
                    build.register(LazyLocation.class, new LocationTypeSerializer());
                    build.register(LazyItem.class, new LazyItemTypeSerializer());
                }))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .file(configFile)
                .build();
        this.templateName = templateName;
    }

    public CommentedConfigurationNode getRootNode() {
        return configurationNode;
    }

    public File getFile() {
        return configFile;
    }

//    ----- Location ---------------------------------------------------------------------------------------------------

    public void setProperty(String path, final Location location) {
        setInternal(path, LazyLocation.fromLocation(location));
    }

    public LazyLocation getLocation(final String path) {
        final CommentedConfigurationNode node = path == null ? getRootNode() : getSection(path);
        if (node == null) return null;
        try {
            return node.get(LazyLocation.class);
        } catch (SerializationException e) {
            return null;
        }
    }

    public Map<String, LazyLocation> getLocationSectionMap(final String path) {
        final CommentedConfigurationNode node = getSection(path);
        final Map<String, LazyLocation> result = new HashMap<>();
        for (final Map.Entry<String, CommentedConfigurationNode> entry : ConfigurateUtil.getMap(node).entrySet()) {
            final CommentedConfigurationNode jailNode = entry.getValue();
            try {
                result.put(entry.getKey().toLowerCase(Locale.ENGLISH), jailNode.get(LazyLocation.class));
            } catch (SerializationException e) {
                LOGGER.log(Level.WARNING, "Error serializing key " + entry.getKey(), e);
            }
        }
        return result;
    }

    //    ----- Item -------------------------------------------------------------------------------------------------------

    public LazyItem getItem(final String path) {
        final CommentedConfigurationNode node = path == null ? getRootNode() : getSection(path);
        if (node == null) return new LazyItem(Material.BARRIER, toComponent("name"), toComponent(List.of("lore")));
        try {
            return node.get(LazyItem.class);
        } catch (SerializationException e) {
            return new LazyItem(Material.BARRIER, toComponent("name"), toComponent(List.of("lore")));
        }
    }

//    ----- List -------------------------------------------------------------------------------------------------------

    public void setProperty(final String path, final List<?> list) {
        setInternal(path, list);
    }

    public <T> void setExplicitList(final String path, final List<T> list, final Type type) {
        try {
            toSplitRoot(path, configurationNode).set(type, list);
        } catch (SerializationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public <T> List<T> getList(final String path, Class<T> type) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) {
            return new ArrayList<>();
        }

        try {
            final List<T> list = node.getList(type);
            if (list == null) return new ArrayList<>();
            return list;
        } catch (SerializationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public boolean isList(String path) {
        final CommentedConfigurationNode node = getInternal(path);
        return node != null && node.isList();
    }

//    ----- String -----------------------------------------------------------------------------------------------------

    public void setProperty(final String path, final String value) {
        setInternal(path, value);
    }

    public String getString(final String path, final String def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) return def;
        return node.getString();
    }

//    ----- Boolean ----------------------------------------------------------------------------------------------------

    public void setProperty(final String path, final boolean value) {
        setInternal(path, value);
    }

    public boolean getBoolean(final String path, final boolean def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) return def;
        return node.getBoolean();
    }

    public boolean isBoolean(final String path) {
        final CommentedConfigurationNode node = getInternal(path);
        return node != null && node.raw() instanceof Boolean;
    }

//    ----- Long -------------------------------------------------------------------------------------------------------

    public void setProperty(final String path, final long value) {
        setInternal(path, value);
    }

    public long getLong(final String path, final long def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) return def;
        return node.getLong();
    }

//    ----- Int --------------------------------------------------------------------------------------------------------

    public void setProperty(final String path, final int value) {
        setInternal(path, value);
    }

    public int getInt(final String path, final int def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) return def;
        return node.getInt();
    }

//    ----- Double -----------------------------------------------------------------------------------------------------

    public void setProperty(final String path, final double value) {
        setInternal(path, value);
    }

    public double getDouble(final String path, final double def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) return def;
        return node.getDouble();
    }

//    ----- Float ------------------------------------------------------------------------------------------------------

    public void setProperty(final String path, final float value) {
        setInternal(path, value);
    }

    public float getFloat(final String path, final float def) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node == null) return def;
        return node.getFloat();
    }

//    ----- Raw --------------------------------------------------------------------------------------------------------

    public void setRaw(final String path, final Object value) {
        setInternal(path, value);
    }

    public Object get(final String path) {
        final CommentedConfigurationNode node = getInternal(path);
        return node == null ? null : node.raw();
    }

//    ----- Section ----------------------------------------------------------------------------------------------------

    public CommentedConfigurationNode getSection(final String path) {
        final CommentedConfigurationNode node = toSplitRoot(path, configurationNode);
        if (node.virtual()) return null;
        return node;
    }

    public CommentedConfigurationNode newSection() {
        return loader.createNode();
    }

//    ----- Utils -------------------------------------------------------------------------------------------------------

    public Set<String> getKeys(final String path) {
        final CommentedConfigurationNode configurationNode = getSection(path);
        return ConfigurateUtil.getKeys(configurationNode);
    }

    public Map<String, CommentedConfigurationNode> getMap(final String path) {
        final CommentedConfigurationNode configurationNode = getSection(path);
        return ConfigurateUtil.getMap(configurationNode);
    }

    public void removeProperty(String path) {
        final CommentedConfigurationNode node = getInternal(path);
        if (node != null) {
            try {
                node.set(null);
            } catch (SerializationException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void setInternal(final String path, final Object value) {
        try {
            toSplitRoot(path, configurationNode).set(value);
        } catch (SerializationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private CommentedConfigurationNode getInternal(final String path) {
        final CommentedConfigurationNode node = toSplitRoot(path, configurationNode);
        if (node.virtual()) return null;
        return node;
    }

    public boolean hasProperty(final String path) {
        return !toSplitRoot(path, configurationNode).isNull();
    }

    public CommentedConfigurationNode toSplitRoot(String path, final CommentedConfigurationNode node) {
        if (path == null) return node;

        path = path.startsWith(".") ? path.substring(1) : path;
        return node.node(path.contains(".") ? path.split("\\.") : new Object[]{path});
    }

    public void load() {
        if (configFile.getParentFile() != null && !configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                LOGGER.log(Level.SEVERE, "Failed to create file: ", configFile.toString());
            }
        }

        if (!configFile.exists()) {
            try {
                if (templateName != null) {
                    Files.copy(resourceClass.getResourceAsStream(templateName), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    this.configFile.createNewFile();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to create file " + configFile, e);
            }
        }

        try {
            configurationNode = loader.load();
        } catch (final ParsingException e) {
            final File broken = new File(configFile.getAbsolutePath() + ".broken." + System.currentTimeMillis());
            if (configFile.renameTo(broken)) {
                LOGGER.log(Level.SEVERE, "The file " + configFile + " is broken, it has been renamed to " + broken, e.getCause());
                return;
            }
            LOGGER.log(Level.SEVERE, "The file " + configFile + " is broken. A backup file has failed to be created", e.getCause());
        } catch (final ConfigurateException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (configurationNode == null) {
                configurationNode = loader.createNode();
            }
        }
    }

    public synchronized void save() {
        try {
            loader.save(configurationNode);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

}