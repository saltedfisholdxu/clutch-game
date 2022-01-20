package moe.orangemc.clutchgames.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LobbyConfig {
    private Location lobbyLocation;

    public LobbyConfig(FileConfiguration config) {
        if (config == null || config.getString("world") == null) {
            lobbyLocation = null;
        } else {
            lobbyLocation = new Location(Bukkit.getWorld(config.getString("world")), config.getInt("x") + 0.5, config.getInt("y"), config.getInt("z") + 0.5);
            Bukkit.getWorld(config.getString("world")).setDifficulty(Difficulty.PEACEFUL);
        }
    }

    public void teleportToLobby(Player player) {
        if (lobbyLocation == null) {
            return;
        }
        player.teleport(lobbyLocation);
        player.setBedSpawnLocation(lobbyLocation);
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }
}
