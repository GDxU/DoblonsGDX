package tbs.doblon.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class GameController extends InputMultiplexer {
    private static InputMultiplexer multiplexer = new InputMultiplexer();
    private static GestureDetector gestureDetector;
    private static final InputProcessor inputProcessor = new InputProcessor() {

        @Override
        public boolean keyDown(int keycode) {
            keyPress(keycode);
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            GameController.keyRelease(keycode);
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {

            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            click(screenX, screenY);
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            scroll(amount);
            return false;
        }
    };

    private static boolean click(int x, int y) {

        return false;
    }

    private static boolean scroll(float dy) {
        return false;
    }

    private static boolean fling(float vx, float vy) {
        return false;
    }

    private static boolean zoom(float scale) {
        return false;
    }

    private static boolean longClick(float scale) {
        return false;
    }

    private static void keyPress(int keyCode) {
        switch (keyCode) {
            case Input.Keys.SPACE:

                break;
        }
    }

    private static void keyRelease(int keyCode) {

    }

    public static void init() {
        //Todo call this in every resume
        multiplexer.clear();
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(gestureListener);
        }
        multiplexer.addProcessor(gestureDetector);
        multiplexer.addProcessor(inputProcessor);
        Gdx.input.setInputProcessor(multiplexer);

    }

    private static final GestureDetector.GestureListener gestureListener = new GestureDetector.GestureListener() {
        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            return GameController.click((int) x, (int) y);
        }

        @Override
        public boolean longPress(float x, float y) {
            return longPress(x, y);
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            return GameController.fling(velocityX, velocityY);
        }

        @Override
        public void pinchStop() {

        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            return GameController.zoom(initialDistance / distance);
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            //Todo calculate zoom
            return false;
        }
    };

}
