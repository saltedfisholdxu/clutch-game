package moe.orangemc.clutchgames.knockback.custom;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.util.Vector2d;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomKnockbackSetSession {
    private CustomKnockbackSetStage stage;

    private double x;
    private double y;

    public void start(Player player) {
        stage = CustomKnockbackSetStage.VERTICAL;
        stage.getExecutor(player).preExecute(this);
    }

    public void nextStage(Player player) {
        stage = stage.getNextStage();

        Bukkit.getScheduler().runTaskLater(ClutchGames.getInstance(), () -> stage.getExecutor(player).preExecute(this), 1);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2d build() {
        return new Vector2d(x, y);
    }
}
