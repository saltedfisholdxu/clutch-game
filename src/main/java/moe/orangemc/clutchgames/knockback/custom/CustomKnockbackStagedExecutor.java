package moe.orangemc.clutchgames.knockback.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface CustomKnockbackStagedExecutor {
    Player getPlayer();
    void preExecute(CustomKnockbackSetSession session);
    void execute(AsyncPlayerChatEvent event, CustomKnockbackSetSession session);
}
