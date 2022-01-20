package moe.orangemc.clutchgames.game;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.lobby.LobbyInventoryHelper;
import moe.orangemc.clutchgames.map.GameMap;
import moe.orangemc.clutchgames.scoreboard.ScoreboardManager;
import moe.orangemc.clutchgames.util.Logger;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private final Map<Player, Game> playerGame = new HashMap<>();
    private final ScoreboardManager scoreboardManager = ClutchGames.getScoreboardManager();

    public void createKnockbackGame(Player player, GameMap gameMap) {
        scoreboardManager.registerGameScoreboard(player);
        playerGame.put(player, new KnockbackGame(player, gameMap));
    }

    public void createNpcKnockbackGame(Player player, GameMap gameMap) {
        scoreboardManager.registerGameScoreboard(player);
        playerGame.put(player, new NpcKnockbackGame(player, gameMap));
    }

    public void createBridgeGame(Player player, GameMap gameMap) {
        scoreboardManager.registerGameScoreboard(player);
        playerGame.put(player, new BridgeGame(player, gameMap));
    }

    public void destroyGame(Player player) {
        if (!playerGame.containsKey(player)) {
            return;
        }
        Game game = playerGame.get(player);
        game.destroy();
        playerGame.remove(player);
        player.getInventory().clear();
        LobbyInventoryHelper.setupPlayerLobbyInventory(player);
        scoreboardManager.destroyGameScoreboard(player);
    }

    public void destroyAllGame() {
        playerGame.forEach(((player, game) -> {
            game.destroy();
            playerGame.remove(player);
            player.getInventory().clear();
            scoreboardManager.destroyGameScoreboard(player);
        }));
    }

    public Game getGame(Player player) {
        return playerGame.get(player);
    }

    public void tick() {
        playerGame.forEach((p, game) -> game.tick());
    }
}
