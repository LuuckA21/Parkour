package me.luucka.parkour.commands;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.exceptions.InsufficientPermissionException;
import me.luucka.parkour.exceptions.NotEnoughArgumentsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParkourCommand extends BaseCommand {

    private final ParkourPlugin plugin;

    public ParkourCommand(ParkourPlugin plugin) {
        super("parkour", "Join or Quit parkour");
        this.plugin = plugin;
        this.setUsage("/parkour < join | quit > [parkour]");
    }

    @Override
    public void execute(CommandSource sender, String[] args) throws Exception {
        if (!sender.isPlayer()) throw new Exception(plugin.getMessages().noConsole());

        if (args.length < 1) throw new NotEnoughArgumentsException(plugin.getMessages().commandUsage(getUsage()));

        final CommandType cmd;

        try {
            cmd = CommandType.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new Exception(plugin.getMessages().commandUsage(getUsage()));
        }

        switch (cmd) {
            case JOIN -> {
                if (args.length < cmd.argsNeeded)
                    throw new NotEnoughArgumentsException(plugin.getMessages().commandUsage("/parkour join <parkour>"));

                if (plugin.getParkourSetupManager().isInSetupMode(sender.getPlayer()))
                    throw new Exception(plugin.getMessages().joinDuringSetup());
                if (plugin.getParkourGameManager().isPlayerInParkour(sender.getPlayer()))
                    throw new Exception(plugin.getMessages().alreadyInParkour());

                final String parkourName = args[1].toLowerCase();

                if (!plugin.getParkourDataManager().exists(parkourName))
                    throw new Exception(plugin.getMessages().notExists(parkourName));

                if (!sender.hasPermission("parkour.bypass")) {
                    if (plugin.getSettings().isPerParkourPermission()
                            && !sender.hasPermission("parkour.join." + parkourName)) {
                        throw new InsufficientPermissionException(plugin.getMessages().noPermission());
                    }
                }

                plugin.getParkourGameManager().playerJoin(sender.getPlayer(), parkourName);
            }
            case QUIT -> {
                if (!plugin.getParkourGameManager().isPlayerInParkour(sender.getPlayer()))
                    throw new Exception(plugin.getMessages().notInParkour());
                plugin.getParkourGameManager().playerQuit(sender.getPlayer(), false);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length == 1) {
            final List<String> options = new ArrayList<>();
            for (final CommandType ct : CommandType.values()) {
                options.add(ct.name().toLowerCase());
            }
            return options;
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("quit")) {
            if (plugin.getSettings().isPerParkourPermission()) {
                final List<String> options = new ArrayList<>();
                for (final String parkour : plugin.getParkourDataManager().getAllParkoursName()) {
                    if (sender.hasPermission("parkour.join." + parkour)) {
                        options.add(parkour);
                    }
                }
                return options;
            }
            return plugin.getParkourDataManager().getAllParkoursName();
        }

        return Collections.emptyList();
    }

    private enum CommandType {
        JOIN(2),
        QUIT(1);

        private final int argsNeeded;

        CommandType(int argsNeeded) {
            this.argsNeeded = argsNeeded;
        }
    }
}
