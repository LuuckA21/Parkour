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
        this.setUsage("/parkour < join | quit > <parkour>");
    }

    @Override
    public void execute(CommandSource sender, String[] args) throws Exception {
        if (!sender.isPlayer()) throw new Exception(plugin.getMessages().noConsole());

        if (args.length < 2) throw new NotEnoughArgumentsException(plugin.getMessages().commandUsage(getUsage()));

        final CommandType cmd;
        final String parkourName = args[1];

        try {
            cmd = CommandType.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new Exception(plugin.getMessages().commandUsage(getUsage()));
        }

        if (!plugin.getParkourDataManager().exists(parkourName)) throw new Exception(plugin.getMessages().notExists(parkourName));

        if (plugin.getSettings().isPerParkourPermission() && !sender.hasPermission("parkour." + parkourName)) throw new InsufficientPermissionException(plugin.getMessages().noPermission());

        switch (cmd) {
            case JOIN -> {
                if (plugin.getParkourSetupManager().isInSetupMode(sender.getPlayer())) throw new Exception(plugin.getMessages().joinDuringSetup());
                if (plugin.getParkourGameManager().isPlayerInParkour(sender.getPlayer())) throw new Exception(plugin.getMessages().alreadyInParkour());
                plugin.getParkourGameManager().playerJoin(sender.getPlayer(), parkourName);
                sender.sendMessage(plugin.getMessages().joinParkour(parkourName));
            }
            case QUIT -> {
                if (!plugin.getParkourGameManager().isPlayerInParkour(sender.getPlayer())) throw new Exception(plugin.getMessages().notInParkour());
                plugin.getParkourGameManager().playerQuit(sender.getPlayer(), false);
                sender.sendMessage(plugin.getMessages().quitParkour(parkourName));
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
        }else if (args.length == 2) {
            if (plugin.getSettings().isPerParkourPermission()) {
                final List<String> options = new ArrayList<>();
                for (final String parkour : plugin.getParkourDataManager().getAllParkoursName()) {
                    if (sender.hasPermission("parkour." + parkour)) {
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
        JOIN,
        QUIT
    }
}
