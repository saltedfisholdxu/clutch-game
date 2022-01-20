package moe.orangemc.clutchgames.task;

import moe.orangemc.clutchgames.ClutchGames;

public class ScoreboardManagerUpdater implements Runnable {
    @Override
    public void run() {
        ClutchGames.getScoreboardManager().updateGameScoreboard();
    }
}
