package moe.orangemc.clutchgames.util;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerPointUtil {
    private static final PlayerPointsAPI PPAPI;

    static {
        PlayerPoints plugin = null;
        try {
            plugin = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
        } catch (ClassCastException ignored) {

        }
        if (plugin == null) {
            PPAPI = null;
        } else {
            PPAPI = plugin.getAPI();
        }
    }

    public static boolean havePoints(Player player, int amount) {
        if (PPAPI == null) {
            return false;
        }
        return PPAPI.look(player.getUniqueId()) >= amount;
    }
    public static void takePoints(Player player, int amount) {
        if (PPAPI == null) {
            return;
        }
        PPAPI.take(player.getUniqueId(), amount);
    }
}
