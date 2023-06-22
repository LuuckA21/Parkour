package me.luucka.parkour.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSource {

    @Getter
    @Setter
    private CommandSender sender;

    public CommandSource(final CommandSender sender) {
        this.sender = sender;
    }

    public Player getPlayer() {
        if (sender instanceof Player player) {
            return player;
        }
        return null;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public void sendMessage(final String message) {
        if (!message.isEmpty()) sender.sendRichMessage(message);
    }

    public boolean hasPermission(final String permission) {
        return sender.hasPermission(permission);
    }
}
