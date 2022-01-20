package moe.orangemc.clutchgames.map.creation;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.map.GameMap;
import moe.orangemc.clutchgames.map.MapType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MapCreationSession {
    private MapCreationStage stage;

    private MapType type;
    private String name;
    private Material icon;

    private Location corner1;
    private Location corner2;
    private Location spawn;
    private Location extraLocation = new Location(null, 0, 0, 0);

    public void setType(MapType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setExtraLocation(Location extraLocation) {
        this.extraLocation = extraLocation;
    }

    public GameMap build() {
        World world = corner1.getWorld();
        if (world == null || !corner2.getWorld().equals(world)) {
            throw new IllegalStateException("Wrong world!");
        }

        int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());

        int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        int dx = xMax - xMin;
        int dy = yMax - yMin;
        int dz = zMax - zMin;

        GameMap gameMap = new GameMap(type, name, icon, dx, dy, dz);

        for (int x = xMin; x < xMax; x ++) {
            for (int y = yMin; y < yMax; y ++) {
                for (int z = zMin; z < zMax; z ++) {
                    Block block = world.getBlockAt(x, y, z);
                    gameMap.writeBlock(block.getType(), block.getData());
                }
            }
        }

        gameMap.writeSpawn(spawn.getBlockX() - xMin, spawn.getBlockY() - yMin, spawn.getBlockZ() - zMin);
        gameMap.writeExtraLocation(extraLocation.getBlockX() - xMin, extraLocation.getBlockY() - yMin, extraLocation.getBlockZ() - zMin);

        return gameMap;
    }

    public void processNext(Player player) {
        stage = stage.getNextStage(this);

        // delay a bit to avoid java.util.ConcurrentModificationException
        Bukkit.getScheduler().runTaskLater(ClutchGames.getInstance(), () -> stage.getStagedExecutor(player).preExecute(this), 1);
    }

    public void start(Player player) {
        stage = MapCreationStage.MAP_TYPE;
        stage.getStagedExecutor(player).preExecute(this);
    }

    public MapType getType() {
        return type;
    }
}
