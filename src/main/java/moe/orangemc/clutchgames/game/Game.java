package moe.orangemc.clutchgames.game;

import moe.orangemc.clutchgames.map.GameMap;
import moe.orangemc.clutchgames.map.GameWorld;
import moe.orangemc.plugincommons.scoreboard.ScoreboardList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public abstract class Game {
    protected final Player player;
    protected final GameWorld world;
    private boolean resting = false;

    protected final List<Block> placedBlocks = new LinkedList<>();

    public Game(Player player, GameMap map) {
        this.player = player;
        this.world = new GameWorld(map);
        this.world.teleportPlayer(player);
    }

    public abstract void tick();
    public abstract void tickScoreboard(ScoreboardList scoreboardList);

    public void destroy() {
        world.destroy();
    }

    public final Player getPlayer() {
        return player;
    }

    public void placeBlock(Block blockToBePlaced) {
        placedBlocks.add(blockToBePlaced);
    }

    public boolean couldBreak(Block blockToBreak) {
        return placedBlocks.contains(blockToBreak);
    }

    public boolean isResting() {
        return resting;
    }

    public void toggleResting() {
        this.resting = !this.resting;
    }

    protected final void clearBlocks() {
        for (Block b : placedBlocks) {
            b.setType(Material.AIR);
        }
        placedBlocks.clear();
    }
}
