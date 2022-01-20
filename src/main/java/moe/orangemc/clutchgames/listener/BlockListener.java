package moe.orangemc.clutchgames.listener;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.game.Game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Game game = ClutchGames.getGameManager().getGame(p);
        if (!event.canBuild()) {
            return;
        }
        if (game == null) {
            event.setCancelled(true);
            return;
        }
        if (game.isResting()) {
            event.setCancelled(true);
        }
        Bukkit.getScheduler().runTaskLater(ClutchGames.getInstance(), () -> {
            if (event.getItemInHand().getType() == event.getBlockPlaced().getLocation().getBlock().getType()) {
                game.placeBlock(event.getBlockPlaced());
            }
        }, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Game game = ClutchGames.getGameManager().getGame(p);
        if (game == null) {
            event.setCancelled(true);
            return;
        }
        if (game.couldBreak(event.getBlock())) {
            return;
        }
        event.setCancelled(true);
    }
}
