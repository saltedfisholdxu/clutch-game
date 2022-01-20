package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.knockback.KnockbackDifficulty;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DifficultyCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "difficulty";
    }

    @Override
    public String getDescription() {
        return "设置你的击退难度";
    }

    @Override
    public String getUsage() {
        return "<击退难度 easy(简单)|normal(普通)|hard(困难)>";
    }

    @Override
    public String getPermissionRequired() {
        return "clutch.command.difficulty";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "控制台不能玩游戏! ");
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        KnockbackDifficulty difficulty;
        switch (args[0]) {
            case "easy":
                difficulty = KnockbackDifficulty.EASY;
                break;
            case "normal":
                difficulty = KnockbackDifficulty.NORMAL;
                break;
            case "hard":
                difficulty = KnockbackDifficulty.HARD;
                break;
            default:
                return false;
        }
        ClutchGames.getMySQLDataSource().putDifficulty((Player) sender, difficulty);
        sender.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "你的击退难度已设置为" + difficulty.getName());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return Arrays.asList("easy", "normal", "hard");
        }
        return Collections.emptyList();
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }
}
