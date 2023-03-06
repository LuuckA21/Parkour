package me.luucka.parkour.commands;

import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entities.SetupParkour;
import me.luucka.parkour.managers.DataManager;
import me.luucka.parkour.managers.GameManager;
import me.luucka.parkour.managers.SetupManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.luucka.parkour.utils.MMColor.toComponent;


public class PAdminCommand extends BaseCommand {

    private final ParkourPlugin plugin;
    private final GameManager gameManager;
    private final DataManager dataManager;
    private final SetupManager setupManager;
    private final Messages messages;

    public PAdminCommand(final ParkourPlugin plugin) {
        super("padmin", "Parkour admin command", "parkour.admin");
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.dataManager = plugin.getDataManager();
        this.setupManager = plugin.getSetupManager();
        this.messages = plugin.getMessages();
        this.setUsage("/padmin < setup | delete | reload > [parkour]");
    }

    @Override
    public void execute(CommandSource sender, String[] args) throws Exception {
        if (!sender.isPlayer()) throw new Exception(messages.noConsole());
        if (!testPermissionSilent(sender.getSender())) throw new Exception(messages.noPermission());
        if (args.length < CommandType.getMinArgsNeeded()) throw new Exception(messages.commandUsage(getUsage()));

        final CommandType cmd;
        try {
            cmd = CommandType.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new Exception(messages.commandUsage(getUsage()));
        }

        final Player player = sender.getPlayer();

        switch (cmd) {
            case SETUP -> {
                if (args.length < cmd.argsNeeded) throw new Exception(messages.commandUsage("/padmin setup <parkour>"));

                final String parkourName = args[1].toLowerCase();
                if (gameManager.isPlayerInGame(player))
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
                player.sendMessage(toComponent(messages.enterSetupMode(parkourName)));
            }
            case DELETE -> {
                if (args.length < cmd.argsNeeded)
                    throw new Exception(messages.commandUsage("/padmin delete <parkour>"));

                final String parkourName = args[1].toLowerCase();
                dataManager.delete(dataManager.getParkour(parkourName).orElseThrow(() -> {
                    throw new RuntimeException(messages.notExists(args[1]));
                }));
                player.sendMessage(toComponent(messages.deleteParkour(parkourName)));
            }
            case RELOAD -> {
                plugin.reload();
                player.sendMessage(toComponent(messages.reload()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSource sender, String[] args) {
        if (!testPermissionSilent(sender.getSender())) return Collections.emptyList();

        if (args.length == 1) {
            final List<String> options = new ArrayList<>();
            for (final CommandType ct : CommandType.values()) {
                options.add(ct.name().toLowerCase());
            }
            return options;
        } else if (args.length == 2) {
            return dataManager.getAllParkoursName();
        }

        return Collections.emptyList();
    }

    private enum CommandType {
        SETUP(2),
        DELETE(2),
        RELOAD(1);

        private final int argsNeeded;

        CommandType(int argsNeeded) {
            this.argsNeeded = argsNeeded;
        }

        public static int getMinArgsNeeded() {
            int min = values()[0].argsNeeded;
            for (CommandType ct : values()) {
                if (ct.argsNeeded < min) min = ct.argsNeeded;
            }
            return min;
        }
    }
}
