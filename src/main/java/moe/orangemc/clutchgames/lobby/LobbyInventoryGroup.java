package moe.orangemc.clutchgames.lobby;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.database.MySQLDataSource;
import moe.orangemc.clutchgames.game.GameType;
import moe.orangemc.clutchgames.knockback.KnockbackDifficulty;
import moe.orangemc.clutchgames.util.ItemStackFactory;
import moe.orangemc.plugincommons.PluginCommons;
import moe.orangemc.plugincommons.inventory.PluginInventory;
import moe.orangemc.plugincommons.inventory.PluginInventoryManager;
import moe.orangemc.plugincommons.inventory.control.Button;
import moe.orangemc.plugincommons.inventory.control.handler.ButtonClickHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class LobbyInventoryGroup {
    private final PluginInventoryManager pim = PluginCommons.getPluginInventoryManager(ClutchGames.getInstance());
    private final MySQLDataSource mySQLDataSource = ClutchGames.getMySQLDataSource();

    private final Player player;
    private final PluginInventory playInventory;

    private final PluginInventory settingsInventory;
    private final PluginInventory knockbackInventory;
    private final PluginInventory npcKnockbackInventory;

    public LobbyInventoryGroup(Player player) {
        this.player = player;

        playInventory = pim.createInventory(player, "选择一个游戏", 3);
        settingsInventory = pim.createInventory(player, "游戏设置", 3);
        knockbackInventory = pim.createInventory(player, "击退设置", 3);
        npcKnockbackInventory = pim.createInventory(player, "NPC击退设置", 3);

        playInventory.putButton(1, 1, new ItemStackFactory(Material.FEATHER).setName(ChatColor.GREEN + "方块自救").build(), (whoClicked, buttonClicked) -> Bukkit.dispatchCommand(whoClicked, "clutch play clutch"));
        playInventory.putButton(7, 1, new ItemStackFactory(Material.STICK).setName(ChatColor.GREEN + "NPC自救").build(), (whoClicked, buttonClicked) -> Bukkit.dispatchCommand(whoClicked, "clutch play NPCClutch"));

        settingsInventory.putButton(1, 1, new ItemStackFactory(Material.STICK).addEnchantment(Enchantment.KNOCKBACK, 1).setName(ChatColor.GREEN + "自救").build(), (whoClicked, buttonClicked) -> {
            whoClicked.closeInventory();
            knockbackInventory.open();
        });
        settingsInventory.putButton(7, 1, new ItemStackFactory(Material.FEATHER).setName(ChatColor.GREEN + "NPC自救").build(), (whoClicked, buttonClicked) -> {
            whoClicked.closeInventory();
            npcKnockbackInventory.open();
        });

        knockbackInventory.putButton(1, 1, new ItemStackFactory(Material.FEATHER).setName(ChatColor.GREEN + "击退大小").addLore("").addLore(ChatColor.YELLOW + "击退难度" + mySQLDataSource.getDifficulty(player, GameType.KNOCKBACK).getName()).build(), new KnockbackDifficultyClickHandler(GameType.KNOCKBACK));
        ButtonClickHandler timerHandler = (whoClicked, buttonClicked) -> {
            int nextTime = mySQLDataSource.getTimer(player) + 20;
            if (nextTime > 200) {
                nextTime = 20;
            }
            mySQLDataSource.setTimer(player, nextTime);
            buttonClicked.setItem(new ItemStackFactory(Material.WATCH).setName(ChatColor.GREEN + "击退时间").addLore("").addLore("击退时间: " + (mySQLDataSource.getTimer(player) / 20) + "s").build());
        };
        knockbackInventory.putButton(3, 1, new ItemStackFactory(Material.WATCH).setName(ChatColor.GREEN + "击退时间").addLore("").addLore("击退时间: " + (mySQLDataSource.getTimer(player) / 20) + "s").build(), timerHandler);
        ButtonClickHandler timesHandler = (whoClicked, buttonClicked) -> {
            int nextTimes = mySQLDataSource.getTimes(player) + 1;
            if (nextTimes > 10) {
                nextTimes = 1;
            }
            mySQLDataSource.setTimes(player, nextTimes);
            buttonClicked.setItem(new ItemStackFactory(Material.BOW).setName(ChatColor.GREEN + "击退次数").addLore("").addLore("击退次数: " + mySQLDataSource.getTimes(player)).build());
        };
        knockbackInventory.putButton(5, 1, new ItemStackFactory(Material.BOW).setName(ChatColor.GREEN + "击退次数").addLore("").addLore("击退次数: " + mySQLDataSource.getTimes(player)).build(), timesHandler);

        knockbackInventory.putButton(7, 1, new ItemStackFactory(Material.BONE).setName(ChatColor.GREEN + "自定义击退设置").addLore("").addLore(ChatColor.YELLOW + "仅在击退难度为").addLore(KnockbackDifficulty.CUSTOM.getName() + ChatColor.YELLOW + "时才可用").build(), new CustomKnockbackClickHandler(GameType.KNOCKBACK));

        npcKnockbackInventory.putButton(1, 1, new ItemStackFactory(Material.FEATHER).setName(ChatColor.GREEN + "击退大小").addLore("").addLore(ChatColor.YELLOW + "击退难度" + mySQLDataSource.getDifficulty(player, GameType.NPC_KNOCKBACK).getName()).build(), new KnockbackDifficultyClickHandler(GameType.NPC_KNOCKBACK));
        npcKnockbackInventory.putButton(7, 1, new ItemStackFactory(Material.BONE).setName(ChatColor.GREEN + "自定义击退设置").addLore("").addLore(ChatColor.YELLOW + "仅在击退难度为").addLore(KnockbackDifficulty.CUSTOM.getName() + ChatColor.YELLOW + "时才可用").build(), new CustomKnockbackClickHandler(GameType.NPC_KNOCKBACK));
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

    private class KnockbackDifficultyClickHandler implements ButtonClickHandler {
        private final GameType gameType;

        public KnockbackDifficultyClickHandler(GameType gameType) {
            this.gameType = gameType;
        }

        @Override
        public void onClick(Player whoClicked, Button buttonClicked) {
            mySQLDataSource.putDifficulty(player, mySQLDataSource.getDifficulty(player, gameType).nextDifficulty(), gameType);
            buttonClicked.setItem(new ItemStackFactory(Material.FEATHER).setName(ChatColor.GREEN + "击退大小").addLore("").addLore(ChatColor.YELLOW + "击退难度" + mySQLDataSource.getDifficulty(player, gameType).getName()).build());
        }
    }
    private static class CustomKnockbackClickHandler implements ButtonClickHandler {
        private final GameType gameType;

        public CustomKnockbackClickHandler(GameType gameType) {
            this.gameType = gameType;
        }

        @Override
        public void onClick(Player whoClicked, Button buttonClicked) {
            ClutchGames.getCustomKnockbackSessionManager().createSession(whoClicked, gameType);
            whoClicked.closeInventory();
        }
    }
}
