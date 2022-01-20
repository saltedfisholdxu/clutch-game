package moe.orangemc.clutchgames.command;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.plugincommons.command.SubCommandBase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class DebugCommand implements SubCommandBase {
    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return ChatColor.RED + "内部调试用, 禁止使用";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;
        TextComponent prefix = new TextComponent(ClutchGames.PREFIX);
        TextComponent click = new TextComponent("点击");
        click.setColor(ChatColor.GREEN);
        TextComponent here = new TextComponent("这里");
        here.setColor(ChatColor.YELLOW);
        here.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://b23.tv/g49y0K"));
        TextComponent remain = new TextComponent("以启用调试模式");
        remain.setColor(ChatColor.GREEN);
        TextComponent component = new TextComponent(prefix, click, here, remain);
        p.spigot().sendMessage(component);
        return true;
    }

    @Override
    public Plugin getProvidingPlugin() {
        return null;
    }
}
