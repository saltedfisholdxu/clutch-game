package moe.orangemc.clutchgames.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import moe.orangemc.clutchgames.ClutchGames;
import moe.orangemc.clutchgames.database.MySQLDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.OfflinePlayer;

public class PAPIHelper extends PlaceholderExpansion {
    private final MySQLDataSource mySQLDataSource = ClutchGames.getMySQLDataSource();

    @Override
    public @NotNull String getIdentifier() {
        return "clutch";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Lucky_fish0w0";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0-SNAPSHOT";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params) {
            case "normal_record":
                return "" + mySQLDataSource.getClutchDefaultRecord(player);
            case "npc_record":
                return "" + mySQLDataSource.getClutchNpcRecord(player);
            case "bridge_record":
                return "" + mySQLDataSource.getBridgeRecord(player);
        }
        return null;
    }
}
