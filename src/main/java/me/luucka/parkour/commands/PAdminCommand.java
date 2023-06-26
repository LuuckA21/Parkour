package me.luucka.parkour.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import me.luucka.parkour.Lobby;
import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entities.SetupParkour;
import me.luucka.parkour.managers.DataManager;
import me.luucka.parkour.managers.GameManager;
import me.luucka.parkour.managers.SetupManager;

import java.util.ArrayList;
import java.util.List;

public class PAdminCommand {

    private final ParkourPlugin plugin;
    private final GameManager gameManager;
    private final DataManager dataManager;
    private final SetupManager setupManager;
    private final Messages messages;
    private final Lobby lobby;

    public PAdminCommand(final ParkourPlugin plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.dataManager = plugin.getDataManager();
        this.setupManager = plugin.getSetupManager();
        this.messages = plugin.getMessages();
        this.lobby = plugin.getLobby();
        register();
    }

    private void register() {
        List<Argument<?>> arguments = new ArrayList<>();
        arguments.add(new StringArgument("parkour").replaceSuggestions(ArgumentSuggestions.strings(dataManager.getAllParkoursName())));

        new CommandAPICommand("padmin")
                .withHelp("Parkour plugin Main command", "Parkour plugin Main command")
                .withPermission("parkour.admin")
                .withSubcommand(
                        new CommandAPICommand("setup")
                                .withArguments(arguments)
                                .executesPlayer((player, args) -> {
                                    final String parkourName = (String) args.get("parkour");

                                    try {
                                        if (gameManager.isPlayerInParkourSession(player))
                                            throw new Exception(messages.joinDuringParkour());
                                        if (setupManager.isPlayerInSetup(player))
                                            throw new Exception(messages.alreadyInSetup());

                                        setupManager.playerJoin(
                                                player,
                                                dataManager.getPlayableParkour(parkourName).map(
                                                        parkour -> new SetupParkour(plugin, parkour)
                                                ).orElseGet(
                                                        () -> new SetupParkour(plugin, parkourName)
                                                )
                                        );
                                        player.sendRichMessage(messages.setupEnterMode(parkourName));
                                    } catch (Exception ex) {
                                        player.sendRichMessage(ex.getMessage());
                                    }
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("delete")
                                .withArguments(arguments)
                                .executesPlayer((player, args) -> {
                                    final String parkourName = (String) args.get("parkour");
                                    try {
                                        dataManager.delete(dataManager.getParkour(parkourName).orElseThrow(() -> new Exception(messages.notExists(parkourName))));
                                        player.sendRichMessage(messages.deleteParkour(parkourName));
                                    } catch (Exception ex) {
                                        player.sendRichMessage(ex.getMessage());
                                    }
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("lobby")
                                .executesPlayer((player, args) -> {
                                    this.lobby.setLobbyLocation(player.getLocation());
                                    player.sendRichMessage(messages.setLobby());
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("reload")
                                .executesPlayer((player, args) -> {
                                    plugin.reload();
                                    player.sendRichMessage(messages.reload());
                                })
                )
                .register();
    }
}
