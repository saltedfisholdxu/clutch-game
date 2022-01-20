package moe.orangemc.clutchgames.map;

import moe.orangemc.clutchgames.util.Logger;

import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {
    private final Random mapSelector = new Random();

    private final List<GameMap> knockbackGameMaps = new ArrayList<>();
    private final List<GameMap> npcKnockbackGameMaps = new ArrayList<>();
    private final List<GameMap> bridgeGameMaps = new ArrayList<>();

    public MapManager(File mapFolder) {
        if (!mapFolder.exists()) {
            mapFolder.mkdirs();
        }
        File[] mapFiles = mapFolder.listFiles();
        if (mapFiles == null) {
            Logger.warn("There is no map in the folder, or the folder does even not exist");
        } else for (File mapFile : mapFiles) {
            GameMapLoader loader = new GameMapLoader(mapFile);
            try {
                GameMap gameMap = loader.load();
                addGameMap(gameMap);
            } catch (IOException e) {
                Logger.warn("Could not load map: " + mapFile);
                e.printStackTrace();
            }
        }
    }

    public void openKnockbackGameMapSelector(Player player) {
        MapSelectionGui gui = new MapSelectionGui(player);
        gui.setupInventory(this.knockbackGameMaps);
        gui.open();
    }

    public void openNpcKnockbackGameMapSelector(Player player) {
        MapSelectionGui gui = new MapSelectionGui(player);
        gui.setupInventory(this.npcKnockbackGameMaps);
        gui.open();
    }

    public void openBridgeGameMapSelector(Player player) {
        MapSelectionGui gui = new MapSelectionGui(player);
        gui.setupInventory(this.bridgeGameMaps);
        gui.open();
    }

    public GameMap randomKnockbackGameMap() {
        return knockbackGameMaps.get(mapSelector.nextInt(knockbackGameMaps.size()));
    }

    public GameMap randomNpcKnockbackGameMap() {
        return npcKnockbackGameMaps.get(mapSelector.nextInt(npcKnockbackGameMaps.size()));
    }

    public GameMap randomBridgeGameMap() {
        return bridgeGameMaps.get(mapSelector.nextInt(bridgeGameMaps.size()));
    }

    public void addGameMap(GameMap gameMap) {
        switch (gameMap.getMapType()) {
            case KNOCKBACK:
                knockbackGameMaps.add(gameMap);
                break;
            case NPC_KNOCKBACK:
                npcKnockbackGameMaps.add(gameMap);
                break;
            case BRIDGE:
                bridgeGameMaps.add(gameMap);
                break;
            default:
                Logger.warn("Unknown map type: " + gameMap.getMapType());
        }
    }
}
