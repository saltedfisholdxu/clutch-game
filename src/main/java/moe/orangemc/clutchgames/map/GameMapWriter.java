package moe.orangemc.clutchgames.map;

import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class GameMapWriter {
    private final File gameMapFile;

    public GameMapWriter(File mapFolder, MapType type) throws IOException {
        if (!mapFolder.exists()) {
            mapFolder.mkdirs();
        }
        this.gameMapFile = new File(mapFolder, Integer.toHexString(hashCode()) + type.getAlias() + ".dat");
        if (!this.gameMapFile.exists()) {
            gameMapFile.createNewFile();
        }
    }

    public void writeMap(GameMap gameMap) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(gameMapFile)))) {
            dos.writeInt(gameMap.getMapType().ordinal());
            dos.writeUTF(gameMap.getName());
            dos.writeInt(gameMap.getIcon().ordinal());

            dos.writeInt(gameMap.getXSize());
            dos.writeInt(gameMap.getYSize());
            dos.writeInt(gameMap.getZSize());

            Vector spawn = gameMap.getRelativeSpawn();
            dos.writeInt(spawn.getBlockX());
            dos.writeInt(spawn.getBlockY());
            dos.writeInt(spawn.getBlockZ());

            Vector extra = gameMap.getExtraLocation();
            dos.writeInt(extra.getBlockX());
            dos.writeInt(extra.getBlockY());
            dos.writeInt(extra.getBlockZ());

            List<Material> blocks = gameMap.getBlockList();
            List<Byte> blockDatas = gameMap.getBlockDataList();

            for (int i = 0; i < gameMap.getXSize() * gameMap.getYSize() * gameMap.getZSize(); i++) {
                Material blockMaterial = blocks.get(i);
                byte data = blockDatas.get(i);
                dos.writeInt(blockMaterial.ordinal());
                dos.writeByte(data);
            }
        }
    }
}
