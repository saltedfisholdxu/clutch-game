package moe.orangemc.clutchgames.task;

import moe.orangemc.clutchgames.ClutchGames;

public class GameManagerUpdater implements Runnable {
    @Override
    public void run() {
        ClutchGames.getGameManager().tick();
    }
}
