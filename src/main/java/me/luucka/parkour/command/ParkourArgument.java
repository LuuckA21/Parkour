package me.luucka.parkour.command;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.luucka.extendlibrary.message.Message;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.manager.DataManager;
import me.luucka.parkour.model.Parkour;
import me.luucka.parkour.model.SetupParkour;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class ParkourArgument {

    private static ParkourPlugin plugin;

    private static DataManager dataManager;

    private static Message messages;

    private ParkourArgument() {
    }

    public static void set(ParkourPlugin plugin) {
        ParkourArgument.plugin = plugin;
        ParkourArgument.dataManager = plugin.getDataManager();
        ParkourArgument.messages = plugin.getMessages();
    }

    public static Argument<Parkour> parkourArgument(String nodeName, boolean playable) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            if (info.sender() instanceof Player) {
                Optional<Parkour> parkour = playable ? dataManager.getPlayableParkour(info.input()) : dataManager.getParkour(info.input());
                if (parkour.isPresent()) {
                    return parkour.get();
                }
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(messages.from("not-exists").with("parkour", info.input()).build());
            }
            throw CustomArgument.CustomArgumentException.fromAdventureComponent(messages.from("no-console").build());
        });
    }

    public static Argument<SetupParkour> setupParkourArgument(String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            if (info.sender() instanceof Player) {
                Optional<Parkour> parkour = dataManager.getPlayableParkour(info.input());
                return parkour.map(value -> new SetupParkour(plugin, value)).orElseGet(() -> new SetupParkour(plugin, info.input()));
            }
            throw CustomArgument.CustomArgumentException.fromAdventureComponent(messages.from("no-console").build());
        });
    }

}
