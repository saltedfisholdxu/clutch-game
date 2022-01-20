package moe.orangemc.clutchgames.gadget;

import moe.orangemc.clutchgames.util.ItemStackFactory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class Gadget {
    private final GadgetType gadgetType;
    private final ItemStack itemStack;
    private final String permissionRequired;
    private final int economyCost;
    private final int pointsCost;

    public Gadget(GadgetType gadgetType, ConfigurationSection cs) {
        this.gadgetType = gadgetType;
        this.itemStack = new ItemStackFactory(Material.getMaterial(cs.getString("type"))).setName(ChatColor.translateAlternateColorCodes('&', cs.getString("name"))).setLore(cs.getStringList("lore").stream().map((s) -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList())).build();
        this.permissionRequired = cs.getString("permission");
        this.economyCost = cs.getInt("cost");
        this.pointsCost = cs.getInt("point-cost");
    }

    public GadgetType getGadgetType() {
        return gadgetType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getPermissionRequired() {
        return permissionRequired;
    }

    public int getEconomyCost() {
        return economyCost;
    }

    public int getPointsCost() {
        return pointsCost;
    }
}
