package moe.orangemc.clutchgames.util;

import org.apache.commons.lang.Validate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemStackFactory {
    private final Material material;
    private int amount = 1;
    private String name = null;
    private List<String> lore = null;
    private final Map<Enchantment, Integer> enchantmentMap = new HashMap<>();

    public ItemStackFactory(Material material) {
        Validate.notNull(material, "material cannot be null");
        this.material = material;
    }

    public ItemStackFactory setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStackFactory setName(String name) {
        this.name = name;
        return this;
    }

    public ItemStackFactory setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemStackFactory addLore(String loreLine) {
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }
        this.lore.add(loreLine);
        return this;
    }

    public ItemStackFactory addEnchantment(Enchantment enc, int level) {
        enchantmentMap.put(enc, level);
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(material, amount);

        ItemMeta im = is.getItemMeta();
        if (im == null) {
            im = Bukkit.getItemFactory().getItemMeta(material);
        }
        if (name != null) {
            im.setDisplayName(name);
        }
        if (lore != null) {
            im.setLore(lore);
        }
        ItemMeta finalIm = im;
        enchantmentMap.forEach((enc, lvl) -> finalIm.addEnchant(enc, lvl, true));
        is.setItemMeta(im);
        return is;
    }
}
