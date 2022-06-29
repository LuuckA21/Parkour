package me.luucka.parkour.commands;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.exceptions.InsufficientPermissionException;
import me.luucka.parkour.exceptions.NotEnoughArgumentsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        if (!testPermissionSilent(sender.getSender())) throw new InsufficientPermissionException(plugin.getMessages().noPermission());

        if (args.length < 1) throw new NotEnoughArgumentsException(plugin.getMessages().commandUsage(getUsage()));

        final CommandType cmd;

        try {
            cmd = CommandType.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new Exception(plugin.getMessages().commandUsage(getUsage()));
        }

        switch (cmd) {
            case SETUP -> {
                if (args.length < cmd.argsNeeded) throw new NotEnoughArgumentsException(plugin.getMessages().commandUsage("/padmin setup <parkour>"));
                if (plugin.getParkourGameManager().isPlayerInParkour(sender.getPlayer())) throw new Exception(plugin.getMessages().joinDuringParkour());
                if (plugin.getParkourSetupManager().isInSetupMode(sender.getPlayer())) throw new Exception(plugin.getMessages().alreadyInSetup());

                plugin.getParkourSetupManager().addToSetup(sender.getPlayer(), args[1].toLowerCase());
                sender.sendMessage(plugin.getMessages().enterSetupMode(args[1]));
            }
            case DELETE -> {
                if (args.length < cmd.argsNeeded) throw new NotEnoughArgumentsException(plugin.getMessages().commandUsage("/padmin delete <parkour>"));
                if (!plugin.getParkourDataManager().exists(args[1].toLowerCase())) throw new Exception(plugin.getMessages().notExists(args[1]));
                plugin.getParkourDataManager().delete(args[1].toLowerCase());
                sender.sendMessage(plugin.getMessages().deleteParkour(args[1]));
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
            return plugin.getParkourDataManager().getAllParkoursName();
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
