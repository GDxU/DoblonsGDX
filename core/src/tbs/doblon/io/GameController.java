package tbs.doblon.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import static tbs.doblon.io.Game.forceTarget;
import static tbs.doblon.io.Game.keys;
import static tbs.doblon.io.Game.player;
import static tbs.doblon.io.Game.sendMoveTarget;
import static tbs.doblon.io.Game.shooting;
import static tbs.doblon.io.Game.speedInc;
import static tbs.doblon.io.Game.turnDir;
import static tbs.doblon.io.SocketManager.socket;

public class GameController extends InputMultiplexer {
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
    private static InputMultiplexer multiplexer = new InputMultiplexer();
    private static GestureDetector gestureDetector;
    private static KeyListener keyListener;
    private static final InputProcessor inputProcessor = new InputProcessor() {
        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.BACKSPACE) {
                if (keyListener != null) {
                    keyListener.onBackSpace();
                }
            } else
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
            if (keyListener != null) {
                keyListener.onKeyPresses(character);
            }
            return false;
        }

        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {
            keyListener = null;
            if (UIManager.touchDown(x, y)) {
                return false;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (UIManager.touchUp(screenX, screenY)) {
                return false;
            }
            click(screenX, screenY);
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            Game.mouseX = screenX;
            Game.mouseY = screenY;
            Game.sendTarget(false || forceTarget);
            forceTarget = false;
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            Game.mouseX = screenX;
            Game.mouseY = screenY;
            Game.sendTarget(false || forceTarget);
            forceTarget = false;
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

    private static void keyPress(int keyNum) {
        if (SocketManager.isConnected() && player != null && !player.dead) {

            // START SHOOTING:
            if (!shooting && keyNum == 32) {
                shooting = true;
                socket.emit("2", 1);
            }

            // MOVEMENT:
            if ((keyNum == 65 || keyNum == 37) && keys.l == 0) {
                keys.l = 1;
                keys.r = 0;
                turnDir = -1;
                sendMoveTarget();
            }
            if ((keyNum == 68 || keyNum == 39) && keys.r == 0) {
                keys.r = -1;
                keys.l = 0;
                turnDir = 1;
                sendMoveTarget();
            }
            if ((keyNum == 87 || keyNum == 38) && keys.u == 0) {
                keys.u = 1;
                keys.d = 0;
                speedInc = 1;
                sendMoveTarget();
            }
            if ((keyNum == 83 || keyNum == 40) && keys.d == 0) {
                keys.d = 1;
                keys.u = 0;
                speedInc = -1;
                sendMoveTarget();
            }
        }
    }

    public static void setKeyListener(KeyListener keyListener) {
        GameController.keyListener = keyListener;
    }

    private static void keyRelease(int keyNum) {
        if (SocketManager.isConnected() && player != null && !player.dead) {

            // UPGRADES:
            int num = -1;
            switch (keyNum) {
                case 48:
                    num = 9;
                    break;
                case 49:
                    num = 0;
                    break;
                case 50:
                    num = 1;
                    break;
                case 51:
                    num = 2;
                    break;
                case 52:
                    num = 3;
                    break;
                case 53:
                    num = 4;
                    break;
                case 54:
                    num = 5;
                    break;
                case 55:
                    num = 6;
                    break;
                case 56:
                    num = 7;
                    break;
                case 57:
                    num = 8;
                    break;
                case 84:
                    num = 10;
                    break;
                case 89:
                    num = 11;
                    break;

            }

            Game.doUpgrade(num, 0, 1);


            // STOP SHOOTING:
            if (keyNum == 32) {
                shooting = false;
                socket.emit("2");
            }

            // MOVEMENT:
            if ((keyNum == 65 || keyNum == 37)) {
                keys.l = 0;
                sendMoveTarget();
            }
            if ((keyNum == 68 || keyNum == 39)) {
                keys.r = 0;
                sendMoveTarget();
            }
            if ((keyNum == 87 || keyNum == 38)) {
                keys.u = 0;
                sendMoveTarget();
            }
            if ((keyNum == 83 || keyNum == 40)) {
                keys.d = 0;
                sendMoveTarget();
            }

            // AUTO SHOOT:
            if (keyNum == 69) {
                socket.emit("as");
            }

            // GIVE COIN:
            if (keyNum == 70) {
                socket.emit("5");
            }
        }
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

    public interface KeyListener {
        void onKeyPresses(char keycode);

        void onBackSpace();
    }

}
