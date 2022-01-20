package moe.orangemc.clutchgames.listener;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.game.Game;
import moe.orangemc.clutchgames.lobby.LobbyInventoryHelper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ClutchGames.getMapCreationSessionManager().removeSession(event.getPlayer());
        ClutchGames.getCustomKnockbackSessionManager().removeSession(event.getPlayer());
        ClutchGames.getGameManager().destroyGame(event.getPlayer());
        ClutchGames.getGadgetManager().removeInventoryGroup(event.getPlayer());
        ClutchGames.getScoreboardManager().destroyScoreboard(event.getPlayer());
        ClutchGames.getInventoryManager().destory(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ClutchGames.getLobbyConfig().teleportToLobby(event.getPlayer());
        ClutchGames.getGameManager().destroyGame(event.getPlayer());
        ClutchGames.getInventoryManager().createInventory(event.getPlayer());
        ClutchGames.getGadgetManager().addInventoryGroup(event.getPlayer());
        LobbyInventoryHelper.setupPlayerLobbyInventory(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getFinalDamage() > 0.001) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Game g = ClutchGames.getGameManager().getGame(event.getPlayer());
        if (g != null) {
            return;
        }
        if (event.getTo().getY() <= 0) {
            event.setCancelled(true);
            ClutchGames.getLobbyConfig().teleportToLobby(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getFinalDamage() > 0.001) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getFinalDamage() > 0.001) {
            event.setCancelled(true);
        }
    }
}
