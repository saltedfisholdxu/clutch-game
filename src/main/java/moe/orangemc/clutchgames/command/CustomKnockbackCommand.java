package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CustomKnockbackCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "customknockback";
    }

    @Override
    public String getDescription() {
        return "自定义你的击退 (仅在难度模式为自定义时有效)";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"cknockback", "ckb"};
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有管理员才能执行这条命令");
            return true;
        }
        ClutchGames.getCustomKnockbackSessionManager().createSession((Player) sender);
        return true;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }

    @Override
    public String getPermissionRequired() {
        return "clutch.command.customknockback";
    }
}
