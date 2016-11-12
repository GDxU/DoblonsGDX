package tbs.doblon.io;

import static tbs.doblon.io.Game.toggleUpgrades;
import static tbs.doblon.io.SocketManager.socket;

/**
 * Created by linde on 11/13/2016.
 */

public class UIManager {
    public static final Button fireButton = new Button(), upgrades = new Button();
    public static final Button enterGame = new Button();

    public static boolean touchDown(float x, float y) {
        if (fireButton.click(x, y)) {
            socket.emit("2", 1);
            return true;
        }else if (upgrades.click(x, y)) {
            toggleUpgrades();
            return true;
        } else if (enterGame.click(x, y)) {
            Game.enterGame();
            return true;
        }

        return false;
    }

    public static boolean touchUp(float x, float y) {
        if (fireButton.click(x, y)) {
            socket.emit("2");
            return true;
        }

        return false;
    }

    public static boolean click(float x, float y) {


        return false;
    }
}
