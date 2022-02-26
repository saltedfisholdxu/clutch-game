package moe.orangemc.clutchgames.knockback.custom;

import moe.orangemc.clutchgames.game.GameType;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CustomKnockbackSessionManager {
    private final Map<Player, CustomKnockbackSetSession> sessionMap = new HashMap<>();

    public CustomKnockbackSetSession getSession(Player p) {
        return sessionMap.get(p);
    }

    public void createSession(Player player, GameType gameType) {
        CustomKnockbackSetSession session = new CustomKnockbackSetSession();
        sessionMap.put(player, session);
        session.start(player, gameType);
    }

    public void removeSession(Player player) {
        sessionMap.remove(player);
    }
}
