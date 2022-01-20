package moe.orangemc.clutchgames.lobby;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.database.MySQLDataSource;
import moe.orangemc.clutchgames.knockback.KnockbackDifficulty;
import moe.orangemc.clutchgames.util.ItemStackFactory;
import moe.orangemc.plugincommons.PluginCommons;
import moe.orangemc.plugincommons.inventory.PluginInventory;
import moe.orangemc.plugincommons.inventory.PluginInventoryManager;
import moe.orangemc.plugincommons.inventory.control.handler.ButtonClickHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LobbyInventoryGroup {
    private final PluginInventoryManager pim = PluginCommons.getPluginInventoryManager(ClutchGames.getInstance());
    private final MySQLDataSource mySQLDataSource = ClutchGames.getMySQLDataSource();

    private final Player player;
    private final PluginInventory playInventory;
    private final PluginInventory settingsInventory;

    public LobbyInventoryGroup(Player player) {
        this.player = player;

        playInventory = pim.createInventory(player, "选择一个游戏", 3);
        settingsInventory = pim.createInventory(player, "游戏设置", 3);

        playInventory.putButton(1, 1, new ItemStackFactory(Material.FEATHER).setName(ChatColor.GREEN + "方块自救").build(), (whoClicked, buttonClicked) -> Bukkit.dispatchCommand(whoClicked, "clutch play clutch"));
        playInventory.putButton(4, 1, new ItemStackFactory(Material.STICK).setName(ChatColor.GREEN + "NPC自救").build(), (whoClicked, buttonClicked) -> Bukkit.dispatchCommand(whoClicked, "clutch play NPCClutch"));
        playInventory.putButton(7, 1, new ItemStackFactory(Material.SANDSTONE).setName(ChatColor.GREEN + "搭路练习").build(), (whoClicked, buttonClicked) -> Bukkit.dispatchCommand(whoClicked, "clutch play bridge"));

        ButtonClickHandler difficultyHandler = (whoClicked, buttonClicked) -> {
            mySQLDataSource.putDifficulty(player, mySQLDataSource.getDifficulty(player).nextDifficulty());
            buttonClicked.setItem(new ItemStackFactory(Material.FEATHER).setName(ChatColor.GREEN + "击退大小").addLore("").addLore(ChatColor.YELLOW + "击退难度" + mySQLDataSource.getDifficulty(player).getName()).build());
        };
        settingsInventory.putButton(1, 1, new ItemStackFactory(Material.FEATHER).setName(ChatColor.GREEN + "击退大小").addLore("").addLore(ChatColor.YELLOW + "击退难度" + mySQLDataSource.getDifficulty(player).getName()).build(), difficultyHandler);
        ButtonClickHandler timerHandler = (whoClicked, buttonClicked) -> {
            int nextTime = mySQLDataSource.getTimer(player) + 20;
            if (nextTime > 200) {
                nextTime = 20;
            }
            mySQLDataSource.setTimer(player, nextTime);
            buttonClicked.setItem(new ItemStackFactory(Material.WATCH).setName(ChatColor.GREEN + "击退时间").addLore("").addLore("击退时间: " + (mySQLDataSource.getTimer(player) / 20) + "s").build());
        };
        settingsInventory.putButton(3, 1, new ItemStackFactory(Material.WATCH).setName(ChatColor.GREEN + "击退时间").addLore("").addLore("击退时间: " + (mySQLDataSource.getTimer(player) / 20) + "s").build(), timerHandler);
        ButtonClickHandler timesHandler = (whoClicked, buttonClicked) -> {
            int nextTimes = mySQLDataSource.getTimes(player) + 1;
            if (nextTimes > 10) {
                nextTimes = 1;
            }
            mySQLDataSource.setTimes(player, nextTimes);
            buttonClicked.setItem(new ItemStackFactory(Material.BOW).setName(ChatColor.GREEN + "击退次数").addLore("").addLore("击退次数: " + mySQLDataSource.getTimes(player)).build());
        };
        settingsInventory.putButton(5, 1, new ItemStackFactory(Material.BOW).setName(ChatColor.GREEN + "击退次数").addLore("").addLore("击退次数: " + mySQLDataSource.getTimes(player)).build(), timesHandler);
        ButtonClickHandler knockbackInventoryHandler = (whoClicked, buttonClicked) -> {
            ClutchGames.getCustomKnockbackSessionManager().createSession(whoClicked);
            whoClicked.closeInventory();
        };
        settingsInventory.putButton(7, 1, new ItemStackFactory(Material.BONE).setName(ChatColor.GREEN + "自定义击退设置").addLore("").addLore(ChatColor.YELLOW + "仅在击退难度为").addLore(KnockbackDifficulty.CUSTOM.getName() + ChatColor.YELLOW + "时才可用").build(), knockbackInventoryHandler);
    }

    public void openPlayInventory() {
        playInventory.open();
    }

    public void openSettingsInventory() {
        settingsInventory.open();
    }

    public void destroyAll() {
        pim.destroyInventory(playInventory);
        pim.destroyInventory(settingsInventory);
    }
}
