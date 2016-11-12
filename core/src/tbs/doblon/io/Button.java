package tbs.doblon.io;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by linde on 11/13/2016.
 */

public class Button {
    private static final Rectangle r1 = new Rectangle(), r2 = new Rectangle();
    float x,y,h,w;

    public boolean click(float x, float y){
        r1.set(x,y,w,h);
        return r1.contains(x,y);
    }
}
