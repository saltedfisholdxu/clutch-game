package moe.orangemc.clutchgames.lobby;

import moe.orangemc.clutchgames.util.ItemStackFactory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class LobbyInventoryHelper {
    public static ItemStack PLAY_SWORD = new ItemStackFactory(Material.IRON_SWORD)
            .setAmount(1)
            .setName(ChatColor.GREEN + "开始游戏")
            .build();

    public static ItemStack SETTING = new ItemStackFactory(Material.REDSTONE_COMPARATOR)
            .setAmount(1)
            .setName(ChatColor.RED + "游戏设置")
            .build();

    public static ItemStack GADGET = new ItemStackFactory(Material.DIAMOND)
            .setAmount(1)
            .setName(ChatColor.GOLD + "游戏皮肤")
            .build();

    public static void setupPlayerLobbyInventory(Player player) {
        PlayerInventory pi = player.getInventory();
        pi.setItem(0, PLAY_SWORD);
        pi.setItem(8, SETTING);
        pi.setItem(7, GADGET);
    }
}
