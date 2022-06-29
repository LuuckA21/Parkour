package me.luucka.parkour.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

public final class Color {

    private Color() {
    }

    public static Component colorize(final String input) {
        return MiniMessage.miniMessage().deserialize(input);
    }

    public static List<Component> colorize(final List<String> input) {
        List<Component> components = new ArrayList<>();
        for (final String s : input) {
            components.add(colorize(s));
        }
        return components;
    }

}
