package moe.orangemc.clutchgames.map;

import org.bukkit.Material;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class GameMapLoader {
    private final File gameMapFile;

    public GameMapLoader(File mapFile) {
        this.gameMapFile = mapFile;
    }

    public GameMap load() throws IOException {
        try (DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(gameMapFile)))) {
            MapType mapType = MapType.values()[dis.readInt() - 1];
            String name = dis.readUTF();
            Material icon = Material.values()[dis.readInt()];

            int xSize = dis.readInt();
            int ySize = dis.readInt();
            int zSize = dis.readInt();
            GameMap map = new GameMap(mapType, name, icon, xSize, ySize, zSize);

            int spawnX = dis.readInt();
            int spawnY = dis.readInt();
            int spawnZ = dis.readInt();

            map.writeSpawn(spawnX, spawnY, spawnZ);

            int extraX = dis.readInt();
            int extraY = dis.readInt();
            int extraZ = dis.readInt();
            map.writeExtraLocation(extraX, extraY, extraZ);

            for (int i = 0; i < xSize * ySize * zSize; i ++) {
                map.writeBlock(Material.values()[dis.readInt()], dis.readByte());
            }
            return map;
        }
    }
}
