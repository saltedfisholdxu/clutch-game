package moe.orangemc.clutchgames.database;

import moe.orangemc.clutchgames.game.GameType;
import moe.orangemc.clutchgames.knockback.KnockbackDifficulty;
import moe.orangemc.clutchgames.util.Logger;
import moe.orangemc.clutchgames.util.Vector2d;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MySQLDataSource {
    private final Map<OfflinePlayer, CachedPlayerSqlResult> cachedResultMap = new HashMap<>();

    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String database;

    public MySQLDataSource(ConfigurationSection section) {
        this.host = section.getString("host");
        this.port = section.getString("port");
        this.username = section.getString("user");
        this.password = section.getString("password");
        this.database = section.getString("database");

        initializeTable();
    }

    private Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", username, password);
        } catch (Exception e) {
            Logger.warn("Could not get connection of " + "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" + " with username: " + username + " and password: " + password);
            throw new RuntimeException(e);
        }
    }

    private void initializeTable() {
        try (Connection c = getConnection()) {
            c.prepareStatement("create table if not exists `knockback` (" +
                    "`id` int auto_increment," +
                    "`uuid` varchar(64) not null," +
                    "`difficulty` int not null," +
                    "primary key (`id`)" +
                    ");").executeUpdate();
            c.prepareStatement("create table if not exists `knockback_npc` (" +
                    "`id` int auto_increment," +
                    "`uuid` varchar(64) not null," +
                    "`difficulty` int not null," +
                    "primary key (`id`)" +
                    ");").executeUpdate();
            c.prepareStatement("create table if not exists `bridge_record` (" +
                    "`id` int auto_increment," +
                    "`uuid` varchar(64) not null," +
                    "`time` int not null," +
                    "primary key (`id`)" +
                    ");").executeUpdate();
            c.prepareStatement("create table if not exists `custom_knockback` (" +
                    "`id` int auto_increment," +
                    "`uuid` varchar(64) not null," +
                    "`vertical` double not null," +
                    "`horizontal` double not null," +
                    "primary key (`id`)" +
                    ");").executeUpdate();
            c.prepareStatement("create table if not exists `custom_knockback_npc` (" +
                    "`id` int auto_increment," +
                    "`uuid` varchar(64) not null," +
                    "`vertical` double not null," +
                    "`horizontal` double not null," +
                    "primary key (`id`)" +
                    ");").executeUpdate();
            c.prepareStatement("create table if not exists `knockback_timer` (" +
                    "`id` int auto_increment," +
                    "`uuid` varchar(64) not null," +
                    "`time` int not null," +
                    "primary key (`id`)" +
                    ");").executeUpdate();
            c.prepareStatement("create table if not exists `knockback_record` (" +
                    "`id` int auto_increment," +
                    "`uuid` varchar(64) not null," +
                    "`normal_distance` double not null," +
                    "`npc_distance` double not null," +
                    "primary key (`id`)" +
                    ");").executeUpdate();
            c.prepareStatement("create table if not exists `gadgets` (" +
                    "`id` int auto_increment," +
                    "`uuid` varchar(64) not null," +
                    "`block_gadget` varchar(64) not null," +
                    "`stick_gadget` varchar(64) not null," +
                    "primary key (`id`)" +
                    ");").executeUpdate();
            c.prepareStatement("create table if not exists `knockback_times` (" +
                    "`id` int auto_increment," +
                    "`uuid` varchar(64) not null," +
                    "`times` int not null," +
                    "primary key (`id`)" +
                    ");").executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean havePutDifficulty(OfflinePlayer player, GameType gameType) {
        String tableName = "knockback";
        if (gameType == GameType.NPC_KNOCKBACK) {
            tableName += "_npc";
        }

        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `" + tableName + "` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            return rs.first();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean haveBridgeRecord(OfflinePlayer player) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `bridge_record` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            return rs.first();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putDifficulty(OfflinePlayer player, KnockbackDifficulty difficulty, GameType gameType) {
        boolean alreadyHave = havePutDifficulty(player, gameType);
        this.cachedResultMap.remove(player);
        String tableName = "knockback";
        if (gameType == GameType.NPC_KNOCKBACK) {
            tableName += "_npc";
        }
        try (Connection c = getConnection()) {
            PreparedStatement putStatement;
            if (alreadyHave) {
                putStatement = c.prepareStatement("update `" + tableName + "` set difficulty=? where uuid=?;");
                putStatement.setString(2, player.getUniqueId().toString());
                putStatement.setInt(1, difficulty.ordinal());
            } else {
                putStatement = c.prepareStatement("insert into `" + tableName + "` (uuid,difficulty) values (?,?);");
                putStatement.setString(1, player.getUniqueId().toString());
                putStatement.setInt(2, difficulty.ordinal());
            }
            putStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public KnockbackDifficulty getDifficulty(OfflinePlayer player, GameType gameType) {
        if (cachedResultMap.containsKey(player)) {
            if (gameType == GameType.NPC_KNOCKBACK && cachedResultMap.get(player).getNpcDifficulty() != null) {
                return cachedResultMap.get(player).getNpcDifficulty();
            } else if (cachedResultMap.get(player).getDifficulty() != null) {
                return cachedResultMap.get(player).getDifficulty();
            }
        }
        if (!havePutDifficulty(player, gameType)) {
            putDifficulty(player, KnockbackDifficulty.EASY, gameType);
            return KnockbackDifficulty.EASY;
        }
        String tableName = "knockback";
        if (gameType == GameType.NPC_KNOCKBACK) {
            tableName += "_npc";
        }
        try (Connection c = getConnection()) {
            PreparedStatement fetchStatement = c.prepareStatement("select * from `" + tableName + "` where uuid=?;");
            fetchStatement.setString(1, player.getUniqueId().toString());
            ResultSet rs = fetchStatement.executeQuery();
            rs.first();
            KnockbackDifficulty difficulty = KnockbackDifficulty.values()[rs.getInt("difficulty")];
            updateCache(player, (result) -> result.setDifficulty(difficulty));
            return difficulty;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean haveTimer(OfflinePlayer player) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `knockback_timer` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            return ps.executeQuery().next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setTimer(OfflinePlayer player, int ticks) {
        boolean alreadyHave = haveTimer(player);
        try (Connection c = getConnection()) {
            if (alreadyHave) {
                PreparedStatement updateStatement = c.prepareStatement("update `knockback_timer` set time=? where uuid=?;");
                updateStatement.setInt(1, ticks);
                updateStatement.setString(2, player.getUniqueId().toString());
                updateStatement.executeUpdate();
            } else {
                PreparedStatement insertStatement = c.prepareStatement("insert into `knockback_timer` (uuid,time) values (?,?);");
                insertStatement.setString(1, player.getUniqueId().toString());
                insertStatement.setInt(2, ticks);
                insertStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getTimer(OfflinePlayer player) {
        if (cachedResultMap.containsKey(player) && cachedResultMap.get(player).getTimer() >= 0) {
            return cachedResultMap.get(player).getTimer();
        }
        if (!haveTimer(player)) {
            setTimer(player, 60);
        }
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `knockback_timer` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            rs.first();
            int timer = rs.getInt("time");
            updateCache(player, (result) -> result.setTimer(timer));
            return timer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean haveKnockback(OfflinePlayer player, GameType gameType) {
        String tableName = "custom_knockback";
        if (gameType == GameType.NPC_KNOCKBACK) {
            tableName += "_npc";
        }
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `" + tableName + "` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            return rs.first();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setKnockback(OfflinePlayer player, Vector2d knockbackVector, GameType gameType) {
        boolean alreadyHave = haveKnockback(player, gameType);
        cachedResultMap.remove(player);
        String tableName = "custom_knockback";
        if (gameType == GameType.NPC_KNOCKBACK) {
            tableName += "_npc";
        }
        try (Connection c = getConnection()) {
            double x = knockbackVector.getX();
            double y = knockbackVector.getY();
            if (alreadyHave) {
                PreparedStatement updateStatement = c.prepareStatement("update `" + tableName + "` set vertical=? , horizontal=? where uuid=?;");
                updateStatement.setDouble(1, y);
                updateStatement.setDouble(2, x);
                updateStatement.setString(3, player.getUniqueId().toString());
                updateStatement.executeUpdate();
            } else {
                PreparedStatement insertStatement = c.prepareStatement("insert into `" + tableName + "` (uuid,vertical,horizontal) values (?,?,?);");
                insertStatement.setString(1, player.getUniqueId().toString());
                insertStatement.setDouble(2, y);
                insertStatement.setDouble(3, x);
                insertStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Vector2d getKnockback(OfflinePlayer player, GameType gameType) {
        if (cachedResultMap.containsKey(player)) {
            if (gameType == GameType.NPC_KNOCKBACK && cachedResultMap.get(player).getNpcKockback() != null) {
                return cachedResultMap.get(player).getNpcKockback();
            } else if (cachedResultMap.get(player).getKnockback() != null) {
                return cachedResultMap.get(player).getKnockback();
            }
        }
        if (!haveKnockback(player, gameType)) {
            setKnockback(player, new Vector2d(0.8997621613275862, 0.46080000519752506), gameType);
        }
        String tableName = "custom_knockback";
        if (gameType == GameType.NPC_KNOCKBACK) {
            tableName += "_npc";
        }
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from " + tableName + " where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            rs.first();
            Vector2d knockbackVector = new Vector2d(rs.getDouble("horizontal"), rs.getDouble("vertical"));
            if (gameType == GameType.NPC_KNOCKBACK) {
                updateCache(player, (result) -> result.setNpcKockback(knockbackVector));
            } else {
                updateCache(player, (result) -> result.setKnockback(knockbackVector));
            }
            return knockbackVector;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getBridgeRecord(OfflinePlayer player) {
        if (cachedResultMap.containsKey(player) && cachedResultMap.get(player).getBridgeRecord() >= 0) {
            return cachedResultMap.get(player).getBridgeRecord();
        }
        if (!haveBridgeRecord(player)) {
            return Integer.MAX_VALUE;
        }
        try (Connection c = getConnection()) {
            PreparedStatement fetchStatement = c.prepareStatement("select * from `bridge_record` where uuid=?;");
            fetchStatement.setString(1, player.getUniqueId().toString());
            ResultSet rs = fetchStatement.executeQuery();
            rs.first();
            int timer = rs.getInt("time");
            updateCache(player, (result) -> result.setTimer(timer));
            return timer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putBridgeRecord(OfflinePlayer player, int time) {
        if (getBridgeRecord(player) <= time) {
            return;
        }
        cachedResultMap.remove(player);
        boolean have = haveBridgeRecord(player);
        try (Connection c = getConnection()) {
            if (have) {
                PreparedStatement updateStatement = c.prepareStatement("update `bridge_record` set time=? where uuid=?;");
                updateStatement.setInt(1, time);
                updateStatement.setString(2, player.getUniqueId().toString());
                updateStatement.executeUpdate();
            } else {
                PreparedStatement insertStatement = c.prepareStatement("insert into `bridge_record` (uuid,time) values (?,?);");
                insertStatement.setString(1, player.getUniqueId().toString());
                insertStatement.setInt(2, time);
                insertStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean haveClutchRecord(OfflinePlayer player) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `knockback_record` where uuid=?");
            ps.setString(1, player.getUniqueId().toString());
            return ps.executeQuery().first();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initClutchRecord(OfflinePlayer player) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("insert into `knockback_record` (uuid,normal_distance,npc_distance) values (?,0,0);");
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double getClutchDefaultRecord(OfflinePlayer player) {
        if (cachedResultMap.containsKey(player) && !Double.isNaN(cachedResultMap.get(player).knockbackRecord)) {
            return cachedResultMap.get(player).getKnockbackRecord();
        }
        if (!haveClutchRecord(player)) {
            initClutchRecord(player);
        }

        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `knockback_record` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            rs.first();
            double normalRecord = rs.getDouble("normal_distance");
            updateCache(player, (result) -> result.setKnockbackRecord(normalRecord));
            return normalRecord;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double getClutchNpcRecord(OfflinePlayer player) {
        if (cachedResultMap.containsKey(player) && !Double.isNaN(cachedResultMap.get(player).getKnockbackNpcRecord())) {
            return cachedResultMap.get(player).getKnockbackNpcRecord();
        }
        if (!haveClutchRecord(player)) {
            initClutchRecord(player);
        }

        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `knockback_record` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            rs.first();
            double npcRecord = rs.getDouble("npc_distance");
            updateCache(player, (result) -> result.setKnockbackNpcRecord(npcRecord));
            return npcRecord;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setClutchDefaultRecord(OfflinePlayer player, double record) {
        if (!haveClutchRecord(player)) {
            initClutchRecord(player);
        }
        if (record < getClutchDefaultRecord(player)) {
            return;
        }
        cachedResultMap.remove(player);
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("update `knockback_record` set normal_distance=? where uuid=?;");
            ps.setDouble(1, record);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setClutchNpcRecord(OfflinePlayer player, double record) {
        if (!haveClutchRecord(player)) {
            initClutchRecord(player);
        }
        if (record < getClutchNpcRecord(player)) {
            return;
        }
        cachedResultMap.remove(player);
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("update `knockback_record` set npc_distance=? where uuid=?;");
            ps.setDouble(1, record);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean haveGadget(OfflinePlayer player) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `gadgets` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            return ps.executeQuery().first();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initPlayerGadget(OfflinePlayer player) {
        if (haveGadget(player)) {
            return;
        }
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("insert into `gadgets` (uuid,block_gadget,stick_gadget) values (?,\"default\",\"default\")");
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayerBlock(OfflinePlayer player, String blockId) {
        if (!haveGadget(player)) {
            initPlayerGadget(player);
        }
        cachedResultMap.remove(player);
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("update `gadgets` set block_gadget=? where uuid=?;");
            ps.setString(1, blockId);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayerStick(OfflinePlayer player, String stickId) {
        if (!haveGadget(player)) {
            initPlayerGadget(player);
        }
        cachedResultMap.remove(player);
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("update `gadgets` set stick_gadget=? where uuid=?;");
            ps.setString(1, stickId);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getPlayerStick(OfflinePlayer player) {
        if (cachedResultMap.containsKey(player) && cachedResultMap.get(player).getStick() != null) {
            return cachedResultMap.get(player).getStick();
        }
        if (!haveGadget(player)) {
            initPlayerGadget(player);
        }
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `gadgets` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            rs.first();
            String stickGadget = rs.getString("stick_gadget");
            updateCache(player, (cache) -> cache.setStick(stickGadget));
            return stickGadget;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getPlayerBlock(OfflinePlayer player) {
        if (cachedResultMap.containsKey(player) && cachedResultMap.get(player).getBlock() != null) {
            return cachedResultMap.get(player).getBlock();
        }
        if (!haveGadget(player)) {
            initPlayerGadget(player);
        }
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `gadgets` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            rs.first();
            String blockGadget = rs.getString("block_gadget");
            updateCache(player, (cache) -> cache.setBlock(blockGadget));
            return blockGadget;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean haveTimes(OfflinePlayer player) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from `knockback_times` where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            return ps.executeQuery().first();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setTimes(OfflinePlayer player, int times) {
        boolean have = haveTimes(player);
        cachedResultMap.remove(player);
        try (Connection c = getConnection()) {
            if (have) {
                PreparedStatement update = c.prepareStatement("update `knockback_times` set times=? where uuid=?;");
                update.setInt(1, times);
                update.setString(2, player.getUniqueId().toString());
                update.executeUpdate();
            } else {
                PreparedStatement insert = c.prepareStatement("insert into `knockback_times` (uuid,times) values (?,?);");
                insert.setString(1, player.getUniqueId().toString());
                insert.setInt(2, times);
                insert.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getTimes(OfflinePlayer player) {
        if (cachedResultMap.containsKey(player) && cachedResultMap.get(player).getTimes() > 0) {
            return cachedResultMap.get(player).getTimes();
        }
        if (!haveTimes(player)) {
            setTimes(player, 1);
        }
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("select * from knockback_times where uuid=?;");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            rs.first();
            int times = rs.getInt("times");
            updateCache(player, (result) -> result.setTimes(times));
            return times;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCache(OfflinePlayer op, Consumer<CachedPlayerSqlResult> resultConsumer) {
        CachedPlayerSqlResult result = cachedResultMap.get(op);
        if (result == null) {
            result = new CachedPlayerSqlResult();
        }
        resultConsumer.accept(result);
        cachedResultMap.put(op, result);
    }

    private class CachedPlayerSqlResult {
        private KnockbackDifficulty difficulty;
        private KnockbackDifficulty npcDifficulty;
        private Vector2d knockback;
        private Vector2d npcKockback;
        private int timer = -1;
        private int bridgeRecord = -1;
        private int times = -1;
        private double knockbackRecord = Double.NaN;
        private double knockbackNpcRecord = Double.NaN;
        private String stick;
        private String block;

        public KnockbackDifficulty getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(KnockbackDifficulty difficulty) {
            this.difficulty = difficulty;
        }

        public Vector2d getKnockback() {
            return knockback;
        }

        public void setKnockback(Vector2d knockback) {
            this.knockback = knockback;
        }

        public int getTimer() {
            return timer;
        }

        public void setTimer(int timer) {
            this.timer = timer;
        }

        public int getBridgeRecord() {
            return bridgeRecord;
        }

        public void setBridgeRecord(int bridgeRecord) {
            this.bridgeRecord = bridgeRecord;
        }

        public double getKnockbackRecord() {
            return knockbackRecord;
        }

        public void setKnockbackRecord(double knockbackRecord) {
            this.knockbackRecord = knockbackRecord;
        }

        public double getKnockbackNpcRecord() {
            return knockbackNpcRecord;
        }

        public void setKnockbackNpcRecord(double knockbackNpcRecord) {
            this.knockbackNpcRecord = knockbackNpcRecord;
        }

        public String getStick() {
            return stick;
        }

        public void setStick(String stick) {
            this.stick = stick;
        }

        public String getBlock() {
            return block;
        }

        public void setBlock(String block) {
            this.block = block;
        }

        public int getTimes() {
            return times;
        }

        public KnockbackDifficulty getNpcDifficulty() {
            return npcDifficulty;
        }

        public void setNpcDifficulty(KnockbackDifficulty npcDifficulty) {
            this.npcDifficulty = npcDifficulty;
        }

        public Vector2d getNpcKockback() {
            return npcKockback;
        }

        public void setNpcKockback(Vector2d npcKockback) {
            this.npcKockback = npcKockback;
        }

        public void setTimes(int times) {
            this.times = times;
        }
    }
}
