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







    window.onkeyup = function (event) {
        var keyNum = event.keyCode ? event.keyCode : event.which;
        if (socket && player && !player.dead) {

            // UPGRADES:
            if (upgrInputsToIndex["k" + keyNum] != undefined) {
                doUpgrade(upgrInputsToIndex["k" + keyNum], 0, 1);
            }

            // STOP SHOOTING:
            if (keyNum == 32) {
                shooting = false;
                socket.emit('2');
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
                socket.emit('as');
            }

            // GIVE COIN:
            if (keyNum == 70) {
                socket.emit('5');
            }
        }
    };
    window.onkeydown = function (event) {
        var keyNum = event.keyCode ? event.keyCode : event.which;
        if (socket && player && !player.dead) {

            // START SHOOTING:
            if (!shooting && keyNum == 32) {
                shooting = true;
                socket.emit('2', 1);
            }

            // MOVEMENT:
            if ((keyNum == 65 || keyNum == 37) && !keys.l) {
                keys.l = 1;
                keys.r = 0;
                turnDir = -1;
                sendMoveTarget();
            }
            if ((keyNum == 68 || keyNum == 39) && !keys.r) {
                keys.r = -1;
                keys.l = 0;
                turnDir = 1;
                sendMoveTarget();
            }
            if ((keyNum == 87 || keyNum == 38) && !keys.u) {
                keys.u = 1;
                keys.d = 0;
                speedInc = 1;
                sendMoveTarget();
            }
            if ((keyNum == 83 || keyNum == 40) && !keys.d) {
                keys.d = 1;
                keys.u = 0;
                speedInc = -1;
                sendMoveTarget();
            }
        }
    };

    mainCanvas.addEventListener('touchmove', touchMove, false);
    mainCanvas.addEventListener('touchstart', touchMove, false);
    function touchMove(event) {
        var touch;
        for (var i = 0; i < event.touches.length; i++) {
            touch = event.touches[i];
            mouseX = (touch.screenX * screenRatio / physicalW) * screenWidth;
            mouseY = (touch.screenY * screenRatio / physicalH) * screenHeight;
            sendTarget(false || forceTarget);
            forceTarget = false;
        }
        try {
            event.preventDefault();
            event.stopPropagation();
        } catch (e) {

        }
    }

    fireButton.addEventListener('touchstart', function () {
        socket.emit('2', 1);
    });

    fireButton.addEventListener('touchend', function () {
        socket.emit('2');
    });

    upgrades.addEventListener('touchstart', function () {
        toggleUpgrades();
    });

    enterGameButton.onclick = function () {
        enterGame();
    }


    userNameInput.addEventListener('keypress', function (e) {
        var key = e.which || e.keyCode;
        if (key === 13) {
            if (true || validNick()) {
                enterGame();
            }
        }
    });

//    <div id="skinSelector" onclick="changeSkin(1)"><i class="material-icons">&#xE8D5;</i> SKIN</div>
    mainCanvas.addEventListener('mousemove', gameInput, false);
    mainCanvas.addEventListener('mousedown', mouseDown, false);
    mainCanvas.addEventListener('mouseup', mouseUp, false);

    function gameInput(e) {
        e.preventDefault();
        e.stopPropagation();
        mouseX = e.clientX;
        mouseY = e.clientY;
        sendTarget(false || forceTarget);
        forceTarget = false;
    }
    function mouseDown(e) {
        e.preventDefault();
        e.stopPropagation();
        socket.emit('2', 1);
    }
    function mouseUp(e) {
        e.preventDefault();
        e.stopPropagation();
        if (socket && player && !player.dead) {
            socket.emit('2');
        }
    }
}
