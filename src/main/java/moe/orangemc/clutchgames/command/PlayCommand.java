package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.game.GameManager;
import moe.orangemc.clutchgames.map.MapManager;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "游玩一个游戏";
    }

    @Override
    public String getUsage() {
        return "<游戏类型 bridge(搭路)|NPCClutch(NPC自救)|clutch(自救)>";
    }

    @Override
    public String getPermissionRequired() {
        return "clutch.command.play";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "控制台怎么玩啊");
            return true;
        }
        if (args.length != 1) {
            return false;
        }

        Player p = (Player) sender;

        MapManager mapManager = ClutchGames.getMapManager();
        switch (args[0]) {
            case "bridge":
                mapManager.openBridgeGameMapSelector(p);
                break;
            case "NPCClutch":
                mapManager.openNpcKnockbackGameMapSelector(p);
                break;
            case "clutch":
                mapManager.openKnockbackGameMapSelector(p);
                break;
            default:
                return false;
        }
        sender.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "正在打开地图选择页面");

        return true;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }
}
