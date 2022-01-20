package moe.orangemc.clutchgames;

import moe.orangemc.clutchgames.command.*;
import moe.orangemc.clutchgames.database.MySQLDataSource;
import moe.orangemc.clutchgames.gadget.GadgetManager;
import moe.orangemc.clutchgames.game.GameManager;
import moe.orangemc.clutchgames.lobby.PlayerLobbyInventoryManager;
import moe.orangemc.clutchgames.knockback.KnockbackConfig;
import moe.orangemc.clutchgames.knockback.custom.CustomKnockbackSessionManager;
import moe.orangemc.clutchgames.listener.*;
import moe.orangemc.clutchgames.lobby.LobbyConfig;
import moe.orangemc.clutchgames.map.MapManager;
import moe.orangemc.clutchgames.map.creation.MapCreationSessionManager;
import moe.orangemc.clutchgames.papi.PAPIHelper;
import moe.orangemc.clutchgames.scoreboard.ScoreboardManager;
import moe.orangemc.clutchgames.task.GameManagerUpdater;
import moe.orangemc.clutchgames.task.ScoreboardManagerUpdater;
import moe.orangemc.plugincommons.command.CommonCommand;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ClutchGames extends JavaPlugin {
    public static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Ki" + ChatColor.GOLD + "no" + ChatColor.RED + "MC" + ChatColor.GRAY + "] ";

    private static ClutchGames instance;

    private static MapCreationSessionManager mapCreationSessionManager;
    private static MapManager mapManager;

    private static GameManager gameManager;
    private static ScoreboardManager scoreboardManager;

    private static GadgetManager gadgetManager;

    private static PlayerLobbyInventoryManager inventoryManager;

    private static CustomKnockbackSessionManager customKnockbackSessionManager;

    private static MySQLDataSource mySQLDataSource;

    private static LobbyConfig lobbyConfig;
    private static KnockbackConfig knockbackConfig;

    @Override
    public void onEnable() {
        instance = this;
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        saveDefaultConfig();
        saveResource("knockback.yml", false);
        saveResource("block.yml", false);
        saveResource("stick.yml", false);

        reload();
        mapCreationSessionManager = new MapCreationSessionManager();
        scoreboardManager = new ScoreboardManager();
        gameManager = new GameManager();

        inventoryManager = new PlayerLobbyInventoryManager();

        customKnockbackSessionManager = new CustomKnockbackSessionManager();

        CommonCommand cmd = new CommonCommand(PREFIX);
        cmd.registerCommand(new MapCommand());
        cmd.registerCommand(new SetLobbyCommand());
        cmd.registerCommand(new PlayCommand());
        cmd.registerCommand(new LobbyCommand());
        cmd.registerCommand(new DifficultyCommand());
        cmd.registerCommand(new CustomKnockbackCommand());
        cmd.registerCommand(new TimerCommand());
        cmd.registerCommand(new DebugCommand());
        cmd.registerCommand(new ReloadCommand());

        getCommand("clutch").setTabCompleter(cmd);
        getCommand("clutch").setExecutor(cmd);

        getServer().getPluginManager().registerEvents(new MapCreationListener(), this);
        getServer().getPluginManager().registerEvents(new NpcListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerItemListener(), this);
        getServer().getPluginManager().registerEvents(new CustomKnockbackSetSessionListener(), this);
        getServer().getPluginManager().registerEvents(new ItemListener(), this);

        getServer().getScheduler().runTaskTimer(this, new GameManagerUpdater(), 0, 0);
        getServer().getScheduler().runTaskTimer(this, new ScoreboardManagerUpdater(), 0, 0);

        new PAPIHelper().register();
    }

    @Override
    public void onDisable() {
        getGameManager().destroyAllGame();
        getScoreboardManager().destroyAllScoreboard();
    }

    public static ClutchGames getInstance() {
        return instance;
    }

    public static MapCreationSessionManager getMapCreationSessionManager() {
        return mapCreationSessionManager;
    }

    public static MapManager getMapManager() {
        return mapManager;
    }

    public static LobbyConfig getLobbyConfig() {
        return lobbyConfig;
    }

    public static KnockbackConfig getKnockbackConfig() {
        return knockbackConfig;
    }

    public static MySQLDataSource getMySQLDataSource() {
        return mySQLDataSource;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public static CustomKnockbackSessionManager getCustomKnockbackSessionManager() {
        return customKnockbackSessionManager;
    }

    public static PlayerLobbyInventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public static GadgetManager getGadgetManager() {
        return gadgetManager;
    }

    public static void reload() {
        mySQLDataSource = new MySQLDataSource(getInstance().getConfig().getConfigurationSection("mysql"));

        lobbyConfig = new LobbyConfig(YamlConfiguration.loadConfiguration(new File(getInstance().getDataFolder(), "lobby.yml")));
        knockbackConfig = new KnockbackConfig(getInstance().getDataFolder());

        mapManager = new MapManager(new File(getInstance().getDataFolder(), "maps"));

        gadgetManager = new GadgetManager(getInstance().getDataFolder());
    }
}
