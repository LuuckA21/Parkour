package me.luucka.parkour.command;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.manager.DataManager;
import me.luucka.parkour.manager.GameManager;
import me.luucka.parkour.manager.SetupManager;
import me.luucka.parkour.model.Parkour;
import me.luucka.parkour.model.SetupParkour;
import me.luucka.parkour.setting.Lobby;
import me.luucka.parkour.setting.Messages;

import static me.luucka.extendlibrary.util.MMColor.toComponent;

public final class PAdminCommand {

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

    public void register() {
        CommandAPICommand padminCommand = new CommandAPICommand("padmin")
                .withHelp("Parkour plugin Main command", "Parkour plugin Main command")
                .withPermission("parkour.admin")
                .withSubcommand(
                        new CommandAPICommand("setup")
                                .withUsage("/padmin setup <parkour>")
                                .withShortDescription("Join setup-mode for a parkour.")
                                .withArguments(ParkourArgument.setupParkourArgument("parkour")
                                        .replaceSuggestions(ArgumentSuggestions.strings(info -> dataManager.getAllParkoursName().toArray(new String[0])))
                                )
                                .executesPlayer((player, args) -> {
                                    final SetupParkour setupParkour = (SetupParkour) args.get("parkour");

                                    if (gameManager.isPlayerInParkourSession(player)) {
                                        throw CommandAPIBukkit.failWithAdventureComponent(toComponent(messages.joinDuringParkour()));
                                    }
                                    if (setupManager.isPlayerInSetup(player)) {
                                        throw CommandAPIBukkit.failWithAdventureComponent(toComponent(messages.alreadyInSetup()));
                                    }

                                    setupManager.playerJoin(player, setupParkour);
                                    player.sendRichMessage(messages.setupEnterMode(setupParkour.getName()));
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("delete")
                                .withUsage("/padmin delete <parkour>")
                                .withShortDescription("Delete a parkour.")
                                .withArguments(ParkourArgument.parkourArgument("parkour", false)
                                        .replaceSuggestions(ArgumentSuggestions.strings(info -> dataManager.getAllParkoursName().toArray(new String[0])))
                                )
                                .executesPlayer((player, args) -> {
                                    final Parkour parkour = (Parkour) args.get("parkour");
                                    dataManager.delete(parkour);
                                    player.sendRichMessage(messages.deleteParkour(parkour.getName()));
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("lobby")
                                .withUsage("/padmin lobby")
                                .withShortDescription("Set main lobby location.")
                                .executesPlayer((player, args) -> {
                                    lobby.setLobbyLocation(player.getLocation());
                                    player.sendRichMessage(messages.setLobby());
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("reload")
                                .withUsage("/padmin reload")
                                .withShortDescription("Reload plugin.")
                                .executesPlayer((player, args) -> {
                                    plugin.reload();
                                    player.sendRichMessage(messages.reloadPlugin());
                                })
                );

        padminCommand.withUsage(
                padminCommand.getSubcommands().stream().map(command -> command.getUsage()[0] + " - " + command.getShortDescription()).toArray(String[]::new)
        ).register();
    }
}
