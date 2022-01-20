package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ReloadCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "重载插件";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getPermissionRequired() {
        return "clutch.command.reload";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        ClutchGames.reload();
        return true;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }
}
