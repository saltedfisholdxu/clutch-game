package moe.orangemc.clutchgames.map;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameMap {
    private final MapType mapType;

    private final String name;
    private final Material icon;

    private final List<Material> blockList = new LinkedList<>();
    private final List<Byte> blockDataList = new LinkedList<>();

    private final int xSize;
    private final int ySize;
    private final int zSize;

    private final Vector relativeSpawn = new Vector();
    private final Vector extraLocation = new Vector();

    private final Random nameGenerator = new Random();

    public GameMap(MapType mapType, String name, Material icon, int xSize, int ySize, int zSize) {
        this.mapType = mapType;
        this.name = name;
        this.icon = icon;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    public void writeBlock(Material material, byte blockData) {
        blockList.add(material);
        blockDataList.add(blockData);
    }

    public List<Material> getBlockList() {
        return blockList;
    }

    public List<Byte> getBlockDataList() {
        return blockDataList;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public int getZSize() {
        return zSize;
    }

    public MapType getMapType() {
        return mapType;
    }

    public void writeSpawn(int x, int y, int z) {
        this.relativeSpawn.zero();
        this.relativeSpawn.add(new Vector(x, y, z));
    }

    public void writeExtraLocation(int x, int y, int z) {
        this.extraLocation.zero();
        this.extraLocation.add(new Vector(x, y, z));
    }

    public Vector getRelativeSpawn() {
        return relativeSpawn;
    }

    public Vector getExtraLocation() {
        return extraLocation;
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public World createWorld() {
        World world = Bukkit.createWorld(new WorldCreator(Integer.toHexString(nameGenerator.nextInt())).generator(new ChunkGenerator() {
            @Override
            public boolean canSpawn(World world, int x, int z) {
                return Math.abs(x) < 5 && Math.abs(z) < 5;
            }

            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {
                BlockPopulator bp = new BlockPopulator() {
                    @Override
                    public void populate(World world, Random random, Chunk source) {

                    }
                };
                return Collections.singletonList(bp);
            }

            @Override
            public Location getFixedSpawnLocation(World world, Random random) {
                return new Location(world, 0, 0, 0).add(getRelativeSpawn());
            }

            @Override
            public byte[] generate(World world, Random random, int x, int z) {
                return new byte[16 * 256 * 16];
            }
        }));
        int counter = 0;
        for (int x = 0; x < getXSize(); x ++) {
            for (int y = 0; y < getYSize(); y ++) {
                for (int z = 0; z < getZSize(); z ++) {
                    Material blockType = blockList.get(counter);
                    byte data = blockDataList.get(counter ++);
                    Block b = world.getBlockAt(new Location(world, x, y, z));
                    b.setType(blockType);
                    b.setData(data);
                }
            }
        }
        world.setDifficulty(Difficulty.PEACEFUL);
        return world;
    }
}
