package moe.orangemc.clutchgames.game;

import moe.orangemc.clutchgames.util.ItemStackFactory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InGameItem {
    public static final ItemStack RETURN_LOBBY = new ItemStackFactory(Material.SLIME_BALL).setName(ChatColor.GREEN + "返回大厅").build();
    public static final ItemStack REST_BUTTON = new ItemStackFactory(Material.BED).setName(ChatColor.GREEN + "休息模式").build();
}
