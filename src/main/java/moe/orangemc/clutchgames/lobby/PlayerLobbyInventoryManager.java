package moe.orangemc.clutchgames.lobby;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerLobbyInventoryManager {
    private final Map<Player, LobbyInventoryGroup> inventoryGroupMap = new HashMap<>();

    public void createInventory(Player player) {
        inventoryGroupMap.put(player, new LobbyInventoryGroup(player));
    }

    public void openPlayInventory(Player player) {
        inventoryGroupMap.get(player).openPlayInventory();
    }

    public void openSettingInventory(Player player) {
        inventoryGroupMap.get(player).openSettingsInventory();
    }

    public void destory(Player player) {
        inventoryGroupMap.remove(player).destroyAll();
    }
}
