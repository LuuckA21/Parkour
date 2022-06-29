package me.luucka.parkour.utils;

import org.bukkit.Material;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Set;

public class MaterialUtil {

    private static final Set<Material> WALL_SIGNS;

    static {
        WALL_SIGNS = getAllMatching(Material.class, "WALL_SIGN",
                "ACACIA_WALL_SIGN", "BIRCH_WALL_SIGN",
                "DARK_OAK_WALL_SIGN", "JUNGLE_WALL_SIGN",
                "OAK_WALL_SIGN", "SPRUCE_WALL_SIGN",
                "CRIMSON_WALL_SIGN", "WARPED_WALL_SIGN",
                "MANGROVE_WALL_SIGN");
    }

    public static boolean isWallSign(final Material material) {
        return WALL_SIGNS.contains(material);
    }

    public static <T extends Enum<T>> Set<T> getAllMatching(final Class<T> enumClass, final String... names) {
        final Set<T> set = EnumSet.noneOf(enumClass);

        for (final String name : names) {
            try {
                final Field enumField = enumClass.getDeclaredField(name);

                if (enumField.isEnumConstant()) {
                    set.add((T) enumField.get(null));
                }
            } catch (final NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        return set;
    }

}
