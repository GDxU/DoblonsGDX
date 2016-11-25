package tbs.doblon.io.views;

import com.badlogic.gdx.Gdx;

import tbs.doblon.io.Game;
import tbs.doblon.io.GameController;
import tbs.doblon.io.Utility;

/**
 * Created by linde on 11/25/2016.
 */

public class EditText extends View {
    String text;
    final GameController.KeyListener listener = new GameController.KeyListener() {
        @Override
        public void onKeyPresses(char c) {
            final String s = String.valueOf(c);
            if (s.length() != 1)
                return;
            if (text == null)
                text = "";
            text = text.concat(s);
            Utility.print(text);
        }

        @Override
        public void onBackSpace() {
            if (text != null && text.length() > 0) {
                text = text.substring(0, text.length() - 1).trim();
            }
        }
    };
    float xOffset;

    public EditText() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, int x, int y) {
                Gdx.input.setOnscreenKeyboardVisible(true);
                GameController.setKeyListener(listener);
                Utility.print("click > " + x + ", " + y);
            }
        });
    }

    @Override
    public void dispose() {

    }

    @Override
    public void draw(float relX, float relY, float parentRight, float parentTop) {
        Utility.drawCenteredText(Game.spriteBatch(), 0, text, Game.screenWidth / 2, Game.screenHeight / 2, Utility.getScale(10));
    }
}
