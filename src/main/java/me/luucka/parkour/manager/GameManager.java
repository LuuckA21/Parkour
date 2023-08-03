package me.luucka.parkour.manager;

import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.entity.Parkour;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private final ParkourPlugin plugin;
    private final PlayerRollbackManager rollbackManager;

    private final Map<UUID, ParkourSession> parkourSessions = new HashMap<>();

    public GameManager(ParkourPlugin plugin) {
        this.plugin = plugin;
        this.rollbackManager = new PlayerRollbackManager();
    }

    public void playerJoin(final Player player, final Parkour parkour) {
        ParkourSession session = new ParkourSession(plugin, player, parkour);
        parkourSessions.put(player.getUniqueId(), session);
        rollbackManager.save(player);
        session.start();
    }

    public void playerQuit(final Player player, final boolean ended) {
        ParkourSession session = parkourSessions.get(player.getUniqueId());
        parkourSessions.remove(player.getUniqueId());
        rollbackManager.restore(player);
        session.end(ended);
    }

    public boolean isPlayerInParkourSession(final Player player) {
        return parkourSessions.containsKey(player.getUniqueId());
    }

    public ParkourSession getParkourSessionByPlayer(final Player player) {
        return parkourSessions.get(player.getUniqueId());
    }
}
