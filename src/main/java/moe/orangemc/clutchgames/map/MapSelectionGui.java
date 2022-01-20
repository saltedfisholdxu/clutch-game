package moe.orangemc.clutchgames.map;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.game.GameManager;
import moe.orangemc.clutchgames.util.ItemStackFactory;
import moe.orangemc.plugincommons.PluginCommons;
import moe.orangemc.plugincommons.inventory.PluginInventory;
import moe.orangemc.plugincommons.inventory.PluginInventoryManager;
import moe.orangemc.plugincommons.inventory.control.handler.InventoryHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class MapSelectionGui {
    private final PluginInventoryManager pim = PluginCommons.getPluginInventoryManager(ClutchGames.getInstance());

    private final PluginInventory selectionInventory;

    public MapSelectionGui(Player player) {
        selectionInventory = pim.createInventory(player, "地图选择", 6);
    }

    public void setupInventory(List<GameMap> maps) {
        int x = 0;
        int y = 0;
        for (GameMap map : maps) {
            selectionInventory.putButton(x, y, new ItemStackFactory(map.getIcon()).setAmount(1).setName(ChatColor.GREEN + map.getName()).build(), (whoClicked, buttonClicked) -> {
                GameManager gm = ClutchGames.getGameManager();
                switch (map.getMapType()) {
                    case NPC_KNOCKBACK:
                        gm.createNpcKnockbackGame(whoClicked, map);
                        break;
                    case KNOCKBACK:
                        gm.createKnockbackGame(whoClicked, map);
                        break;
                    case BRIDGE:
                        gm.createBridgeGame(whoClicked, map);
                        break;
                }
                whoClicked.closeInventory();
            });

            if (++x > 8) {
                x = 0;
                y ++;
            }
        }

        selectionInventory.setHandler(player -> pim.destroyInventory(selectionInventory));
    }

    public void open() {
        selectionInventory.open();
    }
}
