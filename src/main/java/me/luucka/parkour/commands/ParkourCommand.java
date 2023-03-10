package me.luucka.parkour.commands;

import me.luucka.parkour.Messages;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.Settings;
import me.luucka.parkour.managers.DataManager;
import me.luucka.parkour.managers.GameManager;
import me.luucka.parkour.managers.PlayerDataManager;
import me.luucka.parkour.managers.SetupManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParkourCommand extends BaseCommand {

    private final GameManager gameManager;
    private final DataManager dataManager;
    private final SetupManager setupManager;
    private final Messages messages;
    private final Settings settings;
    private final PlayerDataManager playerDataManager;

    public ParkourCommand(ParkourPlugin plugin) {
        super("parkour", "Join or Quit parkour");
        this.gameManager = plugin.getGameManager();
        this.dataManager = plugin.getDataManager();
        this.setupManager = plugin.getSetupManager();
        this.messages = plugin.getMessages();
        this.settings = plugin.getSettings();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.setUsage("/parkour < join | leave > [parkour]");
    }

    @Override
    public void execute(CommandSource sender, String[] args) throws Exception {
        if (!sender.isPlayer()) throw new Exception(messages.noConsole());
        if (args.length < CommandType.getMinArgsNeeded()) throw new Exception(messages.commandUsage(getUsage()));

        final CommandType cmd;
        try {
            cmd = CommandType.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new Exception(messages.commandUsage(getUsage()));
        }

        final Player player = sender.getPlayer();

        switch (cmd) {
            case JOIN -> {
                if (args.length < cmd.argsNeeded)
                    throw new Exception(messages.commandUsage("/parkour join <parkour>"));

                final String parkourName = args[1].toLowerCase();

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
                                    throw new RuntimeException(messages.waitBeforeJoin(parkour.getName(), nextPlayableTime - now));
                                }
                            }
                            return parkour;
                        }).orElseThrow(() -> {
                            throw new RuntimeException(messages.notExists(parkourName));
                        })
                );
            }
            case LEAVE -> {
                if (!gameManager.isPlayerInParkourSession(player)) throw new Exception(messages.notInParkour());
                gameManager.playerQuit(player, false);
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
            if (settings.isPerParkourPermission()) {
                final List<String> options = new ArrayList<>();
                for (final String parkour : dataManager.getAllParkoursName()) {
                    if (sender.hasPermission("parkour.join." + parkour)) {
                        options.add(parkour);
                    }
                }
                return options;
            }
            return dataManager.getAllParkoursName();
        }

        return Collections.emptyList();
    }

    private enum CommandType {
        JOIN(2),
        LEAVE(1);

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
