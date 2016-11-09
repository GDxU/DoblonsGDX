package tbs.doblon.io;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.ArrayList;

public class Game extends GameBase {
    @Override
    public void create() {
        super.create();
        SocketManager.init();
    }

    @Override
    public void resume() {
        super.resume();
//        resumeSounds();
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
//        pauseSounds();
        paused = true;
        super.pause();
    }

    String socket;
    int port;
    int controlIndex = 0;
    int targetFPS = 60;
    public static float delta, delta2;
    public static long currentTime, oldTime;
    private static final int sendFrequency = 10, tUpdateFrequency = 10;
    private static long lastUpdated = 0, lastSent = 0;
    int gameState = 0;
    Object gameData;
    boolean upgradesHidden = false;
    int instructionsSpeed = 5500;
    int insturctionsCountdown = 0;
    public static final  String[] instructionsList = {
            "use A & D or Arrow Keys control the direction of your ship",
            "use your mouse to aim and fire your cannons",
            "destroy enemy ships and collect doblons to upgrade your ship"
    };
    String partyKey;
    Player player = null;
    public static final ArrayList modeList = null;
    String modeIndex = null;
    String currentMode = null;
    public static final  int dayTimeValue = 0;
    public static final ArrayList<Player> users = new ArrayList<Player>();
    public static final ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    float target = 0;
    float targetD = 1;
    float turnDir = 0;
    float speedInc = 0;
    int mTarget = 0;
    int instructionsIndex = Utility.randInt(0, instructionsList.length - 1);
    String[] randomLoadingTexts = {
            "discovering treasure...",
            "setting sail..."
    };

   static long lastAdShown = System.currentTimeMillis();
    //Todo set this value in pause/resume
    static boolean paused;
    // SCALING:
    static int viewMult = 1,maxScreenWidth = 2208,maxScreenHeight = 1242; // 1080;
    static int originalScreenWidth = maxScreenWidth, originalScreenHeight = maxScreenHeight,screenWidth, screenHeight;
    public static void showAd() {
        Utility.log("showAds");
        if (!paused && (1200 < (System.currentTimeMillis() - lastAdShown))) {
            lastAdShown = System.currentTimeMillis();
          //Todo show an ad
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
    static float screenSkX ,screenShackeScale,screenSkY ,screenSkRed , screenSkDir = 0;
    public static void screenShake(float scl,float dir) {
        if (screenShackeScale < scl) {
            screenShackeScale = scl;
            screenSkDir = dir;
        }
    }
    public static void updateScreenShake(long delta) {
        if (screenShackeScale > 0) {
            screenSkX = (float)(screenShackeScale * Math.cos(screenSkDir));
            screenSkY = (float) (screenShackeScale * Math.sin(screenSkDir));
            screenShackeScale *= screenSkRed;
            if (screenShackeScale <= 0.1)
                screenShackeScale = 0;
        }
    }

    float playerCanvasScale = 430, maxFlashAlpha = 0.25f;

    // SKULL ICONS:
    static int iconsCount = 5;
    static float skullIconSize = 50;
    Texture[] iconsList = {};
    initIconList();
    static void initIconList(){
        for (int i = 1; i < iconsCount; ++i) {
            var tmpImg = new Image();
            tmpImg.onload = (public static void (val) {
            return public static void () {
                this.onLoad = null;
                iconsList[val] = this;
            }
            })(i);
            tmpImg.src = ".././img/icons/skull_" + i + ".png";
        }
    }

    public static final IslandInfo[] islandInfo = {
            new IslandInfo(17,0xe0cca7, new float[]{0.92f, 0.95f, 1, 1.05f, 1, 0.85f, 0.95f, 1, 1.1f, 1, 0.96f}),
  new IslandInfo(17, 0xd4c19e,new float[]{1, 0.94f, 1, 1.13f, 0.98f, 1.05f, 1.1f, 1, 0.96f}),
   new IslandInfo(17,0xc7b694, new float[]{1.05f, 0.92f, 1, 1.06f, 1, 0.98f, 1, 0.92f}),
   new IslandInfo(5,0xa4a4a4, new float[]{1.05f, 0.92f, 1, 1.06f, 1, 0.98f, 1, 0.92f})
    };

    // ANIM TEXT:
    static final ArrayList<AnimText> animTexts = new ArrayList<AnimText>();
   static int animTextIndex = 0;
    int scoreCountdown = 0;
    int lastScore = 0;
    int scoreDisplayTime = 1500;
    for (var i = 0; i < 20; ++i) {
        animTexts.push(new AnimText());
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

    public static void drawIsland(float x, float y,float s,int indx) {
        var tmpIndx = (s + "-" + indx);
        var tmpSprt = islandSprites[tmpIndx];
        if (!tmpSprt) {
            tmpIsl = islandInfo[indx];
            if (!tmpIsl) {
                tmpIsl = islandInfo[0];
            }
            var tmpCanvas = document.createElement("canvas");
            var shapeRenderer(fill) = tmpCanvas.getContext('2d');
            tmpCanvas.width = (s * 2) + (indx < 3 ? 300 : 10);
            tmpCanvas.height = tmpCanvas.width;
            shapeRenderer(fill).fillStyle = tmpIsl.color;
            shapeRenderer(fill).strokeStyle = darkColor;
            shapeRenderer(fill).translate(tmpCanvas.width / 2, tmpCanvas.height / 2);
            var tmpOff = (s * tmpIsl.offsets[0]);
            shapeRenderer(fill).beginPath();
            shapeRenderer(fill).moveTo((tmpOff * MathCOS(0)), (tmpOff * MathSIN(0)));

            // ISLAND BASE:
            var offIndx = 0;
            for (var i = 1; i <= tmpIsl.sides - 1; i++) {
                offIndx++;
                if (offIndx >= tmpIsl.offsets.length - 1)
                    offIndx = 0;
                var tmpOff = (s * tmpIsl.offsets[offIndx]);
                shapeRenderer(fill).lineTo((tmpOff * MathCOS(i * 2 * Math.PI / tmpIsl.sides)), (tmpOff * MathSIN(i * 2 * Math.PI / tmpIsl.sides)));
            }
            shapeRenderer(fill).closePath();
            if (indx < 3) {
                shapeRenderer(fill).lineWidth = 300;
                shapeRenderer(fill).globalAlpha = 0.1;
                shapeRenderer(fill).stroke();
                shapeRenderer(fill).lineWidth = 120;
                shapeRenderer(fill).stroke();
            }
            shapeRenderer(fill).lineWidth = 8.5;
            shapeRenderer(fill).globalAlpha = 1;
            shapeRenderer(fill).stroke();
            shapeRenderer(fill).fill();

            // PALM TREES:


            islandSprites[tmpIndx] = tmpCanvas;
            tmpSprt = islandSprites[tmpIndx];
        }
        spriteBatch().draw(tmpSprt, x - tmpSprt.width / 2, y - tmpSprt.height / 2,
                tmpSprt.width, tmpSprt.height);
    }

    public static void showAnimText(x, y, txt, scale, fadeDelay, type, sclPlus) {
        var tmpText = animTexts[animTextIndex];
        tmpText.show(x, y, txt, scale, fadeDelay, sclPlus);
        tmpText.type = type;
        animTextIndex++;
        if (animTextIndex >= animTexts.length)
            animTextIndex = 0;
    }

    var kickReason = null;
    public static void kickPlayer(reason) {
        leaveGame();
        if (!kickReason)
            kickReason = reason;
        showMainMenuText(kickReason);
        SocketManager.socket.close();
    }

    // UPDATE OR PUSH PLAYER:
    public static void updateOrPushUser(Player user) {
        int tmpIndx = getPlayerIndex(user.sid);
        if (tmpIndx >=0) {
            users.set(tmpIndx,user);
        } else {
            users.add(user);
        }
    }

    public static void unlockSkins(int indx) {
        if (!indx) {
            skinInfo.style.display = "inline-block";
            skinSelector.style.display = "inline-block";
            if (hasStorage) {
                Utility.saveString("isFollDob", 1);
            }
        }
    }
    static int darkColor = 0x4d4d4d;

    // PAGE IS READY:
    public static void getURLParam(name, url) {
        if (!url) url = location.href;
        name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
        var regexS = "[\\?&]" + name + "=([^&#]*)";
        var regex = new RegExp(regexS);
        var results = regex.exec(url);
        return results == null ? null : results[1];
    }
    var lobbyURLIP = getURLParam("l");
    var lobbyRoomID;
    if (lobbyURLIP) {
        var tmpL = lobbyURLIP.split("-");
        lobbyURLIP = tmpL[0];
        lobbyRoomID = tmpL[1];
    }


    boolean activePopup;
    public static void showWeaponPopup(int indx) {
        for (var i = 0; i < 4; i++) {
            var tmpDiv = document.getElementById('popupRow' + i);
            if (tmpDiv) {
                if (i != indx || tmpDiv.style.visibility == 'visible') {
                    tmpDiv.style.visibility = 'hidden';
                } else {
                    tmpDiv.style.visibility = 'visible';
                }
            }
        }
    }

    public static final ArrayList<TextureRegion> renderedSkins = new ArrayList<TextureRegion>();
    public static int skinIndex = Utility.getInt("sknInx");
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

    var skinDisplayIconSize = 200;
    public static void changeSkin(val) {
        skinIndex += val;
        if (skinIndex >= userSkins.length)
            skinIndex = 0;
        else if (skinIndex < 0)
            skinIndex = userSkins.length - 1;
        if (!renderedSkins[skinIndex]) {
            var tmpCanvas = document.createElement('canvas');
            tmpCanvas.width = tmpCanvas.height = skinDisplayIconSize;
            var shapeRenderer(fill) = tmpCanvas.getContext('2d');
            shapeRenderer(fill).translate((tmpCanvas.width / 2), (tmpCanvas.height / 2));
            shapeRenderer(fill).lineJoin = "round";
            renderPlayer(shapeRenderer(fill), {
                    dir: (Math.PI),
                    width: 60,
                    length: 125,
                    rearLength: 25,
                    noseLength: 35,
                    cannonLength: 18,
                    cannonWidth: 28,
                    cannons: 1
            }, 0, 0, userSkins[skinIndex]);
            renderedSkins[skinIndex] = tmpCanvas.toDataURL();
        }
        skinIcon.src = renderedSkins[skinIndex];
        skinName.innerHTML = userSkins[skinIndex].name;
        if (hasStorage) {
            Utility.saveString("sknInx", skinIndex);
        }
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

    public static void drawCircle(float x, float y, float s) {
        shapeRenderer(fill).circle(x,y,s);
    }

    public static void renderPlayer(Player tmpObj, float tmpX, float tmpY,float tmpS, float delta) {

        // RENDER PLAYER SHIP:
        contxt.lineWidth = 8.5;

        // SIDE ITEMS:
        if (true) {
            contxt.fillStyle = tmpS.color2;
            contxt.strokeStyle = darkColor;
            var tmpL = (tmpObj.length - (tmpObj.rearLength + tmpObj.noseLength));
            var cW = ((tmpObj.cannonLength * 2) + tmpObj.width + contxt.lineWidth) * (tmpObj.animMults ? (tmpObj.animMults[0].mult || 1) : 1);

            // REGULAR CANNONS:
            if (tmpObj.cannons) {
                var cY = -(tmpObj.cannons - 1) * ((tmpL / tmpObj.cannons + 1) / 2);
                for (var c = 0; c < tmpObj.cannons; ++c) {
                    contxt.roundRect(-cW / 2, cY + ((tmpL / tmpObj.cannons) * c) - (tmpObj.cannonWidth / 2), cW, tmpObj.cannonWidth, 0).stroke();
                    contxt.fill();
                }
            }

            // SCATTER CANNONS:
            if (tmpObj.scatterCannons) {
                contxt.save();
                contxt.rotate(Math.PI / 2);
                var cY = -(tmpObj.scatterCannons - 1) * ((tmpL / tmpObj.scatterCannons + 1) / 2);
                for (var c = 0; c < tmpObj.scatterCannons; ++c) {
                    for (var c2 = 0; c2 < 2; ++c2) {
                        contxt.roundRect(cY + ((tmpL / tmpObj.scatterCannons) * c) - (tmpObj.cannonWidth / 2), -cW / 2.4, tmpObj.cannonWidth, cW / 2.4, 0, 1.3).stroke();
                        contxt.fill();
                        contxt.rotate(Math.PI);
                    }
                }
                contxt.restore();
            }

            // ROWS:
            if (tmpObj.rows) {
                if (!tmpObj.rowRot) {
                    tmpObj.rowRot = 0;
                }
                if (!tmpObj.rowSpeed) {
                    tmpObj.rowSpeed = 0.002;
                }
                tmpObj.rowRot += tmpObj.rowSpeed * delta;
                if (tmpObj.rowRot >= 0.3) {
                    tmpObj.rowRot = 0.3;
                    tmpObj.rowSpeed = -0.001;
                }
                if (tmpObj.rowRot <= -0.35) {
                    tmpObj.rowRot = -0.35;
                    tmpObj.rowSpeed = 0.002;
                }
                var cY = (tmpObj.rows - 1) * ((tmpL / tmpObj.rows + 1) / 2);
                var tmpW = tmpObj.width / 5;
                cW = (9 + tmpObj.width);
                for (var c = 0; c < tmpObj.rows; ++c) {
                    var tmpVal = cY - ((tmpL / tmpObj.rows) * c);
                    contxt.save();
                    contxt.translate(0, tmpVal);
                    contxt.rotate(tmpObj.rowRot);
                    contxt.roundRect(0, -(tmpW / 2), cW, tmpW, 0).stroke();
                    contxt.fill();
                    contxt.rotate(-(tmpObj.rowRot * 2) - Math.PI);
                    contxt.roundRect(0, -(tmpW / 2), cW, tmpW, 0).stroke();
                    contxt.fill();
                    contxt.restore();
                }
            }
        }

        // REAR ITEMS:
        if (true) {

            // MINE DROPPER:
            if (tmpObj.mineDropper) {
                contxt.roundRect(-(tmpObj.width / 2) * 0.55, -(tmpObj.length / 2) - (15 * (tmpObj.animMults ? (tmpObj.animMults[2].mult || 1) : 1)) + 2,
                        tmpObj.width * 0.55, 17, 0, 1.2).stroke();
                contxt.fill();
            }

            // REAR CANNON:
            if (tmpObj.rearCannon) {
                contxt.roundRect(-(tmpObj.width / 2) * 0.55, -(tmpObj.length / 2) - (15 * (tmpObj.animMults ? (tmpObj.animMults[2].mult || 1) : 1)) + 2,
                        tmpObj.width * 0.55, 17, 0).stroke();
                contxt.fill();
            }

            // RUDDER:
            if (tmpObj.rudder) {
                contxt.save();
                contxt.translate(0, -(tmpObj.length / 2));
                contxt.roundRect(-4, -tmpObj.rudder, 8, tmpObj.rudder, 0).stroke();
                contxt.fill();
                contxt.restore();
            }
        }

        // FRONT WEAPONS:
        if (true) {

            // RAM:
            if (tmpObj.ramLength) {
                contxt.beginPath();
                contxt.moveTo((tmpObj.width / 2.5), (tmpObj.length / 2) - tmpObj.noseLength);
                contxt.lineTo((tmpObj.width / 20), (tmpObj.length / 2) + tmpObj.ramLength);
                contxt.lineTo(-((tmpObj.width / 20)), (tmpObj.length / 2) + tmpObj.ramLength);
                contxt.lineTo(-(tmpObj.width / 2.5), (tmpObj.length / 2) - tmpObj.noseLength);
                contxt.closePath();
                contxt.stroke();
                contxt.fill();
            }

            // CHASE CANNONS:
            if (tmpObj.chaseCannons) {
                var cWid = tmpObj.cannonWidth / 2.5;
                contxt.roundRect(-(tmpObj.width / 2), 0, cWid, ((tmpObj.length / 2) - tmpObj.noseLength) + ((tmpObj.cannonLength * 2.3) * (tmpObj.animMults ? (tmpObj.animMults[3].mult || 1) : 1)), 0).stroke();
                contxt.fill();
                contxt.roundRect((tmpObj.width / 2) - cWid, 0, cWid, ((tmpObj.length / 2) - tmpObj.noseLength) + ((tmpObj.cannonLength * 2.3) * (tmpObj.animMults ? (tmpObj.animMults[3].mult || 1) : 1)), 0).stroke();
                contxt.fill();
            }
        }

        // HULL:
        var nsLngthPls = (tmpObj.length / 1.85);
        contxt.fillStyle = tmpS.color1;
        contxt.beginPath();
        contxt.moveTo(0, -(tmpObj.length / 2));
        contxt.lineTo((tmpObj.width / 2) * 0.55, -(tmpObj.length / 2));
        contxt.lineTo((tmpObj.width / 2), -(tmpObj.length / 2) + tmpObj.rearLength);
        contxt.lineTo((tmpObj.width / 2), (tmpObj.length / 2) - tmpObj.noseLength);
        contxt.quadraticCurveTo((tmpObj.width / 2), nsLngthPls - (tmpObj.noseLength / 2), 0, nsLngthPls);
        contxt.quadraticCurveTo(-(tmpObj.width / 2), nsLngthPls - (tmpObj.noseLength / 2),
                -(tmpObj.width / 2), nsLngthPls - tmpObj.noseLength);
        contxt.lineTo(-(tmpObj.width / 2), -(tmpObj.length / 2) + tmpObj.rearLength);
        contxt.lineTo(-(tmpObj.width / 2) * 0.55, -(tmpObj.length / 2));
        contxt.closePath();
        contxt.stroke();
        contxt.fill();

        // DECK:
        if (true) {
            var tmpVal, tmpDir, cY;
            tmpL = tmpObj.length;
            contxt.fillStyle = tmpS.color2;

            // CROWS NEST:
            if (!tmpObj.swivelCannons && !tmpObj.gatlinCannons
                    && !tmpObj.twinCannons && !tmpObj.quadCannons && !tmpObj.autoCannons
                    && !tmpObj.bigCannon && !tmpObj.sniperCannon && !tmpObj.trippleCannons) {
                drawCircle(0, 0, (tmpObj.cannonWidth / 1.8), contxt);
            } else {

                // GATLIN CANNONS:
                var tmpMax = tmpObj.gatlinCannons;
                if (tmpMax) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2;
                    for (var c = 0; c < tmpObj.gatlinCannons; ++c) {
                        contxt.save();
                        tmpVal = cY - ((tmpL / tmpMax) * c);
                        contxt.translate(0, tmpVal);
                        contxt.rotate(tmpDir);
                        contxt.roundRect(0, -(tmpObj.cannonWidth / 2.5), (tmpObj.cannonLength * 2.1) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), (tmpObj.cannonWidth / 1.25), 0).stroke();
                        contxt.fill();
                        contxt.beginPath();
                        contxt.moveTo(0, 0);
                        contxt.lineTo((tmpObj.cannonLength * 2.1) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), 0);
                        contxt.closePath();
                        contxt.stroke();
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.8), contxt);
                        contxt.restore();
                    }
                }

                // BIG CANNON:
                tmpMax = tmpObj.bigCannon;
                if (tmpMax) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2;
                    for (var c = 0; c < tmpObj.bigCannon; ++c) {
                        contxt.save();
                        tmpVal = cY - ((tmpL / tmpMax) * c);
                        contxt.translate(0, tmpVal);
                        contxt.rotate(tmpDir - (Math.PI / 2));
                        contxt.roundRect(-(tmpObj.cannonWidth / 2), 0, (tmpObj.cannonWidth / 2) * 2, (tmpObj.cannonLength * 3) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), 0, 1.2).stroke();
                        contxt.fill();
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.2), contxt);
                        contxt.restore();
                    }
                }

                // SNIPER CANNON:
                var tmpMax = tmpObj.sniperCannon;
                if (tmpMax) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2;
                    for (var c = 0; c < tmpObj.sniperCannon; ++c) {
                        contxt.save();
                        tmpVal = cY - ((tmpL / tmpMax) * c);
                        contxt.translate(0, tmpVal);
                        contxt.rotate(tmpDir - (Math.PI / 2));
                        contxt.roundRect(-(tmpObj.cannonWidth / 2.2), 0, (tmpObj.cannonWidth / 2.2) * 2, (tmpObj.cannonLength * 3.5) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), 0).stroke();
                        contxt.fill();
                        contxt.roundRect(-(tmpObj.cannonWidth / 2), 0, (tmpObj.cannonWidth / 2) * 2, (tmpObj.cannonLength * 2.5) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), 0, 1.2).stroke();
                        contxt.fill();
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.2), contxt);
                        contxt.restore();
                    }
                }

                // SWIVEL CANNONS:
                var tmpMax = tmpObj.swivelCannons;
                if (tmpMax) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2;
                    for (var c = 0; c < tmpObj.swivelCannons; ++c) {
                        contxt.save();
                        tmpVal = cY - ((tmpL / tmpMax) * c);
                        contxt.translate(0, tmpVal);
                        contxt.rotate(tmpDir);
                        contxt.roundRect(0, -(tmpObj.cannonWidth / 2.4), (tmpObj.cannonLength * 2) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), tmpObj.cannonWidth / 1.2, 0).stroke();
                        contxt.fill();
                        drawCircle(0, 0, MathMAX((tmpObj.cannonWidth / 1.8), 13), contxt);
                        contxt.restore();
                    }
                }

                // TWIN/QUAD CANNONS:
                tmpMax = (tmpObj.twinCannons || tmpObj.quadCannons);
                if (tmpMax) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    var tmpCount = 2;
                    var rotPlus = Math.PI;
                    if (tmpObj.quadCannons) {
                        tmpCount = 4;
                        rotPlus /= 2;
                    }
                    tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2;
                    for (var c = 0; c < tmpMax; ++c) {
                        tmpVal = cY - ((tmpL / tmpMax) * c);
                        contxt.save();
                        contxt.translate(0, tmpVal);
                        contxt.rotate(tmpDir);
                        for (var c2 = 0; c2 < tmpCount; ++c2) {
                            contxt.roundRect(0, -(tmpObj.cannonWidth / 2.4), (tmpObj.cannonLength * 2.1) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), (tmpObj.cannonWidth / 1.25), 0).stroke();
                            contxt.fill();
                            contxt.rotate(rotPlus);
                        }
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.4), contxt);
                        contxt.restore();
                    }
                }

                // TRIPPLE CANNONS:
                tmpMax = tmpObj.trippleCannons;
                if (tmpMax) {
                    var tmpS = tmpObj.cannonWidth / 1.3;
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2;
                    for (var c = 0; c < tmpMax; ++c) {
                        tmpVal = cY - ((tmpL / tmpMax) * c);
                        contxt.save();
                        contxt.translate(0, tmpVal);
                        contxt.rotate(tmpDir);
                        contxt.roundRect(0, -tmpS, (tmpObj.cannonLength * 2.1) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), (tmpS / 1.4), 0).stroke();
                        contxt.fill();
                        contxt.roundRect(0, tmpS - (tmpS / 1.4), (tmpObj.cannonLength * 2.1) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), (tmpS / 1.4), 0).stroke();
                        contxt.fill();
                        drawCircle(0, 0, tmpS, contxt);
                        contxt.restore();
                    }
                }

                // AUTO CANNONS:
                tmpMax = tmpObj.autoCannons;
                if (tmpMax) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    for (var c = 0; c < tmpObj.autoCannons; ++c) {
                        contxt.save();
                        tmpVal = cY - ((tmpL / tmpMax) * c);
                        tmpDir = tmpObj.aimDir - tmpObj.dir + Math.PI / 2;
                        contxt.translate(0, tmpVal);
                        contxt.rotate(tmpDir);
                        contxt.roundRect(0, -(tmpObj.cannonWidth / 2.6), (tmpObj.cannonLength * 2) * (tmpObj.animMults ? (tmpObj.animMults[1].mult || 1) : 1), tmpObj.cannonWidth / 1.3, 0).stroke();
                        contxt.fill();
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.85), contxt);
                        contxt.restore();
                    }
                }
            }
        }

        // EFFECT OVERLAY:
        if (tmpS.overlay) {
            contxt.globalCompositeOperation = "source-atop";
            contxt.fillStyle = tmpS.overlay;
            contxt.fillRect(-playerCanvas.width / 2, -playerCanvas.height / 2, playerCanvas.width,
                    playerCanvas.height);
            contxt.globalCompositeOperation = "source-over";
        }
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

public static void sendTarget(float force) {
        long tmpTime = currentTime;
        if (player!=null  && !player.dead) {
        target = Math.atan2(mouseY - (screenHeight / 2), mouseX - (screenWidth / 2));
        if (force || tmpTime - lastUpdated > tUpdateFrequency) {
        if (controlIndex == 1) {
        targetD = Math.sqrt(MathPOW(mouseY - (screenHeight / 2), 2) + MathPOW(mouseX - screenWidth / 2, 2));
        targetD *= Math.min(maxScreenWidth / screenWidth, maxScreenHeight / screenHeight);
        targetD /= (maxScreenHeight / 3.5);
        targetD = targetD.round(1);
        if (targetD > 1)
        targetD = 1;
        else if (targetD < 0.5)
        targetD = 0.5;
        }
        lastUpdated = tmpTime;
        }
        if (force || tmpTime - lastSent > sendFrequency) {
        lastSent = tmpTime;
        if (controlIndex == 1) {
        socket.emit('1', target.round(2), targetD.round(1));
        } else {
        socket.emit('1', target);
        }
        }
        }
        }
public static void sendMoveTarget() {
        //Todo might have to do keys[]
        if (keys.r !=0&& keys.l!=0)
        turnDir = 0;
        if (!keys.u && !keys.d)
        speedInc = 0;
        SocketManager.socket.emit('4', turnDir, speedInc);
        }
public static void renderGameObject(tmpObj, ctxt) {
        var tmpIndx = (tmpObj.c + "-" + tmpObj.shp + "-" + tmpObj.s);
        var tmpSprt = gameObjSprites[tmpIndx];
        if (!tmpSprt) {
        var tmpCanvas = document.createElement("canvas");
        var shapeRenderer(fill) = tmpCanvas.getContext('2d');
        tmpCanvas.width = (tmpObj.s * 2) + 10;
        tmpCanvas.height = tmpCanvas.width;
        shapeRenderer(fill).strokeStyle = darkColor;
        shapeRenderer(fill).lineWidth = 8.5;
        shapeRenderer(fill).translate(tmpCanvas.width / 2, tmpCanvas.height / 2);
        if (tmpObj.c == 0) {
        shapeRenderer(fill).fillStyle = '#797979';
        } else if (tmpObj.c == 1) {
        shapeRenderer(fill).fillStyle = '#e89360';
        } else if (tmpObj.c == 2) {
        shapeRenderer(fill).fillStyle = '#c8c8c8';
        } else if (tmpObj.c == 3) {
        shapeRenderer(fill).fillStyle = '#e9cd5f';
        } else if (tmpObj.c == 4) {
        shapeRenderer(fill).fillStyle = '#EB6565';
        } else if (tmpObj.c == 5) {
        shapeRenderer(fill).fillStyle = '#6FE8E2';
        } else if (tmpObj.c == 6) {
        shapeRenderer(fill).fillStyle = '#7BE86F';
        }
        if (tmpObj.shp == 1) {
        var spikes = 6;
        var rot = Math.PI / 2 * 3;
        var rad = tmpObj.s / 2;
        var step = Math.PI / spikes;
        shapeRenderer(fill).beginPath();
        shapeRenderer(fill).moveTo(0, -rad);
        for (var s = 0; s < spikes; s++) {
        shapeRenderer(fill).lineTo(MathCOS(rot) * rad, MathSIN(rot) * rad);
        rot += step;
        shapeRenderer(fill).lineTo(MathCOS(rot) * (rad * 0.8), MathSIN(rot) * (rad * 0.8));
        rot += step;
        }
        shapeRenderer(fill).lineTo(0, -rad);
        shapeRenderer(fill).closePath();
        shapeRenderer(fill).stroke();
        shapeRenderer(fill).fill();
        } else if (tmpObj.shp == 2) {
        var rad = tmpObj.s / 1.6;
        shapeRenderer(fill).beginPath();
        shapeRenderer(fill).moveTo(0, -rad);
        shapeRenderer(fill).lineTo(rad, 0);
        shapeRenderer(fill).lineTo(0, rad);
        shapeRenderer(fill).lineTo(-rad, 0);
        shapeRenderer(fill).closePath();
        shapeRenderer(fill).stroke();
        shapeRenderer(fill).fill();
        } else if (tmpObj.shp == 3) {
        var rad = tmpObj.s / 1.6;
        shapeRenderer(fill).beginPath();
        shapeRenderer(fill).moveTo(0, -rad);
        shapeRenderer(fill).lineTo(rad / 1.5, 0);
        shapeRenderer(fill).lineTo(0, rad);
        shapeRenderer(fill).lineTo(-rad / 1.5, 0);
        shapeRenderer(fill).closePath();
        shapeRenderer(fill).stroke();
        shapeRenderer(fill).fill();
        } else {
        shapeRenderer(fill).beginPath();
        shapeRenderer(fill).arc(0, 0, tmpObj.s / 2, 0, 2 * Math.PI);
        shapeRenderer(fill).stroke();
        shapeRenderer(fill).fill();
        }
        gameObjSprites[tmpIndx] = tmpCanvas;
        tmpSprt = gameObjSprites[tmpIndx];
        }
        spriteBatch().draw(tmpSprt, -tmpSprt.width / 2, -tmpSprt.height / 2,
        tmpSprt.width, tmpSprt.height);
        }



// UPDATE MENU:
public static void updateMenuLoop(delta) {
        if (gameState != 1) {

        // MENU INSTRUCTIONS TEXT:
        insturctionsCountdown -= delta;
        if (insturctionsCountdown <= 0) {
        insturctionsCountdown = instructionsSpeed;
        instructionsText.innerHTML = instructionsList[instructionsIndex];
        instructionsIndex++;
        if (instructionsIndex >= instructionsList.length)
        instructionsIndex = 0;
        }
        }
        }
