package me.luucka.parkour.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.Settings;
import me.luucka.parkour.managers.DataManager;
import me.luucka.parkour.managers.GameManager;
import me.luucka.parkour.managers.PlayerDataManager;
import me.luucka.parkour.managers.SetupManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ParkourCommand {

    private final GameManager gameManager;
    private final DataManager dataManager;
    private final SetupManager setupManager;
    private final Messages messages;
    private final Settings settings;
    private final PlayerDataManager playerDataManager;

    public ParkourCommand(ParkourPlugin plugin) {
        this.gameManager = plugin.getGameManager();
        this.dataManager = plugin.getDataManager();
        this.setupManager = plugin.getSetupManager();
        this.messages = plugin.getMessages();
        this.settings = plugin.getSettings();
        this.playerDataManager = plugin.getPlayerDataManager();
        register();
    }

    private void register() {
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(new StringArgument("parkour").replaceSuggestions(ArgumentSuggestions.strings(info -> {
            if (info.sender() instanceof Player player) {
                if (!player.hasPermission("parkour.bypass")) {
                    if (settings.isPerParkourPermission()) {
                        final List<String> options = new ArrayList<>();
                        for (final String parkour : dataManager.getAllParkoursName()) {
                            if (player.hasPermission("parkour.join." + parkour)) {
                                options.add(parkour);
                            }
                        }
                        return options.toArray(new String[0]);
                    }
                }
            }
            return dataManager.getAllParkoursName().toArray(new String[0]);
        })));

        new CommandAPICommand("parkour")
                .withHelp("Command for playing Parkour", "Command for joining and leaving Parkour games")
                .withSubcommand(
                        new CommandAPICommand("join")
                                .withArguments(arguments)
                                .executesPlayer((player, args) -> {
                                    final String parkourName = (String) args.get("parkour");

                                    try {
                                        gameManager.playerJoin(
                                                player,
                                                dataManager.getPlayableParkour(parkourName).map(parkour -> {
                                                    if (setupManager.isPlayerInSetup(player))
                                                        throw new RuntimeException(messages.joinDuringSetup());

                                                    if (gameManager.isPlayerInParkourSession(player))
                                                        throw new RuntimeException(messages.alreadyInParkour());

                                                    if (!player.hasPermission("parkour.bypass")) {
                                                        if (settings.isPerParkourPermission()
                                                                && !player.hasPermission("parkour.join." + parkour.getName())) {
                                                            throw new RuntimeException(messages.noPermission());
                                                        }
                                                        long now = System.currentTimeMillis();
                                                        long nextPlayableTime = playerDataManager.getPlayerParkourData(player.getUniqueId(), parkour).getLastPlayedTime() + (parkour.getCooldown() * 1000L);
                                                        if (now < nextPlayableTime) {
                                                            throw new RuntimeException(messages.parkourWaitJoin(parkour.getName(), nextPlayableTime - now));
                                                        }
                                                    }
                                                    return parkour;
                                                }).orElseThrow(() -> new Exception(messages.notExists(parkourName)))
                                        );
                                    } catch (Exception ex) {
                                        player.sendRichMessage(ex.getMessage());
                                    }

                                })
                )
                .withSubcommand(
                        new CommandAPICommand("leave")
                                .executesPlayer((player, args) -> {
                                    try {
                                        if (!gameManager.isPlayerInParkourSession(player))
                                            throw new Exception(messages.notInParkour());
                                        gameManager.playerQuit(player, false);
                                    } catch (Exception ex) {
                                        player.sendRichMessage(ex.getMessage());
                                    }
                                })
                )
                .register();
    }
}
