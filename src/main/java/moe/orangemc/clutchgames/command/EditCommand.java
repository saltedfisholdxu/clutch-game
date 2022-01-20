package moe.orangemc.clutchgames.command;

import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class EditCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public String getDescription() {
        return "编辑一个地图";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getPermissionRequired() {
        return SubCommandBase.super.getPermissionRequired();
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        return false;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }
}
