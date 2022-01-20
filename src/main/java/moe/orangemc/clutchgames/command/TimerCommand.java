package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TimerCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "timer";
    }

    @Override
    public String getDescription() {
        return "设置击退周期";
    }

    @Override
    public String getUsage() {
        return "[时间]";
    }

    @Override
    public String getPermissionRequired() {
        return "clutch.command.timer";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家才能执行此命令");
            return true;
        }

        try {
            int timer = Integer.parseInt(args[0]);
            ClutchGames.getMySQLDataSource().setTimer((Player) sender, timer * 20);
            sender.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "将你的时间设置为" + timer + "秒");
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }
}
