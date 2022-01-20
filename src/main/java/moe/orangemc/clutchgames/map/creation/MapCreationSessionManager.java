package moe.orangemc.clutchgames.map.creation;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MapCreationSessionManager {
    private final Map<Player, MapCreationSession> sessionMap = new HashMap<>();

    public void createSession(Player player) {
        MapCreationSession session = new MapCreationSession();
        sessionMap.put(player, session);
        session.start(player);
    }

    public MapCreationSession getSession(Player player) {
        return sessionMap.get(player);
    }

    public void removeSession(Player player) {
        sessionMap.remove(player);
    }
}
