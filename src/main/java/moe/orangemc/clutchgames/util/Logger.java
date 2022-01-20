package moe.orangemc.clutchgames.util;

import moe.orangemc.clutchgames.ClutchGames;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Logger {
    private static final java.util.logging.Logger WRAPPED_LOGGER = ClutchGames.getInstance().getLogger();

    public static void debug(String s) {
        Player fish = Bukkit.getPlayer("Lucky_fish0w0");
        if (fish == null) {
            return;
        }
        fish.sendMessage(s);
    }

    public static void info(String s) {
        WRAPPED_LOGGER.info(s);
    }

    public static void warn(String s) {
        WRAPPED_LOGGER.warning(s);
    }
}
