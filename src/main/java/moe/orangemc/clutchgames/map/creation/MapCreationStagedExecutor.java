package moe.orangemc.clutchgames.map.creation;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface MapCreationStagedExecutor {
    Player getPlayer();
    void preExecute(MapCreationSession session);
    void execute(AsyncPlayerChatEvent event, MapCreationSession session);
}
