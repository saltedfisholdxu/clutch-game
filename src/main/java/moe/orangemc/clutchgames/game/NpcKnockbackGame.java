package moe.orangemc.clutchgames.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.gadget.GadgetManager;
import moe.orangemc.clutchgames.map.GameMap;
import moe.orangemc.clutchgames.util.ItemStackFactory;
import moe.orangemc.clutchgames.util.LocationUtil;
import moe.orangemc.plugincommons.scoreboard.ScoreboardList;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.InvocationTargetException;

public class NpcKnockbackGame extends Game {
    private int defaultNpcNoDamageTick = 11;
    private int defaultPlayerNoDamageTick = 11;

    private final NPC knockbackNpc;
    private boolean shouldAttack = false;
    private double furthestDistance = 0;
    private double currentDistance = 0;
    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();

    private final int noDamageTick;

    private int npcNoDamageTick = 0;

    public NpcKnockbackGame(Player player, GameMap map) {
        super(player, map);

        knockbackNpc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Karlatemp");
        knockbackNpc.setProtected(false);
        SkinTrait st = new SkinTrait();
        knockbackNpc.addTrait(st);
        st.setSkinName("Karlatemp", true);
        knockbackNpc.data().set(NPC.COLLIDABLE_METADATA, false);
        knockbackNpc.spawn(this.world.getExtraLocation().add(0.5, 0, 0.5));

        noDamageTick = player.getMaximumNoDamageTicks();

        Player entity = (Player) knockbackNpc.getEntity();
        entity.getEquipment().setItemInHand(new ItemStackFactory(Material.STICK).addEnchantment(Enchantment.ARROW_DAMAGE, 1).build());
        knockbackNpc.getDefaultGoalController().addGoal(new Goal() {
            @Override
            public void reset() {
                shouldAttack = false;
            }

            @Override
            public void run(GoalSelector goalSelector) {
                if (NpcKnockbackGame.this.isResting()) {
                    return;
                }

                knockbackNpc.faceLocation(player.getLocation());
                knockbackNpc.getNavigator().setTarget(world.getExtraLocation().add(0.5, 0, 0.5));

                Location npcLocation = knockbackNpc.getEntity().getLocation();
                if (!shouldAttack) {
                    return;
                }

                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || npcLocation.distance(player.getLocation()) > 4) {
                    shouldAttack = false;
                    return;
                }

                if (player.getNoDamageTicks() <= 0) {
                    PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ANIMATION);
                    packetContainer.getEntityModifier(knockbackNpc.getEntity().getWorld()).write(0, entity);
                    packetContainer.getIntegers().write(0, 0);
                    try {
                        pm.sendServerPacket(player, packetContainer);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    player.damage(0);

                    double knockbackAngle = npcLocation.getYaw();

                    player.setVelocity(ClutchGames.getKnockbackConfig().getKnockbackByRotation(player, ClutchGames.getMySQLDataSource().getDifficulty(player), knockbackAngle));
                }
            }

            @Override
            public boolean shouldExecute(GoalSelector goalSelector) {
                return true;
            }
        }, 114514);
    }

    @Override
    public void tick() {
        if (player.getLocation().getWorld() != world.getSpawnLocation().getWorld()) {
            world.teleportPlayer(player);
        }

        if (player.getLocation().getY() < 0) {
            world.teleportPlayer(player);
            shouldAttack = false;
            knockbackNpc.teleport(world.getExtraLocation().add(0.5, 0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
            clearBlocks();
        }
        if (knockbackNpc.getEntity().getLocation().getY() < 0) {
            knockbackNpc.teleport(world.getExtraLocation().add(0.5, 0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        GadgetManager gm = ClutchGames.getGadgetManager();
        if (player.getMaximumNoDamageTicks() != defaultPlayerNoDamageTick) {
            player.setMaximumNoDamageTicks(defaultPlayerNoDamageTick);
        }
        player.getInventory().setItem(0, gm.getStickGadget(player));
        player.getInventory().setItem(1, gm.getBlockGadget(player));
        player.getInventory().setItem(2, gm.getBlockGadget(player));
        player.getInventory().setItem(6, InGameItem.REST_BUTTON);
        player.getInventory().setItem(7, InGameItem.RETURN_LOBBY);

        if (npcNoDamageTick > 0) {
            npcNoDamageTick --;
        }
        if (player.isOnGround()) {
            furthestDistance = Math.max(furthestDistance, LocationUtil.distanceWithoutYAxis(player.getLocation(), world.getSpawnLocation()));
            currentDistance = LocationUtil.distanceWithoutYAxis(player.getLocation(), world.getSpawnLocation());
        }
    }

    @Override
    public void tickScoreboard(ScoreboardList scoreboardList) {
        KnockbackGame.updateScoreboard(scoreboardList, player.getName(), furthestDistance, currentDistance, world.getSpawnLocation(), player.getLocation(), ClutchGames.getMySQLDataSource().getDifficulty(player), ClutchGames.getMySQLDataSource().getKnockback(player));
    }

    public NPC getKnockbackNpc() {
        return knockbackNpc;
    }

    public void attackNpc() {
        if (isResting()) {
            toggleResting();
        }
        npcNoDamageTick = defaultNpcNoDamageTick;
        this.shouldAttack = true;
    }

    public boolean shouldNpcDamaged() {
        return npcNoDamageTick <= 0;
    }

    @Override
    public void destroy() {
        super.destroy();
        knockbackNpc.destroy();
        ClutchGames.getMySQLDataSource().setClutchNpcRecord(player, furthestDistance);
        player.setMaximumNoDamageTicks(noDamageTick);
    }

    public void setDefaultNpcNoDamageTick(int defaultNpcNoDamageTick) {
        this.defaultNpcNoDamageTick = defaultNpcNoDamageTick;
    }

    public void setDefaultPlayerNoDamageTick(int defaultPlayerNoDamageTick) {
        this.defaultPlayerNoDamageTick = defaultPlayerNoDamageTick;
    }
}
