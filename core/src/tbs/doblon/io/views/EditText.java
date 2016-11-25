package tbs.doblon.io.views;

import tbs.doblon.io.GameController;

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
            text.concat(s);
        }

        @Override
        public void onBackSpace() {
            if (text != null && text.length() > 0) {
                text = text.substring(0, text.length() - 1);
            }
        }
    };
    float xOffset;

    public EditText() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, int x, int y) {
                GameController.setKeyListener(listener);
            }


        });
    }

    @Override
    public void dispose() {

    }

    @Override
    public void draw(float relX, float relY, float parentRight, float parentTop) {

    }
}
