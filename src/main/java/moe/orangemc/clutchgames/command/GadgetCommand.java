package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GadgetCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "gadget";
    }

    @Override
    public String getDescription() {
        return "设置你的皮肤";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "这条命令必须由玩家执行");
            return true;
        }

        Player p = (Player) sender;
        ClutchGames.getGadgetManager().openInventory(p);

        return true;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }

    @Override
    public String getPermissionRequired() {
        return "clutch.command.gadget";
    }
}
