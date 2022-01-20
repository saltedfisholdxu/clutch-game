package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LobbyCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "lobby";
    }

    @Override
    public String getDescription() {
        return "返回到大厅";
    }

    @Override
    public String getUsage() {
        return "lobby";
    }

    @Override
    public String getPermissionRequired() {
        return "clutch.command.lobby";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "无法传送不存在的实体");
            return true;
        }

        Player p = (Player) sender;
        ClutchGames.getLobbyConfig().teleportToLobby(p);
        ClutchGames.getGameManager().destroyGame(p);
        sender.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "正在将你传送至大厅");

        return true;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }
}
