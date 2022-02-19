package moe.orangemc.clutchgames.knockback.custom;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.database.MySQLDataSource;
import moe.orangemc.clutchgames.listener.CustomKnockbackSetSessionListener;
import moe.orangemc.clutchgames.util.Vector2d;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public enum CustomKnockbackSetStage {
    VERTICAL {
        @Override
        public CustomKnockbackStagedExecutor getExecutor(Player player) {
            return new CustomKnockbackStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(CustomKnockbackSetSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请输入垂直方向的击退大小 (当前: " + DATA_SOURCE.getKnockback(player, session.getGameType()).getY() + ")");
                    CustomKnockbackSetSessionListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, CustomKnockbackSetSession session) {
                    event.setCancelled(true);
                    try {
                        double y = Double.parseDouble(event.getMessage());
                        session.setY(y);
                        session.nextStage(player);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "无效的数字! ");
                        CustomKnockbackSetSessionListener.registerHandler(this);
                    }
                }
            };
        }
    },
    HORIZONTAL {
        @Override
        public CustomKnockbackStagedExecutor getExecutor(Player player) {
            return new CustomKnockbackStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(CustomKnockbackSetSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请输入水平方向的击退大小 (当前: " + DATA_SOURCE.getKnockback(player, session.getGameType()).getX() + ")");;
                    CustomKnockbackSetSessionListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, CustomKnockbackSetSession session) {
                    event.setCancelled(true);
                    try {
                        double x = Double.parseDouble(event.getMessage());
                        session.setX(x);
                        session.nextStage(player);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "无效的数字! ");
                        CustomKnockbackSetSessionListener.registerHandler(this);
                    }
                }
            };
        }
    },
    FINISH {
        @Override
        public CustomKnockbackStagedExecutor getExecutor(Player player) {
            return new CustomKnockbackStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(CustomKnockbackSetSession session) {
                    execute(null, session);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, CustomKnockbackSetSession session) {
                    Vector2d vec = session.build();
                    DATA_SOURCE.setKnockback(player, vec, session.getGameType());
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "你的击退被设为 水平: " + vec.getX() + ", 垂直: " + vec.getY());
                    ClutchGames.getCustomKnockbackSessionManager().removeSession(player);
                }
            };
        }
    };

    private static final MySQLDataSource DATA_SOURCE = ClutchGames.getMySQLDataSource();

    public CustomKnockbackSetStage getNextStage() {
        switch (this) {
            case VERTICAL:
                return HORIZONTAL;
            case HORIZONTAL:
                return FINISH;
            default:
                throw new IllegalStateException("Unknown stage.");
        }
    }

    public CustomKnockbackStagedExecutor getExecutor(Player player) {
        throw new IllegalStateException("Unknown stage.");
    }
}
