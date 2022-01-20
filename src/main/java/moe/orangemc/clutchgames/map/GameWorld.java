package moe.orangemc.clutchgames.map;

import moe.orangemc.clutchgames.ClutchGames;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;

public class GameWorld {
    private final World world;
    private final GameMap map;

    public GameWorld(GameMap map) {
        this.map = map;
        this.world = map.createWorld();
    }

    public void teleportPlayer(Player player) {
        Location l = new Location(world, 0, 0, 0);
        l.add(map.getRelativeSpawn());
        if (map.getExtraLocation().length() != 0) {
            double dx = player.getLocation().getX() - getExtraLocation().getX();
            double dz = player.getLocation().getZ() - getExtraLocation().getZ();

            double knockbackAngle = Math.toDegrees(Math.atan2(-dx, dz)) + 180;
            l.setYaw((float) knockbackAngle);
        } else {
            l.setYaw((float) (ClutchGames.getKnockbackConfig().getRotation() + 180));
        }

        player.teleport(l);
        player.setBedSpawnLocation(l, true);
        player.setFallDistance(0);
    }

    public void destroy() {
        for (Player p : world.getPlayers()) {
            ClutchGames.getLobbyConfig().teleportToLobby(p);
        }

        Bukkit.unloadWorld(world, false);
        Bukkit.getWorlds().remove(world);
        deleteWorldFiles(world.getWorldFolder());
    }

    public Location getExtraLocation() {
        Location result = new Location(world, 0, 0, 0);
        result.add(map.getExtraLocation());
        return result;
    }

    public Location getSpawnLocation() {
        Location result = new Location(world, 0, 0, 0);
        result.add(map.getRelativeSpawn());
        return result;
    }

    private static void deleteWorldFiles(final File path) {
        if (path.exists() && path.isDirectory()) {
            final File[] files = path.listFiles();
            if (files == null) {
                return;
            }
            for (final File file : files) {
                if (file.isDirectory()) {
                    deleteWorldFiles(file);
                } else {
                    file.delete();
                }
            }
        }
        path.delete();
    }
}
