package moe.orangemc.clutchgames.listener;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.map.creation.MapCreationStagedExecutor;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MapCreationListener implements Listener {
    private static final List<MapCreationStagedExecutor> HANDLERS = new LinkedList<>();

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Iterator<MapCreationStagedExecutor> handlerIterator = HANDLERS.iterator();
        while (handlerIterator.hasNext()) {
            MapCreationStagedExecutor executor = handlerIterator.next();
            if (executor.getPlayer() == event.getPlayer()) {
                Bukkit.getScheduler().runTaskLater(ClutchGames.getInstance(), () -> executor.execute(event, ClutchGames.getMapCreationSessionManager().getSession(event.getPlayer())), 1);
                handlerIterator.remove();
            }
        }
    }

    public static void registerHandler(MapCreationStagedExecutor executor) {
        HANDLERS.add(executor);
    }
}
