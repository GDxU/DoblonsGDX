package tbs.doblon.io;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.StringBuilder;

public class Game extends GameBase {
    @Override
    public void create() {
        super.create();
        SocketManager.init();
    }

    @Override
    public void resume() {
        super.resume();
        resumeSounds();
        paused = false;
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

    @Override
    public void pause() {
        pauseSounds();
        paused = true;
        super.pause();
    }

    String socket;
    int port;
    int targetFPS = 60;
    public static float delta, delta2;
    public static long currentTime, oldTime;
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

    long lastAdShown = System.currentTimeMillis();
    public static void showAd() {
        Utility.log("showAds");
        if (!paused && (1200 < (System.currentTimeMillis() - lastAdShown))) {
            lastAdShown = System.currentTimeMillis();
            ShowAds.showInterstitialAd();
        }
    }

    public static Player getPlayerIndexById(String id) {

                for (int i =0;i<Level.players.size();i++){
                    final Player p = Level.players.get(i);
                    if (p.id.equals(id)){
                        return p;
                    }
                }

        return null;
    }

    // CHECK IF PLAYER IS IN ARRAY:
    public static boolean objectExists(Player obj) {

        for (int i =0;i<Level.players.size();i++){
            final Player p = Level.players.get(i);
            if (p.sid == obj.sid){
                return true;
            }
        }

        return false;
    }

    // FIND PLAYER INDEX:
    public static int getPlayerIndex(int sid) {

        for (int i =0;i<Level.players.size();i++){
            final Player p = Level.players.get(i);
            if (p.sid == sid){
                return i;
            }
        }

        return -1;
    }

    // SCREEN SHAKE:
    float screenSkX ,screenShackeScale,screenSkY ,screenSkRed , screenSkDir = 0;
    public static void screenShake(scl, dir) {
        if (screenShackeScale < scl) {
            screenShackeScale = scl;
            screenSkDir = dir;
        }
    }
    public static void updateScreenShake(delta) {
        if (screenShackeScale > 0) {
            screenSkX = screenShackeScale * MathCOS(screenSkDir);
            screenSkY = screenShackeScale * MathSIN(screenSkDir);
            screenShackeScale *= screenSkRed;
            if (screenShackeScale <= 0.1)
                screenShackeScale = 0;
        }
    }

    float playerCanvasScale = 430, maxFlashAlpha = 0.25f;

    // SKULL ICONS:
    var iconsCount = 5;
    var skullIconSize = 50;
    var iconsList = [];
    for (var i = 1; i < iconsCount; ++i) {
        var tmpImg = new Image();
        tmpImg.onload = (public static void (val) {
        return public static void () {
            this.onLoad = null;
            iconsList[val] = this;
        }
        })(i);
        tmpImg.src = ".././img/icons/skull_" + i + ".png";
    }

    // ANIM TEXT:
    var animTexts = [];
    var animTextIndex = 0;
    var scoreCountdown = 0;
    var lastScore = 0;
    var scoreDisplayTime = 1500;
    for (var i = 0; i < 20; ++i) {
        animTexts.push(new animText());
    }
    public static void updateAnimTexts(delta) {
        // UPDATE COOLDOWNS:
        if (scoreCountdown) {
            scoreCountdown -= delta;
            if (scoreCountdown <= 0) {
                scoreCountdown = 0;
                lastScore = 0;
            }
        }

        // RENDER:
        mainContext.textAlign = "center";
        mainContext.strokeStyle = '#5f5f5f';
        mainContext.fillStyle = '#ffffff';
        mainContext.lineWidth = 7;
        for (var i = 0; i < animTexts.length; ++i) {
            animTexts[i].update(delta);
        }
        mainContext.globalAlpha = 1;
    }

    public static void showAnimText(x, y, txt, scale, fadeDelay, type, sclPlus) {
        var tmpText = animTexts[animTextIndex];
        tmpText.show(x, y, txt, scale, fadeDelay, sclPlus);
        tmpText.type = type;
        animTextIndex++;
        if (animTextIndex >= animTexts.length)
            animTextIndex = 0;
    };

    // NOTIFICATIONS:
    public static void hideNotifByType(type) {
        for (var i = 0; i < animTexts.length; ++i) {
            if (animTexts[i].type == type)
                animTexts[i].active = false;
        }
    }
    public static void showNotification(text) {
        for (var i = 0; i < animTexts.length; ++i) {
            if (animTexts[i].type == "notif")
                animTexts[i].active = false;
        }
        showAnimText(maxScreenWidth / 2, maxScreenHeight / 1.27, text, 42, 1500, "notif", 0.19);
    }
    public static void showBigNotification(text) {
        hideNotifByType("bNotif");
        showAnimText(maxScreenWidth / 2, screenHeight / 3, text, 130, 1000, "bNotif", 0.26);
    }
    public static void showScoreNotif(value) {
        hideNotifByType("sNotif");
        lastScore += value;
        showAnimText(maxScreenWidth / 2, maxScreenHeight / 1.34, ("+" + lastScore), 35, scoreDisplayTime, "sNotif", 0.16);
        scoreCountdown = scoreDisplayTime;
    }

    // SHOW A TEXT IN THE MENU:
    public static void showMainMenuText(text) {
        userInfoContainer.style.display = "none";
        loadingContainer.style.display = "block";
        loadingContainer.innerHTML = text;
    }
    public static void hideMainMenuText() {
        userInfoContainer.style.display = "block";
        loadingContainer.style.display = "none";
    }

    // TOGGLE UI:
    public static void toggleGameUI(visible) {
        var display = visible ? "block" : "none";
        gameUiContainer.style.display = display;
    }
    public static void toggleMenuUI(visible) {

        // SHOWING MAIN MENU:
        if (visible) {
            menuContainer.style.display = "flex";
            darkener.style.display = "block";
            // linksContainer.style.display = "block";
            target[2] = 0;
        }

        // HIDING MENU:
        else {
            menuContainer.style.display = "none";
            darkener.style.display = "none";
            // linksContainer.style.display = "none";
        }
    }

    public static void initAds() {
        ShowAds.initInterstitialAd({adId: "ca-app-pub-6350309116730071/9878078741", isTest: true});
    }

    public static final String treasureMap = "2B=TK:KAB,SSV:100K";

    int[] keys = {0, 0, 0, 0};//l,r,u,d

    public void resetKeys() {
        keys[0] = 0;
        keys[1] = 0;
    }

    public static String cid;

    public void getStorage() {
        cid = Utility.getString("sckt");
        if (cid.length() < 2) {
//            cid = Utility.getUniqueID();
            Utility.saveString("sckt", cid);
        }
    }

    int mouseX, mouseY;
    boolean forceTarget = true, shooting;
    String userNameInput = Utility.getString("lstnmdbl");

    public static void toggleUpgrades() {

    }

    // GAME INPUT:
    public static void enterGame() {
        if (SocketManager.isConnected()) {
            showMainMenuText(randomLoadingTexts[UTILS.randInt(0, randomLoadingTexts.length - 1)]);
            socket.emit('respawn', {
                    name:userNameInput.value,
                    skin:skinIndex
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


    //[0] = name, [1] = color1, [2] = color2;
    String[][] userSkins = {
            "Default",
            "#eb6d6d",
            "#949494"
    }, {
        "Purple",
        "#b96bed",
        "#949494"
        }, {
        "Green",
        "#6FED6B",
        "#949494"
        }, {
        "Orange",
        "#EDB76B",
        "#949494"
        }, {
        "Black",
        "#696969",
        "#949494"
        }, {
        "Navy",
        "#adadad",
        "#949494"
        }, {
        "Ghostly",
        "#9BEB6D",
        "#949494",
        opacity: 0.7,
        overlay: "rgba(0,255,0,0.3)"
        }, {
        "Glass",
        "#6DEBDE",
        "#949494",
        opacity: 0.6,
        overlay: "rgba(0,0,255,0.3)"
        }, {
        "Pirate",
        "#5E5E5E",
        "#737373"
        }, {
        "Sketch",
        "#E8E8E8",
        "#E8E8E8"
        }, {
        "Gold",
        "#e9cd5f",
        "#949494"
        }, {
        "Hazard",
        "#737373",
        "#e9cd5f"
        }, {
        "Apples",
        "#91DB30",
        "#eb6d6d"
        }, {
        "Beach",
        "#eac086",
        "#FFD57D"
        }, {
        "Wood",
        "#8c5d20",
        "#949494"
        }, {
        "Diamond",
        "#6FE8E2",
        "#EDEDED"
        }, {
        "Midnight",
        "#5E5E5E",
        "#A763BA"
        }, {
        "Valentine",
        "#FE4998",
        "#F9A7FE"
        }, {
        "Cheddar",
        "#FCA403",
        "#FDFC08"
        }, {
        "PewDie",
        "#09BBDF",
        "#1D3245"
        }, {
        "Crimson",
        "#6B3333",
        "#AD3E3E"
        }, {
        "Banana",
        "#fbf079",
        "#f9f9f9"
        }, {
        "Cherry",
        "#F8E0F7",
        "#F5A9F2"
        }, {
        "Moon",
        "#1C1C1C",
        "#F2F5A9"
        }, {
        "Master",
        "#fce525",
        "#bb5e0e"
        }, {
        "Reddit",
        "#fe562d",
        "#f9f9f9"
        }, {
        "4Chan",
        "#ffd3b4",
        "#3c8d2e"
        }, {
        "Necron",
        "#808080",
        "#80ff80"
        }, {
        "Ambient",
        "#626262",
        "#80ffff"
        }, {
        "Uranium",
        "#5a9452",
        "#80ff80"
        }, {
        "XPlode",
        "#fe4c00",
        "#f8bf00"
        }, {
        "巧克力",
        "#804029",
        "#f9ebb4"
        };
