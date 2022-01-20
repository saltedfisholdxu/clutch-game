package moe.orangemc.clutchgames.map.creation;

import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.listener.MapCreationListener;
import moe.orangemc.clutchgames.map.GameMap;
import moe.orangemc.clutchgames.map.GameMapWriter;
import moe.orangemc.clutchgames.map.MapType;
import moe.orangemc.clutchgames.util.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.io.IOException;

public enum MapCreationStage {
    MAP_TYPE {
        @Override
        public MapCreationStage getNextStage(MapCreationSession session) {
            return MAP_NAME;
        }

        @Override
        public MapCreationStagedExecutor getStagedExecutor(Player player) {
            return new MapCreationStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(MapCreationSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请输入地图类型");
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "可用的地图类型有: " + ChatColor.GREEN + "clutch(方块自救)" + ChatColor.YELLOW + ", " + ChatColor.GREEN + "NPCClutch(NPC自救)" + ChatColor.YELLOW + ", " + ChatColor.GREEN + "bridge(搭路)");
                    MapCreationListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, MapCreationSession session) {
                    String content = event.getMessage();
                    event.setCancelled(true);
                    switch (content) {
                        case "clutch":
                            session.setType(MapType.KNOCKBACK);
                            session.processNext(event.getPlayer());
                            return;
                        case "NPCClutch":
                            session.setType(MapType.NPC_KNOCKBACK);
                            session.processNext(event.getPlayer());
                            return;
                        case "bridge":
                            session.setType(MapType.BRIDGE);
                            session.processNext(event.getPlayer());
                            return;
                        default:
                            event.getPlayer().sendMessage(ClutchGames.PREFIX + ChatColor.RED + "无效的地图类型");
                            MapCreationListener.registerHandler(this);
                    }
                }
            };
        }
    },
    MAP_NAME {
        @Override
        public MapCreationStage getNextStage(MapCreationSession session) {
            return ICON;
        }

        @Override
        public MapCreationStagedExecutor getStagedExecutor(Player player) {
            return new MapCreationStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(MapCreationSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请输入地图名");
                    MapCreationListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, MapCreationSession session) {
                    event.setCancelled(true);
                    session.setName(event.getMessage());
                    session.processNext(player);
                }
            };
        }
    },
    ICON {
        @Override
        public MapCreationStage getNextStage(MapCreationSession session) {
            return CORNER_1;
        }

        @Override
        public MapCreationStagedExecutor getStagedExecutor(Player player) {
            return new MapCreationStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(MapCreationSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请手拿地图的图标并输入\"" + ChatColor.GREEN + "next" + ChatColor.YELLOW + "\"");
                    MapCreationListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, MapCreationSession session) {
                    event.setCancelled(true);
                    if (!"next".equals(event.getMessage())) {
                        player.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "请输入next");
                        return;
                    }
                    session.setIcon(player.getEquipment().getItemInHand().getType());
                    session.processNext(player);
                }
            };
        }
    },
    CORNER_1 {
        @Override
        public MapCreationStage getNextStage(MapCreationSession session) {
            return CORNER_2;
        }

        @Override
        public MapCreationStagedExecutor getStagedExecutor(Player player) {
            return new MapCreationStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(MapCreationSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请飞到地图的一角并输入\"" + ChatColor.GREEN + "next" + ChatColor.YELLOW + "\"");
                    MapCreationListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, MapCreationSession session) {
                    if (!event.getMessage().equals("next")) {
                        MapCreationListener.registerHandler(this);
                        return;
                    }

                    event.setCancelled(true);
                    session.setCorner1(event.getPlayer().getLocation());
                    session.processNext(player);
                }
            };
        }
    },
    CORNER_2 {
        @Override
        public MapCreationStage getNextStage(MapCreationSession session) {
            return SPAWN;
        }
        @Override
        public MapCreationStagedExecutor getStagedExecutor(Player player) {
            return new MapCreationStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(MapCreationSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请飞到地图的另一角并输入\"" + ChatColor.GREEN + "next" + ChatColor.YELLOW + "\"");
                    MapCreationListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, MapCreationSession session) {
                    if (!event.getMessage().equals("next")) {
                        MapCreationListener.registerHandler(this);
                        return;
                    }

                    event.setCancelled(true);
                    session.setCorner2(event.getPlayer().getLocation());
                    session.processNext(player);
                }
            };
        }
    },
    SPAWN {
        @Override
        public MapCreationStage getNextStage(MapCreationSession session) {
            if (session.getType() == MapType.NPC_KNOCKBACK) {
                return NPC_SPAWN;
            }
            if (session.getType() == MapType.BRIDGE) {
                return BRIDGE_DESTINATION;
            }
            return FINISH;
        }

        @Override
        public MapCreationStagedExecutor getStagedExecutor(Player player) {
            return new MapCreationStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(MapCreationSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请飞到地图的出生点并输入\"" + ChatColor.GREEN + "next" + ChatColor.YELLOW + "\"");
                    MapCreationListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, MapCreationSession session) {
                    if (!event.getMessage().equals("next")) {
                        MapCreationListener.registerHandler(this);
                        return;
                    }

                    event.setCancelled(true);
                    session.setSpawn(event.getPlayer().getLocation());
                    session.processNext(player);
                }
            };
        }
    },
    NPC_SPAWN {
        @Override
        public MapCreationStage getNextStage(MapCreationSession session) {
            return FINISH;
        }

        @Override
        public MapCreationStagedExecutor getStagedExecutor(Player player) {
            return new MapCreationStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(MapCreationSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请飞到NPC的出生点并输入\"" + ChatColor.GREEN + "next" + ChatColor.YELLOW + "\"");
                    MapCreationListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, MapCreationSession session) {
                    if (!event.getMessage().equals("next")) {
                        MapCreationListener.registerHandler(this);
                        return;
                    }

                    session.setExtraLocation(event.getPlayer().getLocation());
                    session.processNext(player);
                }
            };
        }
    },
    BRIDGE_DESTINATION {
        @Override
        public MapCreationStage getNextStage(MapCreationSession session) {
            return FINISH;
        }

        @Override
        public MapCreationStagedExecutor getStagedExecutor(Player player) {
            return new MapCreationStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(MapCreationSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.YELLOW + "请飞到终点并输入\"" + ChatColor.GREEN + "next" + ChatColor.YELLOW + "\"");
                    MapCreationListener.registerHandler(this);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, MapCreationSession session) {
                    if (!event.getMessage().equals("next")) {
                        MapCreationListener.registerHandler(this);
                        return;
                    }
                    event.setCancelled(true);

                    session.setExtraLocation(event.getPlayer().getLocation());
                    session.processNext(player);
                }
            };
        }
    },
    FINISH {
        @Override
        public MapCreationStagedExecutor getStagedExecutor(Player player) {
            return new MapCreationStagedExecutor() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void preExecute(MapCreationSession session) {
                    player.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "正在创建地图, 请稍后...");
                    execute(null, session);
                }

                @Override
                public void execute(AsyncPlayerChatEvent event, MapCreationSession session) {
                    // finish the session
                    GameMap map = session.build();
                    ClutchGames.getMapManager().addGameMap(map);
                    ClutchGames.getMapCreationSessionManager().removeSession(player);
                    File mapFolder = new File(ClutchGames.getInstance().getDataFolder(), "maps");
                    try {
                        new GameMapWriter(mapFolder, map.getMapType()).writeMap(map);
                        player.sendMessage(ClutchGames.PREFIX + ChatColor.GREEN + "地图创建成功! ");
                    } catch (IOException e) {
                        player.sendMessage(ClutchGames.PREFIX + ChatColor.RED + "在创建地图时出现了错误, 请检查控制台");
                        Logger.warn("Unable to save map file");
                        e.printStackTrace();
                    }
                    ClutchGames.getMapCreationSessionManager().removeSession(player);
                }
            };
        }
    };

    public MapCreationStage getNextStage(MapCreationSession session) {
        throw new IllegalStateException("Unknown stage.");
    }

    public MapCreationStagedExecutor getStagedExecutor(Player player) {
        throw new IllegalStateException("Unknown stage.");
    }
}
