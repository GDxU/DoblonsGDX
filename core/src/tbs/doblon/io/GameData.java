package tbs.doblon.io;

import java.util.ArrayList;

/**
 * Created by linde on 11/12/2016.
 */

public class GameData {


    public static int mapScale, objCount, outerColor, waterColor;
    public static final ArrayList<Island> islands = new ArrayList<Island>();

    public GameData(String data) {
        super();
        parse(data);
    }

    public void parse(String data) {
        //Todo
    }
}
