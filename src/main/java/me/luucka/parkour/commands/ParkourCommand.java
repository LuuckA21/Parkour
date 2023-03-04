package me.luucka.parkour.commands;

import me.luucka.helplib.commands.BaseCommand;
import me.luucka.helplib.commands.CommandSource;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entities.Parkour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        if (args.length < 1) throw new Exception(plugin.getMessages().commandUsage(getUsage()));

        final CommandType cmd;

        try {
            cmd = CommandType.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new Exception(plugin.getMessages().commandUsage(getUsage()));
        }

        switch (cmd) {
            case JOIN -> {
                if (args.length < cmd.argsNeeded)
                    throw new Exception(plugin.getMessages().commandUsage("/parkour join <parkour>"));

                if (plugin.getSetupManager().isPlayerInSetupMode(sender.getPlayer()))
                    throw new Exception(plugin.getMessages().joinDuringSetup());
                if (plugin.getGameManager().isPlayerInParkourGame(sender.getPlayer()))
                    throw new Exception(plugin.getMessages().alreadyInParkour());

                final String parkourName = args[1].toLowerCase();
                Optional<Parkour> optionalParkour = plugin.getDataManager().getPlayableParkour(parkourName);

                if (optionalParkour.isEmpty())
                    throw new Exception(plugin.getMessages().notExists(parkourName));

                if (!sender.hasPermission("parkour.bypass")) {
                    if (plugin.getSettings().isPerParkourPermission()
                            && !sender.hasPermission("parkour.join." + parkourName)) {
                        throw new Exception(plugin.getMessages().noPermission());
                    }
                }

                plugin.getGameManager().playerJoin(sender.getPlayer(), optionalParkour.get());
            }
            case QUIT -> {
                if (!plugin.getGameManager().isPlayerInParkourGame(sender.getPlayer()))
                    throw new Exception(plugin.getMessages().notInParkour());
                plugin.getGameManager().playerQuit(sender.getPlayer(), false);
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
                for (final String parkour : plugin.getDataManager().getAllParkoursName()) {
                    if (sender.hasPermission("parkour.join." + parkour)) {
                        options.add(parkour);
                    }
                }
                return options;
            }
            return plugin.getDataManager().getAllParkoursName();
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
