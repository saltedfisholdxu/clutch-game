package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.lobby.LobbyConfig;
import moe.orangemc.clutchgames.util.Logger;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class SetLobbyCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "setlobby";
    }

    @Override
    public String getDescription() {
        return "设置大厅位置";
    }

    @Override
    public String getUsage() {
        return "执行即可";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "控制台在哪里");
            return true;
        }

        File lobbyFile = new File(ClutchGames.getInstance().getDataFolder(), "lobby.yml");
        if (!lobbyFile.exists()) {
            try {
                lobbyFile.createNewFile();
            } catch (IOException e) {
                sender.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "无法保存大厅信息");
                Logger.warn("Could not save lobby location");
                e.printStackTrace();
                return true;
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(lobbyFile);
        Location targetLocation = ((Player) sender).getLocation();
        config.set("world", targetLocation.getWorld().getName());
        config.set("x", targetLocation.getBlockX());
        config.set("y", targetLocation.getBlockY());
        config.set("z", targetLocation.getBlockZ());
        try {
            config.save(lobbyFile);
            ClutchGames.getLobbyConfig().setLobbyLocation(targetLocation);
            sender.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "已保存大厅信息");
        } catch (IOException e) {
            sender.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "无法保存大厅信息");
            Logger.warn("Could not save lobby location");
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }

    @Override
    public String getPermissionRequired() {
        return "clutch.command.setlobby";
    }
}
