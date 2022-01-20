package moe.orangemc.clutchgames.gadget;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.database.MySQLDataSource;
import moe.orangemc.clutchgames.util.ItemStackFactory;
import moe.orangemc.clutchgames.util.PlayerPointUtil;
import moe.orangemc.clutchgames.util.VaultUtil;
import moe.orangemc.plugincommons.PluginCommons;
import moe.orangemc.plugincommons.inventory.PluginInventory;
import moe.orangemc.plugincommons.inventory.PluginInventoryManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class GadgetInventoryGroup {
    private final PluginInventoryManager pim = PluginCommons.getPluginInventoryManager(ClutchGames.getInstance());
    private final MySQLDataSource mySQLDataSource = ClutchGames.getMySQLDataSource();

    private final Player player;
    private final PluginInventory gadgetMenu;
    private final PluginInventory blockMenu;
    private final PluginInventory stickMenu;

    public GadgetInventoryGroup(Player player, Map<String, Gadget> stickGadget, Map<String, Gadget> blockGadget) {
        this.player = player;

        blockMenu = pim.createInventory(player, "方块皮肤", 6);
        initMenu(blockGadget, blockMenu);
        stickMenu = pim.createInventory(player, "击退棒皮肤", 6);
        initMenu(stickGadget, stickMenu);

        gadgetMenu = pim.createInventory(player, "皮肤", 3);
        gadgetMenu.putButton(1, 1, new ItemStackFactory(Material.STICK).setName(ChatColor.YELLOW + "击退棒皮肤").build(), (whoClicked, buttonClicked) -> stickMenu.open());
        gadgetMenu.putButton(7, 1, new ItemStackFactory(Material.SANDSTONE).setName(ChatColor.YELLOW + "方块皮肤").build(), (whoClicked, buttonClicked) -> blockMenu.open());
    }

    private void initMenu(Map<String, Gadget> gadgets, PluginInventory menu) {
        AtomicInteger x = new AtomicInteger();
        AtomicInteger y = new AtomicInteger();
        gadgets.forEach((id, gadget) -> {
            ItemStack is = buildItemStack(id, gadget);

            menu.putButton(x.get(), y.get(), is, (whoClicked, buttonClicked) -> {
                try {
                    if (gadget.getPermissionRequired().isEmpty() || player.hasPermission(gadget.getPermissionRequired())) {
                        player.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "你选择了这个物品! ");
                        if (gadget.getGadgetType() == GadgetType.STICK) {
                            mySQLDataSource.setPlayerStick(player, id);
                        } else {
                            mySQLDataSource.setPlayerBlock(player, id);
                        }
                        return;
                    }
                    if (VaultUtil.haveMoney(player, gadget.getEconomyCost()) && PlayerPointUtil.havePoints(player, gadget.getPointsCost())) {
                        VaultUtil.takeMoney(player, gadget.getEconomyCost());
                        PlayerPointUtil.takePoints(player, gadget.getPointsCost());
                        player.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "你购买了这个物品, 再次点击以选择它");
                    }
                } finally {
                    buttonClicked.setItem(buildItemStack(id, gadget));
                }
            });

            x.incrementAndGet();
            if (x.get() > 8) {
                x.set(0);
                y.incrementAndGet();
            }
        });
    }

    private ItemStack buildItemStack(String id, Gadget gadget) {
        ItemStack is = gadget.getItemStack().clone();
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            im = Bukkit.getItemFactory().getItemMeta(is.getType());
        }
        List<String> lore = im.getLore();
        if (lore == null) {
            lore = new LinkedList<>();
        }
        lore.add("");

        boolean used = false;
        switch (gadget.getGadgetType()) {
            case BLOCK:
                used = Objects.equals(mySQLDataSource.getPlayerBlock(player), id);
                break;
            case STICK:
                used = Objects.equals(mySQLDataSource.getPlayerStick(player), id);
                break;
        }
        if (used) {
            lore.add(ChatColor.GREEN + "已选中");
        } else if (player.hasPermission(gadget.getPermissionRequired())) {
            lore.add(ChatColor.GREEN + "点击以使用");
        } else {
            if (gadget.getEconomyCost() > 0) {
                lore.add(ChatColor.YELLOW + "点击以花费" + ChatColor.GREEN + gadget.getEconomyCost() + ChatColor.YELLOW + "金币购买");
            }
            if (gadget.getPointsCost() > 0) {
                lore.add(ChatColor.YELLOW + "点击以花费" + ChatColor.YELLOW + gadget.getPointsCost() + ChatColor.YELLOW + "点券购买");
            }
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public void open() {
        gadgetMenu.open();
    }

    public void destroy() {
        pim.destroyInventory(this.blockMenu);
        pim.destroyInventory(this.gadgetMenu);
        pim.destroyInventory(this.stickMenu);
    }
}
