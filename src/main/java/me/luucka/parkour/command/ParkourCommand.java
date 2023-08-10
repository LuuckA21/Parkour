package me.luucka.parkour.command;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.manager.DataManager;
import me.luucka.parkour.manager.GameManager;
import me.luucka.parkour.manager.PlayerDataManager;
import me.luucka.parkour.manager.SetupManager;
import me.luucka.parkour.model.Parkour;
import me.luucka.parkour.setting.Messages;
import me.luucka.parkour.setting.Settings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.luucka.extendlibrary.util.MMColor.toComponent;

public final class ParkourCommand {

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

    public void register() {
        CommandAPICommand parkourCommand = new CommandAPICommand("parkour")
                .withHelp("Command for playing Parkour", "Command for joining and leaving Parkour games")
                .withSubcommand(
                        new CommandAPICommand("join")
                                .withUsage("/parkour join <parkour>")
                                .withShortDescription("Join a parkour.")
                                .withArguments(ParkourArgument.parkourArgument("parkour", true)
                                        .replaceSuggestions(ArgumentSuggestions.strings(info -> {
                                            if (info.sender() instanceof Player player) {
                                                if (settings.isPerParkourPermission() && !player.hasPermission("parkour.bypass")) {
                                                    final List<String> options = new ArrayList<>();
                                                    for (final String parkour : dataManager.getAllParkoursName()) {
                                                        if (player.hasPermission("parkour.join." + parkour)) {
                                                            options.add(parkour);
                                                        }
                                                    }
                                                    return options.toArray(new String[0]);
                                                }
                                            }
                                            return dataManager.getAllParkoursName().toArray(new String[0]);
                                        }))
                                )
                                .executesPlayer((player, args) -> {
                                    final Parkour parkour = (Parkour) args.get("parkour");

                                    if (setupManager.isPlayerInSetup(player)) {
                                        throw CommandAPIBukkit.failWithAdventureComponent(toComponent(messages.joinDuringSetup()));
                                    }
                                    if (gameManager.isPlayerInParkourSession(player)) {
                                        throw CommandAPIBukkit.failWithAdventureComponent(toComponent(messages.alreadyInParkour()));
                                    }
                                    if (settings.isPerParkourPermission() && !player.hasPermission("parkour.bypass")) {
                                        throw CommandAPIBukkit.failWithAdventureComponent(toComponent(messages.noPermission()));
                                    }
                                    long now = System.currentTimeMillis();
                                    long nextPlayableTime = playerDataManager.getPlayerParkourData(player.getUniqueId(), parkour).getLastPlayedTime() + (parkour.getCooldown() * 1000L);
                                    if (now < nextPlayableTime) {
                                        throw CommandAPIBukkit.failWithAdventureComponent(toComponent(messages.parkourWaitJoin(parkour.getName(), nextPlayableTime - now)));
                                    }

                                    gameManager.playerJoin(player, parkour);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("leave")
                                .withUsage("/parkour leave")
                                .withShortDescription("Leave parkour session!")
                                .executesPlayer((player, args) -> {
                                    if (!gameManager.isPlayerInParkourSession(player)) {
                                        throw CommandAPIBukkit.failWithAdventureComponent(toComponent(messages.notInParkour()));
                                    }
                                    gameManager.playerQuit(player, false);
                                })
                );

        parkourCommand.withUsage(
                parkourCommand.getSubcommands().stream().map(command -> command.getUsage()[0] + " - " + command.getShortDescription()).toArray(String[]::new)
        ).register();
    }
}
