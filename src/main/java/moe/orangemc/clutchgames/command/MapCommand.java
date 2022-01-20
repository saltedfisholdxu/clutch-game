package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MapCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "map";
    }

    @Override
    public String getDescription() {
        return "创建一个地图";
    }

    @Override
    public String getUsage() {
        return "你用了就知道了";
    }

    @Override
    public String getPermissionRequired() {
        return "clutch.command.map";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "我们没法定位控制台");
            return true;
        }
        ClutchGames.getMapCreationSessionManager().createSession((Player) sender);
        sender.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "会话已生成");
        return true;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }
}
