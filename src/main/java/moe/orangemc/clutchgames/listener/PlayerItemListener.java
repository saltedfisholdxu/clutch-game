package moe.orangemc.clutchgames.listener;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.game.Game;
import moe.orangemc.clutchgames.game.InGameItem;
import moe.orangemc.clutchgames.lobby.LobbyInventoryHelper;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }

        ItemStack is = event.getItem();
        if (is.isSimilar(LobbyInventoryHelper.PLAY_SWORD)) {
            ClutchGames.getInventoryManager().openPlayInventory(event.getPlayer());
            event.setCancelled(true);
        } else if (is.isSimilar(LobbyInventoryHelper.SETTING)) {
            ClutchGames.getInventoryManager().openSettingInventory(event.getPlayer());
            event.setCancelled(true);
        } else if (is.isSimilar(InGameItem.RETURN_LOBBY)) {
            Bukkit.dispatchCommand(event.getPlayer(), "clutch lobby");
            event.setCancelled(true);
        } else if (is.isSimilar(LobbyInventoryHelper.GADGET)) {
            ClutchGames.getGadgetManager().openInventory(event.getPlayer());
            event.setCancelled(true);
        } else if (is.isSimilar(InGameItem.REST_BUTTON)) {
            Game game = ClutchGames.getGameManager().getGame(event.getPlayer());
            event.setCancelled(true);
            if (game != null) {
                game.toggleResting();
            }
        }
    }
}
