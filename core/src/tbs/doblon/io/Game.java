package tbs.doblon.io;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game extends GameBase {
    @Override
    public void create() {
        super.create();
        SocketManager.init();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    protected void onDraw() {

    }

    String socket;
    int port;
    int targetFPS = 60;
    public static float delta, delta2;
    public static long currentTime, oldTime ;
    int gameState = 0;
    Object gameData;
    boolean upgradesHidden = false;
    int instructionsSpeed = 5500;
    int insturctionsCountdown = 0;
    String[] instructionsList = {
            "use A & D or Arrow Keys control the direction of your ship",
            "use your mouse to aim and fire your cannons",
            "destroy enemy ships and collect doblons to upgrade your ship"
    };

    int instructionsIndex = Utility.randInt(0, instructionsList.length - 1);
    String[] randomLoadingTexts = {
            "discovering treasure...",
            "setting sail..."
    };



    int[] keys = {0,0, 0, 0};//l,r,u,d
    public void resetKeys() {
        keys[0] = 0;
        keys[1] = 0;
    }

    public static String cid;
    public void getStorage(){
        cid = Utility.getString("sckt");
        if (cid.length()<2) {
//            cid = Utility.getUniqueID();
            Utility.saveString("sckt", cid);
        }
    }

    int mouseX, mouseY;
    boolean forceTarget = true, shooting;
   String userNameInput = Utility.getString("lstnmdbl");

    public static void toggleUpgrades(){

    }

    // GAME INPUT:
    public static void enterGame() {
        if (SocketManager.isConnected) {
            showMainMenuText(randomLoadingTexts[UTILS.randInt(0, randomLoadingTexts.length - 1)]);
            socket.emit('respawn', {
                    name: userNameInput.value,
                    skin: skinIndex
            });
            Utility.saveString("lstnmdbl", userNameInput.value);
            mainCanvas.focus();
        }
    }

    // LEAVE GAME TO MENU:
    public static void leaveGame() {
        gameState = 0;
        toggleGameUI(false);
        toggleMenuUI(true);
    }

    // DO UPGRADE:
    public static void doUpgrade(index, pos, tp) {
        socket.emit('3', index, pos, tp);
    }

    public void roundRect(float x, float y, float w, float h, float r, float s) {
        s = s || 1;

        if (w < 2 * r) r = w / 2;
        if (h < 2 * r) r = h / 2;
        this.beginPath();
        this.moveTo(x + r, y);
        this.arcTo(x + w * s, y, x + w * s, y + h, r);
        this.arcTo(x + w, y + h, x, y + h, r);
        this.arcTo(x, y + h, x, y, r);
        this.arcTo(x * (s == 1 ? s : (s * 1.2)), y, x + w * s, y, r);
        this.closePath();
        return this;
    }
}
