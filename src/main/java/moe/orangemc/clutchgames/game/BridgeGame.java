package moe.orangemc.clutchgames.game;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.gadget.GadgetManager;
import moe.orangemc.clutchgames.map.GameMap;
import moe.orangemc.clutchgames.util.ActionBarUtil;
import moe.orangemc.plugincommons.scoreboard.ScoreboardList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BridgeGame extends Game {
    private int timer = 0;
    private boolean timing = false;
    private boolean firstBlockPlaced = false;

    public BridgeGame(Player player, GameMap map) {
        super(player, map);
    }

    @Override
    public void placeBlock(Block blockToBePlaced) {
        if (!firstBlockPlaced) {
            firstBlockPlaced = true;
            timing = true;
            timer = 0;
        }
        super.placeBlock(blockToBePlaced);
    }

    @Override
    public void tick() {
        if (player.getLocation().getWorld() != world.getSpawnLocation().getWorld()) {
            world.teleportPlayer(player);
        }

        Location playerLoc = player.getLocation();
        GadgetManager gm = ClutchGames.getGadgetManager();
        player.getInventory().setItem(0, gm.getBlockGadget(player));
        player.getInventory().setItem(1, gm.getBlockGadget(player));
        player.getInventory().setItem(7, InGameItem.RETURN_LOBBY);

        if (timing) {
            timer ++;

            int totalSeconds = timer / 20;
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            double millSeconds = timer % 20 * 0.05;

            ActionBarUtil.sendActionBar(player, ChatColor.GOLD + "" + minutes + ":" + (seconds + millSeconds));
        }

        if (world.getExtraLocation().distance(playerLoc) < 1) {
            clearBlocks();
            world.teleportPlayer(player);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            timing = false;
            firstBlockPlaced = false;
            logTimer();
        } else if (playerLoc.getY() < 0) {
            clearBlocks();
            world.teleportPlayer(player);
            timing = false;
            firstBlockPlaced = false;
        }
    }

    private void logTimer() {
        ClutchGames.getMySQLDataSource().putBridgeRecord(player, timer / 20);
    }

    @Override
    public void tickScoreboard(ScoreboardList scoreboardList) {
        scoreboardList.set(0, "");
        scoreboardList.set(1, ChatColor.AQUA + "个人最好时间");
        int time = ClutchGames.getMySQLDataSource().getBridgeRecord(player);
        int minutes = time / 60;
        int seconds = time % 60;
        scoreboardList.set(2, minutes + ":" + seconds);
        scoreboardList.set(4, ChatColor.GOLD + "方块放置: ");
        scoreboardList.set(5, ChatColor.YELLOW + "" + this.placedBlocks.size());

        scoreboardList.set(6, ChatColor.AQUA + "当前时间: ");
        int time2 = timer / 20;
        int min2 = time2 / 60;
        int sec2 = time2 % 60;
        scoreboardList.set(7, ChatColor.YELLOW + "" + min2 + ":" + sec2);

        scoreboardList.set(9, "mc.kinomc.cn");
    }
}
