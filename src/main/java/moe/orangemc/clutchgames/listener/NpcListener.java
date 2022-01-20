package moe.orangemc.clutchgames.listener;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.game.Game;
import moe.orangemc.clutchgames.game.NpcKnockbackGame;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class NpcListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onNPCDamageByEntity(NPCDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        Player player = (Player) event.getDamager();
        Game game = ClutchGames.getGameManager().getGame(player);
        if (!(game instanceof NpcKnockbackGame)) {
            event.setCancelled(true);
            return;
        }
        NpcKnockbackGame npcGame = (NpcKnockbackGame) game;
        NPC npc = event.getNPC();
        if (npcGame.getKnockbackNpc() != npc) {
            event.setCancelled(true);
            return;
        }

        if (!npcGame.shouldNpcDamaged()) {
            event.setCancelled(true);
            return;
        }
        event.setDamage(0);
        Bukkit.getScheduler().runTaskLater(ClutchGames.getInstance(), () -> npc.getEntity().setVelocity(new Vector()), 1);
        npcGame.attackNpc();
    }
}
