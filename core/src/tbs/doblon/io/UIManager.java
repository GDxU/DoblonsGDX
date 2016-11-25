package tbs.doblon.io;

import java.util.ArrayList;

import static tbs.doblon.io.Game.toggleUpgrades;
import static tbs.doblon.io.SocketManager.socket;

/**
 * Created by linde on 11/13/2016.
 */

public class UIManager {
    public static final Button fireButton = new Button(), upgrades = new Button();
    public static final Button enterGame = new Button();
public static ArrayList<Button> upgradeButtons = new ArrayList<Button>();
public static ArrayList<Button> weaponButtons = new ArrayList<Button>();
    public static boolean touchDown(float x, float y) {
        if (fireButton.click(x, y)) {
            socket.emit("2", 1);
            return true;
        }

        if (upgrades.click(x, y)) {
            toggleUpgrades();
            return true;
        }

        if (enterGame.click(x, y)) {
            Game.enterGame();
            return true;
        }

        for (final Button upgradeButton : upgradeButtons) {
            if (upgradeButton.click(x,y)){
                final String text = upgradeButton.text.toLowerCase();
                if (text.contains("hull strength")){
                }else if (text.contains("auto repair")){
                }else if (text.contains("cannon range")){
                }else if (text.contains("cannon damage")){
                }else if (text.contains("reload speed")){
                }else if (text.contains("move speed")){
                }else if (text.contains("turn speed")){
                }else if (text.contains("ram damage")){
                }else if (text.contains("fishing")){
                }else if (text.contains("battles")){
                }else if (text.contains("man of war")){

                }
                return true;
            }
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
