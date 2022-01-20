package moe.orangemc.clutchgames.game;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.gadget.GadgetManager;
import moe.orangemc.clutchgames.knockback.KnockbackDifficulty;
import moe.orangemc.clutchgames.map.GameMap;
import moe.orangemc.clutchgames.util.ActionBarUtil;
import moe.orangemc.clutchgames.util.LocationUtil;
import moe.orangemc.clutchgames.util.Vector2d;
import moe.orangemc.plugincommons.scoreboard.ScoreboardList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class KnockbackGame extends Game {
    private int ticks;
    private int hits = 0;
    private double furthestDistance = 0;
    private double currentDistance = 0;
    private final int noDamageTick;

    public KnockbackGame(Player player, GameMap map) {
        super(player, map);
        this.noDamageTick = player.getMaximumNoDamageTicks();
        player.setMaximumNoDamageTicks(noDamageTick / 2);
        resetTicks();

        Location loc = player.getLocation();
        loc.setYaw((float) (ClutchGames.getKnockbackConfig().getRotation() + 180));
        player.teleport(loc);
    }

    private void resetTicks() {
        ticks = ClutchGames.getMySQLDataSource().getTimer(player);
    }

    @Override
    public void tick() {
        if (player.getLocation().getWorld() != world.getSpawnLocation().getWorld()) {
            world.teleportPlayer(player);
        }
        if (player.getLocation().getY() < 0) {
            world.teleportPlayer(player);
            resetTicks();
            hits = 0;
            clearBlocks();
        }

        GadgetManager gm = ClutchGames.getGadgetManager();
        player.getInventory().setItem(0, gm.getBlockGadget(player));
        player.getInventory().setItem(6, InGameItem.REST_BUTTON);
        player.getInventory().setItem(7, InGameItem.RETURN_LOBBY);

        if (isResting()) {
            ActionBarUtil.sendActionBar(player, ChatColor.GREEN + "你在休息");
            return;
        }

        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
            if (hits > 0) {
                if (player.getNoDamageTicks() <= 0) {
                    hits--;
                    this.player.damage(0);
                    Bukkit.getScheduler().runTaskLater(ClutchGames.getInstance(), () -> this.player.setVelocity(ClutchGames.getKnockbackConfig().getKnockback(player, ClutchGames.getMySQLDataSource().getDifficulty(this.player))), 1);
                }
            } else {
                ticks--;

                if (ticks == 0) {
                    resetTicks();
                    hits = ClutchGames.getMySQLDataSource().getTimes(player);
                }
            }
        }

        ActionBarUtil.sendActionBar(player, ChatColor.YELLOW + "击退冷却: " + (ticks / 20) + "s");

        if (player.isOnGround()) {
            furthestDistance = Math.max(furthestDistance, LocationUtil.distanceWithoutYAxis(player.getLocation(), world.getSpawnLocation()));
            currentDistance = LocationUtil.distanceWithoutYAxis(player.getLocation(), world.getSpawnLocation());
        }
    }

    @Override
    public void tickScoreboard(ScoreboardList scoreboardList) {
        updateScoreboard(scoreboardList, player.getName(), furthestDistance, currentDistance, world.getSpawnLocation(), player.getLocation(), ClutchGames.getMySQLDataSource().getDifficulty(player), ClutchGames.getMySQLDataSource().getKnockback(player));
    }

    static void updateScoreboard(ScoreboardList scoreboardList, String name, double furthestDistance, double currentDistance, Location spawnLocation, Location location, KnockbackDifficulty difficulty, Vector2d knockback) {
        scoreboardList.set(1, "玩家名: " + ChatColor.YELLOW + name);

        scoreboardList.set(3, "练习信息: ");
        scoreboardList.set(4, String.format("最远距离: " + ChatColor.LIGHT_PURPLE + "%.2f", furthestDistance));
        scoreboardList.set(5, String.format("当前距离: " + ChatColor.LIGHT_PURPLE + "%.2f", currentDistance));
        scoreboardList.set(6, "练习难度: " + difficulty.getName());
        if (difficulty == KnockbackDifficulty.CUSTOM) {
            scoreboardList.set(7, String.format("击退大小: " + ChatColor.LIGHT_PURPLE + "%.2f,%.2f", knockback.getX(), knockback.getY()));
        } else {
            scoreboardList.set(7, "");
        }

        scoreboardList.set(9, ChatColor.YELLOW + "mc.kinomc.cn");
    }

    @Override
    public void destroy() {
        super.destroy();

        ClutchGames.getMySQLDataSource().setClutchDefaultRecord(player, furthestDistance);
        player.setMaximumNoDamageTicks(noDamageTick);
    }
}
