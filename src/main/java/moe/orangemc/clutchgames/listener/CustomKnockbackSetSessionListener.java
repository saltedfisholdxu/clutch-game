package moe.orangemc.clutchgames.listener;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.knockback.custom.CustomKnockbackStagedExecutor;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CustomKnockbackSetSessionListener implements Listener {
    private static final List<CustomKnockbackStagedExecutor> HANDLERS = new LinkedList<>();

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Iterator<CustomKnockbackStagedExecutor> iterator = HANDLERS.iterator();
        while (iterator.hasNext()) {
            CustomKnockbackStagedExecutor executor = iterator.next();
            if (executor.getPlayer() == event.getPlayer()) {
                Bukkit.getScheduler().runTaskLater(ClutchGames.getInstance(), () -> executor.execute(event, ClutchGames.getCustomKnockbackSessionManager().getSession(executor.getPlayer())), 1);
                iterator.remove();
            }
        }
    }

    public static void registerHandler(CustomKnockbackStagedExecutor executor) {
        HANDLERS.add(executor);
    }
}
