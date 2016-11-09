package tbs.doblon.io;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BitmapLoader {

    public static Texture storeButton;
    TextureAtlas atlas;
    TextureRegion[] islandSprites;

    public static void init() {
        dispose();
//Todo        storeButton = Utility.getTexture("storebutton.png");
    }

    public static void dispose() {

        Utility.dispose(storeButton);

    }


    public BitmapLoader() {
        init();
    }
}
