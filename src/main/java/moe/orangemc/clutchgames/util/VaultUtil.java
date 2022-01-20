package moe.orangemc.clutchgames.util;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtil {
    private static final Economy ECO;

    static {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            ECO = null;
        } else {
            ECO = rsp.getProvider();
        }
    }

    public static boolean haveMoney(Player player, int amount) {
        if (ECO == null) {
            return false;
        }
        return ECO.has(player, amount);
    }

    public static void takeMoney(Player player, int amount) {
        if (ECO == null) {
            return;
        }
        ECO.withdrawPlayer(player, amount);
    }
}
