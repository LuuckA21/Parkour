package me.luucka.parkour.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

/**
 * MMColor class help you with Kyori adventure {@link Component}
 */
public final class MMColor {

    private MMColor() {
    }

    /**
     * Generate {@link Component} from a {@link String}
     *
     * @param input {@link String} to convert
     * @return {@link Component} generated from a {@link String}
     */
    public static Component toComponent(final String input) {
        return MiniMessage.miniMessage().deserialize(input);
    }


    /**
     * Generate List of {@link Component} from a List of {@link String}
     *
     * @param input list of {@link String} to convert
     * @return List of {@link Component} generated from a List of {@link String}
     */
    public static List<Component> toComponent(final List<String> input) {
        return input.stream().map(MMColor::toComponent).toList();
    }

    /**
     * Generate List of {@link Component} from a List of {@link String}
     *
     * @param input list of {@link String} to convert
     * @return List of {@link Component} generated from a List of {@link String}
     */
    public static List<Component> toComponent(final String... input) {
        return toComponent(List.of(input));
    }

    /**
     * Generate a Legacy {@link String} from a {@link Component}
     *
     * @param input {@link Component} to convert
     * @return {@link String} generated from a {@link Component}
     */
    public static String toLegacy(final Component input) {
        return LegacyComponentSerializer.legacySection().serialize(input);
    }

    /**
     * Generate {@link String} from a {@link Component}
     *
     * @param input {@link Component} to convert
     * @return {@link String} generated from a {@link Component}
     */
    public static String toMMString(final Component input) {
        return MiniMessage.miniMessage().serialize(input);
    }

    /**
     * Generate List of {@link String} from a List of {@link Component}
     *
     * @param input list of {@link Component} to convert
     * @return List of {@link String} generated from a List of {@link Component}
     */
    public static List<String> toMMString(final List<Component> input) {
        return input.stream().map(MMColor::toMMString).toList();
    }

}
