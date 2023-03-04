package me.luucka.parkour.commands;

import me.luucka.helplib.commands.BaseCommand;
import me.luucka.helplib.commands.CommandSource;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entities.Parkour;
import me.luucka.parkour.entities.SetupParkour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PAdminCommand extends BaseCommand {

    private final ParkourPlugin plugin;

    public PAdminCommand(final ParkourPlugin plugin) {
        super("padmin", "Parkour admin command", "parkour.admin");
        this.plugin = plugin;
        this.setUsage("/padmin < setup | delete | reload > [parkour]");
    }

    @Override
    public void execute(CommandSource sender, String[] args) throws Exception {
        if (!sender.isPlayer()) throw new Exception(plugin.getMessages().noConsole());

        if (!testPermissionSilent(sender.getSender())) throw new Exception(plugin.getMessages().noPermission());

        if (args.length < 1) throw new Exception(plugin.getMessages().commandUsage(getUsage()));

        final CommandType cmd;

        try {
            cmd = CommandType.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new Exception(plugin.getMessages().commandUsage(getUsage()));
        }

        switch (cmd) {
            case SETUP -> {
                if (args.length < cmd.argsNeeded)
                    throw new Exception(plugin.getMessages().commandUsage("/padmin setup <parkour>"));
                final String parkourName = args[1].toLowerCase();
                if (plugin.getGameManager().isPlayerInParkourGame(sender.getPlayer()))
                    throw new Exception(plugin.getMessages().joinDuringParkour());
                if (plugin.getSetupManager().isPlayerInSetupMode(sender.getPlayer()))
                    throw new Exception(plugin.getMessages().alreadyInSetup());

                final Optional<Parkour> optionalParkour = plugin.getDataManager().getPlayableParkour(parkourName);
                plugin.getSetupManager().addPlayerToSetupMode(sender.getPlayer(), optionalParkour.map(parkour -> new SetupParkour(plugin, parkour)).orElseGet(() -> new SetupParkour(plugin, parkourName)));
                sender.sendMessage(plugin.getMessages().enterSetupMode(parkourName));
            }
            case DELETE -> {
                if (args.length < cmd.argsNeeded)
                    throw new Exception(plugin.getMessages().commandUsage("/padmin delete <parkour>"));
                final String parkourName = args[1].toLowerCase();
                Optional<Parkour> optionalParkour = plugin.getDataManager().getParkour(parkourName);
                if (optionalParkour.isEmpty()) throw new Exception(plugin.getMessages().notExists(args[1]));
                plugin.getDataManager().delete(optionalParkour.get());
                sender.sendMessage(plugin.getMessages().deleteParkour(parkourName));
            }
            case RELOAD -> {
                plugin.reload();
                sender.sendMessage(plugin.getMessages().reload());
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
            return plugin.getDataManager().getAllParkoursName();
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
    }
}
