package moe.orangemc.clutchgames.knockback;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.game.GameType;
import moe.orangemc.clutchgames.util.Vector2d;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class KnockbackConfig {
    private final double rotation;
    private final Map<KnockbackDifficulty, Vector2d> knockbackDifficultyMap = new HashMap<>();

    public KnockbackConfig(File folder) {
        YamlConfiguration yc = YamlConfiguration.loadConfiguration(new File(folder, "knockback.yml"));
        rotation = yc.getDouble("rotation");

        for (KnockbackDifficulty difficulty : KnockbackDifficulty.values()) {
            ConfigurationSection knockbackSection = yc.getConfigurationSection(difficulty.name().toLowerCase(Locale.ROOT));
            if (knockbackSection == null) { // custom difficulty does not have the exact config.
                continue;
            }
            knockbackDifficultyMap.put(difficulty, new Vector2d(knockbackSection.getDouble("horizontal"), knockbackSection.getDouble("vertical")));
        }
    }

    public Vector getKnockback(Player player, KnockbackDifficulty difficulty, GameType gameType) {
        return getKnockbackByRotation(player, difficulty, rotation, gameType);
    }

    public double getRotation() {
        return rotation;
    }

    public Vector getKnockbackByRotation(Player player, KnockbackDifficulty difficulty, double rotation, GameType gameType) {
        Vector2d vec;
        if (difficulty == KnockbackDifficulty.CUSTOM) {
            vec = ClutchGames.getMySQLDataSource().getKnockback(player, gameType);
        } else {
            vec = knockbackDifficultyMap.get(difficulty);
        }
        return new Vector(-Math.sin(Math.toRadians(rotation)) * vec.getX(), vec.getY(), Math.cos(Math.toRadians(rotation)) * vec.getX());
    }

}
