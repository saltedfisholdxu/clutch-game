package moe.orangemc.clutchgames.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ActionBarUtil {
    public static void sendActionBar(Player player, String msg) { // It is implemented by spigot later.....
        PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
        chat.getBytes().write(0, (byte) 2);
        chat.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\":\"" + msg + "\"}"));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, chat);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
