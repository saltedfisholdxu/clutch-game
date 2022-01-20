package moe.orangemc.clutchgames.gadget;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.database.MySQLDataSource;
import moe.orangemc.clutchgames.util.Logger;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GadgetManager {
    private final MySQLDataSource mySQLDataSource = ClutchGames.getMySQLDataSource();
    private final Map<String, Gadget> stickGadgets;
    private final Map<String, Gadget> blockGadgets;

    private final Map<Player, GadgetInventoryGroup> inventoryGroupMap = new HashMap<>();

    public GadgetManager(File dataFolder) {
        stickGadgets = new GadgetLoader(new File(dataFolder, "stick.yml")).readConfiguration(GadgetType.STICK);
        if (!stickGadgets.containsKey("default")) {
            throw new IllegalStateException("The gadget list must have default sets.");
        }
        blockGadgets = new GadgetLoader(new File(dataFolder, "block.yml")).readConfiguration(GadgetType.BLOCK);
        if (!blockGadgets.containsKey("default")) {
            throw new IllegalStateException("The gadget list must have default sets.");
        }
    }

    public ItemStack getStickGadget(Player player) {
        String id = mySQLDataSource.getPlayerStick(player);
        if (!stickGadgets.containsKey(id)) {
            mySQLDataSource.setPlayerStick(player, "default");
            id = "default";
        }
        ItemStack stickClone = stickGadgets.get(id).getItemStack().clone();
        ItemMeta im = stickClone.getItemMeta();
        if (im == null) {
            im = Bukkit.getItemFactory().getItemMeta(stickClone.getType());
        }
        im.addEnchant(Enchantment.KNOCKBACK, 1, true);
        stickClone.setItemMeta(im);
        return stickClone;
    }

    public ItemStack getBlockGadget(Player player) {
        String id = mySQLDataSource.getPlayerBlock(player);
        if (!blockGadgets.containsKey(id)) {
            mySQLDataSource.setPlayerBlock(player, "default");
            id = "default";
        }
        ItemStack is = blockGadgets.get(id).getItemStack().clone();
        is.setAmount(64);
        return is;
    }

    public void addInventoryGroup(Player player) {
        this.inventoryGroupMap.put(player, new GadgetInventoryGroup(player, stickGadgets, blockGadgets));
    }

    public void openInventory(Player player) {
        this.inventoryGroupMap.get(player).open();
    }

    public void removeInventoryGroup(Player player) {
        if (this.inventoryGroupMap.containsKey(player)) {
            this.inventoryGroupMap.get(player).destroy();
        }
        this.inventoryGroupMap.remove(player);
    }
}
