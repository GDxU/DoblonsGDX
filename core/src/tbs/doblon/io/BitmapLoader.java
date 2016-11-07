package tbs.doblon.io;

import com.badlogic.gdx.graphics.Texture;

public class BitmapLoader {

    public static Texture storeButton;

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
