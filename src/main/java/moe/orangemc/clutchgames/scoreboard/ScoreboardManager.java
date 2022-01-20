package moe.orangemc.clutchgames.scoreboard;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.plugincommons.PluginCommons;
import moe.orangemc.plugincommons.scoreboard.ScoreboardList;
import moe.orangemc.plugincommons.scoreboard.ScoreboardListManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {
    private final ScoreboardListManager scoreboardListManager = PluginCommons.getScoreboardListManager(ClutchGames.getInstance());

    private final Map<Player, ScoreboardList> gameScoreboard = new HashMap<>();

    public void registerGameScoreboard(Player player) {
        gameScoreboard.put(player, scoreboardListManager.createScoreboard(player.getName(), ChatColor.YELLOW + "Ki" + ChatColor.GOLD + "no" + ChatColor.RED + "MC"));
    }

    public void destroyScoreboard(Player player) {
        if (gameScoreboard.containsKey(player)) {
            scoreboardListManager.destroyScoreboard(gameScoreboard.get(player));
            gameScoreboard.remove(player);
        }
    }

    public void destroyGameScoreboard(Player player) {
        if (gameScoreboard.containsKey(player)) {
            scoreboardListManager.destroyScoreboard(gameScoreboard.get(player));
            gameScoreboard.remove(player);
        }
    }

    public void updateGameScoreboard() {
        gameScoreboard.forEach((p, sc) -> {
            sc.displayToPlayer(p);
            ClutchGames.getGameManager().getGame(p).tickScoreboard(sc);
        });
    }

    public void destroyAllScoreboard() {
        gameScoreboard.forEach((p, scb) -> scoreboardListManager.destroyScoreboard(scb));
    }
}
