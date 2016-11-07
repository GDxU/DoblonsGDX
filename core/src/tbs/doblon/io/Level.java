package tbs.doblon.io;

import java.util.ArrayList;

/**
 * Created by mike on 3/4/16.
 */
public class Level {
    public static ArrayList<Player> players = new ArrayList<Player>();

    public static void update(){
        for (Player player : players) {
            player.update(Game.delta);
        }
    }
}
