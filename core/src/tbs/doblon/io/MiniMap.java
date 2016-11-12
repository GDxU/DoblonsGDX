package tbs.doblon.io;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

import static tbs.doblon.io.Game.gameData;
import static tbs.doblon.io.Game.minimap;

/**
 * Created by linde on 11/12/2016.
 */

public class MiniMap {
    ArrayList<MiniMapItem> miniMapItems = new ArrayList<MiniMapItem>();
    float x,y,width, height,r,s;

    public void draw(ShapeRenderer renderer){
        Game.roundRect(x,y,width,height, r,s);
        for (MiniMapItem item : miniMapItems) {
            renderer.setColor(Utility.tmpColor.set(item.color));
            renderer.circle(item.x, item.y, item.w/2);
        }
    }
}
