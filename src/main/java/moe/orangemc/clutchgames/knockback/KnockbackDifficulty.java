package moe.orangemc.clutchgames.knockback;

import org.bukkit.ChatColor;

public enum KnockbackDifficulty {
    EASY(ChatColor.GREEN + "简单"),
    NORMAL(ChatColor.YELLOW + "普通"),
    HARD(ChatColor.RED + "困难"),
    CUSTOM(ChatColor.AQUA + "自定义");

    private final String name;

    KnockbackDifficulty(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public KnockbackDifficulty nextDifficulty() {
        switch (this) {
            case EASY:
                return NORMAL;
            case NORMAL:
                return HARD;
            case HARD:
                return CUSTOM;
            case CUSTOM:
                return EASY;
            default:
                throw new IllegalStateException("Unknown enum: " + this);
        }
    }
}
