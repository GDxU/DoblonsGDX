package tbs.doblon.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import tbs.doblon.io.views.EditText;
import tbs.doblon.io.views.HUDManager;

public class Game extends GameBase {
    public static final String[] instructionsList = {
            "use A & D or Arrow Keys control the direction of your ship",
            "use your mouse to aim and fire your cannons",
            "destroy enemy ships and collect doblons to upgrade your ship"
    };
    public static final ArrayList<String> modeList = new ArrayList<String>();
    public static final ArrayList<Player> users = new ArrayList<Player>();
    public static final ArrayList<Obstacle> gameObjects = new ArrayList<Obstacle>();
    public static final IslandInfo[] islandInfo = {
            new IslandInfo(17, 0xe0cca7, new float[]{0.92f, 0.95f, 1, 1.05f, 1, 0.85f, 0.95f, 1, 1.1f, 1, 0.96f}),
            new IslandInfo(17, 0xd4c19e, new float[]{1, 0.94f, 1, 1.13f, 0.98f, 1.05f, 1.1f, 1, 0.96f}),
            new IslandInfo(17, 0xc7b694, new float[]{1.05f, 0.92f, 1, 1.06f, 1, 0.98f, 1, 0.92f}),
            new IslandInfo(5, 0xa4a4a4, new float[]{1.05f, 0.92f, 1, 1.06f, 1, 0.98f, 1, 0.92f})
    };
    public static final ArrayList<TextureRegion> renderedSkins = new ArrayList<TextureRegion>();
    public static final String treasureMap = "2B=TK:KAB,SSV:100K";
    public static final MiniMap minimap = new MiniMap();
    // ANIM TEXT:
    static final ArrayList<AnimText> animTexts = new ArrayList<AnimText>();
    static final int[] pingColors = {0xffffff, 0xffffff, 0xff6363, 0xff6363, 0x67ff3e66, 0xffffff88, 0x63b0ff};
    private static final int sendFrequency = 10, tUpdateFrequency = 10;
    public static OrthographicCamera camera = new OrthographicCamera();
    public static String socket;
    public static String scoreText;
    public static String upgradesText;
    public static String coinDisplayText, lobbyURLIP;
    public static int lobbyRoomID;
    public static GameController gameController = new GameController();
    public static ArrayList<UpgradeItem> upgradeItems = new ArrayList<UpgradeItem>(12);
    public static int port;
    public static int controlIndex = 0;
    public static int targetFPS = 60;
    public static float delta, delta2;
    public static long currentTime, oldTime;
    public static int gameState = 0;
    public static GameData gameData;
    public static boolean upgradesHidden = false;
    public static int instructionsSpeed = 5500;
    public static int insturctionsCountdown = 0;
    public static String partyKey;
    public static Player player = null;
    public static String modeIndex = null;
    public static String leaderboardText = null;
    public static String currentMode = null;
    public static String dayTimeValue = "";
    public static float target = 0;
    public static float targetD = 1;
    public static float turnDir = 0;
    public static float speedInc = 0;
    public static int mTarget = 0;
    public static int instructionsIndex = Utility.randInt(0, instructionsList.length - 1);
    public static String[] randomLoadingTexts = {
            "discovering treasure...",
            "setting sail..."
    };
    public static float playerCanvasScale = 430, maxFlashAlpha = 0.25f;
    public static Texture[] iconsList = {};
    public static int skinIndex;
    public static TextureRegion skinIcon;
    public static String skinName;
    public static Keys keys = new Keys();//l,r,u,d
    public static String cid;
    public static int mouseX, mouseY;
    public static boolean forceTarget = true, shooting;
    public static String userNameInput;
    public static ArrayList<Skin> userSkins = new ArrayList<Skin>();
    public static String instructionsText = "";
    static long lastAdShown = System.currentTimeMillis();
    //Todo set this value in pause/resume
    static boolean paused;
    // SCALING:
    static float viewMult = 1;
    static int maxScreenWidth = 2208, maxScreenHeight = 1242; // 1080;
    static int originalScreenWidth = maxScreenWidth, originalScreenHeight = maxScreenHeight;
    // SCREEN SHAKE:
    static float screenSkX, screenShackeScale, screenSkY, screenSkRed, screenSkDir = 0;
    // SKULL ICONS:
    static int iconsCount = 5;

//    initIconList();

    //    static void initIconList() {
//        for (int i = 1; i < iconsCount; ++i) {
//            var tmpImg = new Image();
//            tmpImg.onload = (public static void(val) {
//            return public static void() {
//                this.onLoad = null;
//                iconsList[val] = this;
//            }
//            })(i);
//            tmpImg.src = ".././img/icons/skull_" + i + ".png";
//        }
//    }
    static float skullIconSize = 50;
    static int animTextIndex = 0;
    static int darkColor = 0x4d4d4d;
    static int lastScore;
    static long scoreCountdown, scoreDisplayTime;
    static int skinDisplayIconSize = 200;
    private static long lastUpdated = 0, lastSent = 0;
    private static short a = initAnimTexts();
    String kickReason;
    boolean activePopup;
    int o = initSkins();

    public static void showAd() {
        Utility.log("showAds");
        if (!paused && (1200 < (System.currentTimeMillis() - lastAdShown))) {
            lastAdShown = System.currentTimeMillis();
            //Todo show an ad
        }
    }

    public static Player getPlayerIndexById(String id) {
        for (int i = 0; i < Level.players.size(); i++) {
            final Player p = Level.players.get(i);
            if (p.id.equals(id)) {
                return p;
            }
        }

        return null;
    }

    // CHECK IF PLAYER IS IN ARRAY:
    public static boolean objectExists(Player obj) {

        for (int i = 0; i < Level.players.size(); i++) {
            final Player p = Level.players.get(i);
            if (p.sid == obj.sid) {
                return true;
            }
        }

        return false;
    }

    // PAGE IS READY:
//    public static void getURLParam(name, url) {
//        if (!url) url = location.href;
//        name = name.replace( /[\[]/,"\\\[").replace( /[\]]/,"\\\]");
//        var regexS = "[\\?&]" + name + "=([^&#]*)";
//        var regex = new RegExp(regexS);
//        var results = regex.exec(url);
//        return results == null ? null : results[1];
//    }

//    public static String lobbyURLIP = getURLParam("l");
//    if(lobbyURLIP)
//    {
//        String tmpL = lobbyURLIP.split("-");
//        lobbyURLIP = tmpL.get(0);
//        lobbyRoomID = tmpL.get(1);
//    }

    // FIND PLAYER INDEX:
    public static int getPlayerIndex(int sid) {

        for (int i = 0; i < Level.players.size(); i++) {
            final Player p = Level.players.get(i);
            if (p.sid == sid) {
                return i;
            }
        }

        return -1;
    }

    public static void screenShake(float scl, float dir) {
        if (screenShackeScale < scl) {
            screenShackeScale = scl;
            screenSkDir = dir;
        }
    }

    public static void updateScreenShake(long delta) {
        if (screenShackeScale > 0) {
            screenSkX = (float) (screenShackeScale * Math.cos(screenSkDir));
            screenSkY = (float) (screenShackeScale * Math.sin(screenSkDir));
            screenShackeScale *= screenSkRed;
            if (screenShackeScale <= 0.1)
                screenShackeScale = 0;
        }
    }

    public static short initAnimTexts() {
        for (int i = 0; i < 20; ++i) {
            animTexts.add(new AnimText());
        }

        return 0;
    }

    public static void updateAnimTexts(float delta) {
        // UPDATE COOLDOWNS:
        if (scoreCountdown >= 0) {
            scoreCountdown -= delta;
            if (scoreCountdown <= 0) {
                scoreCountdown = 0;
                lastScore = 0;
            }
        }

        // RENDER:

        for (int i = 0; i < animTexts.size(); ++i) {
            final AnimText animText = animTexts.get(i);
            animText.update(delta);
            //todo might have to draw text with a bigger size mainContext.strokeStyle = 0x5f5f5f;
            Utility.drawCenteredText(spriteBatch(), 0xfffff, animText.text, animText.x, animText.y, animText.scale);
        }
    }

    public static void drawIsland(float x, float y, float s, int indx) {
        //Todo render islands onto sprite sheet
//Todo        final ShapeRenderer renderer = shapeRenderer(fill);
//          IslandInfo tmpIsl = islandInfo[indx];
//
//            if (tmpIsl == null) {
//                tmpIsl = islandInfo[0];
//            }
//// Todo           tmpCanvas.width = (s * 2) + (indx < 3 ? 300 : 10);
//           renderer.setColor(Utility.tmpColor.set(tmpIsl.color));
////       Todo     renderer.strokeStyle = darkColor;
//            float tmpOff = (s * tmpIsl.offsets[0]);
//
//            renderer.moveTo((tmpOff * Math.cos(0)), (tmpOff * Math.sin(0)));
//
//            // ISLAND BASE:
//            int offIndx = 0;
//            for (int i = 1; i <= tmpIsl.sides - 1; i++) {
//                offIndx++;
//                if (offIndx >= tmpIsl.offsets.length - 1)
//                    offIndx = 0;
//                float tmpOff1 = (s * tmpIsl.offsets[offIndx]);
//                renderer.lineTo((tmpOff1 * Math.cos(i * 2 * Math.PI / tmpIsl.sides)), (tmpOff1 * Math.sin(i * 2 * Math.PI / tmpIsl.sides)));
//            }
////  Todo          renderer.closePath();
//            if (indx < 3) {
//                renderer.lineWidth = 300;
//                renderer.globalAlpha = 0.1;
//                renderer.stroke();
//                renderer.lineWidth = 120;
//                renderer.stroke();
//            }
//            renderer.lineWidth = 8.5;
//            renderer.globalAlpha = 1;
//
//            // PALM TREES:
//
//
//            islandSprites[tmpIndx] = tmpCanvas;
//            tmpSprt = islandSprites[tmpIndx];
//        spriteBatch().draw(tmpSprt, x - tmpSprt.width / 2, y - tmpSprt.height / 2,
//                tmpSprt.width, tmpSprt.height);
    }

    public static void showAnimText(float x, float y, String txt, float scale, long fadeDelay, String type, float sclPlus) {
        AnimText tmpText = animTexts.get(animTextIndex);
        tmpText.show(x, y, txt, scale, fadeDelay, sclPlus);
        tmpText.type = type;
        animTextIndex++;
        if (animTextIndex >= animTexts.size())
            animTextIndex = 0;
    }

    public static void kickPlayer(String reason) {
        leaveGame();
        showMainMenuText(reason);
        SocketManager.socket.close();
    }

    // UPDATE OR PUSH PLAYER:
    public static void updateOrPushUser(JSONObject obj) {
        //Todo check
        Utility.log("push > ");
        Utility.log(obj.toString());
        int tmpIndx = getPlayerIndex(obj.getInt("sid"));
        if (tmpIndx >= 0) {
            final Player player = users.get(tmpIndx);
            player.updateData(obj);
        } else {
            users.add(new Player(obj));
        }
    }

    public static void unlockSkins(int indx) {
        if (indx != 0) {
//      todo      skinInfo.style.display = "inline-block";
//            skinSelector.style.display = "inline-block";
            Utility.saveInt("isFollDob", 1);
        }
    }

    public static void showWeaponPopup(int indx) {
        for (int i = 0; i < 4; i++) {
//     todo       final Button tmpDiv = document.getElementById("popupRow" + i);
//            if (tmpDiv!=null ) {
//            tmpDiv.visible = !tmpDiv.visible;
//            }
        }
    }

    // NOTIFICATIONS:
    public static void hideNotifByType(String type) {
        for (int i = 0; i < animTexts.size(); ++i) {
            if (animTexts.get(i).type.equals(type))
                animTexts.get(i).active = false;
        }
    }

    public static void showNotification(String text) {
        for (int i = 0; i < animTexts.size(); ++i) {
            if (animTexts.get(i).type.equals("notif"))
                animTexts.get(i).active = false;
        }
        showAnimText(maxScreenWidth / 2, maxScreenHeight / 1.27f, text, 42, 1500, "notif", 0.19f);
    }

    public static void showBigNotification(String text) {
        hideNotifByType("bNotif");
        showAnimText(maxScreenWidth / 2, screenHeight / 3, text, 130, 1000, "bNotif", 0.26f);
    }

    public static void showScoreNotif(int value) {
        hideNotifByType("sNotif");
        lastScore += value;
        showAnimText(maxScreenWidth / 2, maxScreenHeight / 1.34f, ("+" + lastScore), 35, scoreDisplayTime, "sNotif", 0.16f);
        scoreCountdown = scoreDisplayTime;
    }

    public static void changeSkin(int val) {
        final ShapeRenderer renderer = shapeRenderer(fill);
        skinIndex += val;
        if (skinIndex >= userSkins.size())
            skinIndex = 0;
        else if (skinIndex < 0)
            skinIndex = userSkins.size() - 1;
        if (renderedSkins.get(skinIndex) != null) {
            //Todo w= skinDisplayIconSize

//   Todo         renderPlayer(renderer, {
//                    dir:(Math.PI),
//                    width:60,
//                    length:125,
//                    rearLength:25,
//                    noseLength:35,
//                    cannonLength:18,
//                    cannonWidth:28,
//                    cannons:1
//            },0, 0, userSkins.get(skinIndex));
//            renderedSkins.get(skinIndex) = tmpCanvas.toDataURL();
        }
        skinIcon = renderedSkins.get(skinIndex);
        skinName = userSkins.get(skinIndex).name;
        Utility.saveInt("sknInx", skinIndex);
    }

    // SHOW A TEXT IN THE MENU:
    public static void showMainMenuText(String text) {
//  todo      userInfoContainer.style.display = "none";
//        loadingContainer.style.display = "block";
//        loadingContainer.innerHTML = text;
    }

    public static void hideMainMenuText() {
//   todo     userInfoContainer.style.display = "block";
//        loadingContainer.style.display = "none";
    }

    // TOGGLE UI:
    public static void toggleGameUI(boolean visible) {
//  todo      var display = visible ? "block" : "none";
//        gameUiContainer.style.display = display;
    }

    public static void toggleMenuUI(boolean visible) {

        // SHOWING MAIN MENU:
        if (visible) {
// todo           menuContainer.style.display = "flex";
//            darkener.style.display = "block";
            // linksContainer.style.display = "block";
//            target[2] = 0;
        }

        // HIDING MENU:
        else {
//            menuContainer.style.display = "none";
//            darkener.style.display = "none";
            // linksContainer.style.display = "none";
        }
    }

    public static void initAds() {
        //Todo
        final String adID = "ca-app-pub-6350309116730071/9878078741";
    }

    public static void resetKeys() {
        keys.l = 0;
        keys.r = 0;
    }

    public static void toggleUpgrades() {

    }

    public static void drawCircle(float x, float y, float s) {
        shapeRenderer(fill).circle(x, y, s);
    }

    public static void renderPlayer(Player tmpObj, float tmpX, float tmpY, Skin tmpS, float delta) {
        final ShapeRenderer render = shapeRenderer(fill);
        // RENDER PLAYER SHIP:
//        contxt.lineWidth = 8.5;
        final float lineWidth = 8.5f;
        // SIDE ITEMS:
        if (true) {
            render.setColor(Utility.tmpColor.set(tmpS.color2));
//            contxt.strokeStyle = darkColor;
            float tmpL = (tmpObj.length - (tmpObj.rearLength + tmpObj.noseLength));
            float cW = ((tmpObj.cannonLength * 2) + tmpObj.w + lineWidth) * (tmpObj.animMults != null ? (tmpObj.animMults.get(0).mult == 0 ? 1 : tmpObj.animMults.get(0).mult) : 1);

            // REGULAR CANNONS:
            if (tmpObj.cannons < 0) {
                float cY = -(tmpObj.cannons - 1) * ((tmpL / tmpObj.cannons + 1) / 2);
                for (int c = 0; c < tmpObj.cannons; ++c) {
                    render.rect(-cW / 2, cY + ((tmpL / tmpObj.cannons) * c) - (tmpObj.cannonWidth / 2), cW, tmpObj.cannonWidth);
                }
            }

            // SCATTER CANNONS:
            if (tmpObj.scatterCannons < 0) {
                //Todo contxt.rotate(Math.PI / 2);
                float cY = -(tmpObj.scatterCannons - 1) * ((tmpL / tmpObj.scatterCannons + 1) / 2);
                for (int c = 0; c < tmpObj.scatterCannons; ++c) {
                    for (int c2 = 0; c2 < 2; ++c2) {
                        //Todo double check why s is 1.3f
                        roundRect(cY + ((tmpL / tmpObj.scatterCannons) * c) - (tmpObj.cannonWidth / 2), -cW / 2.4f, tmpObj.cannonWidth, cW / 2.4f, 0);
//                        contxt.rotate(Math.PI);
                    }
                }
            }

            // ROWS:
            if (tmpObj.rows != 0) {
                if (tmpObj.rowRot != 0) {
                    tmpObj.rowRot = 0;
                }

                if (tmpObj.rowSpeed != 0) {
                    tmpObj.rowSpeed = 0.002f;
                }
                tmpObj.rowRot += tmpObj.rowSpeed * delta;
                if (tmpObj.rowRot >= 0.3f) {
                    tmpObj.rowRot = 0.3f;
                    tmpObj.rowSpeed = -0.001f;
                }
                if (tmpObj.rowRot <= -0.35f) {
                    tmpObj.rowRot = -0.35f;
                    tmpObj.rowSpeed = 0.002f;
                }
                float cY = (tmpObj.rows - 1) * ((tmpL / tmpObj.rows + 1) / 2);
                float tmpW = tmpObj.w / 5;
                cW = (9 + tmpObj.w);
                for (int c = 0; c < tmpObj.rows; ++c) {
                    float tmpVal = cY - ((tmpL / tmpObj.rows) * c);
//      todo              contxt.translate(0, tmpVal);
//                    contxt.rotate(tmpObj.rowRot);
                    roundRect(0, -(tmpW / 2), cW, tmpW, 0);
//          todo          contxt.rotate(-(tmpObj.rowRot * 2) - Math.PI);
                    roundRect(0, -(tmpW / 2), cW, tmpW, 0);
                }
            }
        }

        // REAR ITEMS:
        if (true) {

            // MINE DROPPER:
            if (tmpObj.mineDropper != 0) {
                roundRect(-(tmpObj.w / 2) * 0.55f, -(tmpObj.length / 2) - (15 * (tmpObj.animMults.size() > 2 ? (tmpObj.animMults.get(2).mult == 0 ? 1 : tmpObj.animMults.get(2).mult) : 1)) + 2,
                        tmpObj.w * 0.55f, 17, 1.2f);
            }

            // REAR CANNON:
            if (tmpObj.rearCannon != 0) {
                roundRect(-(tmpObj.w / 2) * 0.55f, -(tmpObj.length / 2) - (15 * (tmpObj.animMults.size() > 1 ? (tmpObj.animMults.get(2).mult == 0 ? 1 : tmpObj.animMults.get(2).mult) : 1)) + 2, tmpObj.w * 0.55f, 17, 0);
            }

            // RUDDER:
            if (tmpObj.rudder != 0) {
//         todo       contxt.translate(0, -(tmpObj.length / 2));
                roundRect(-4, -tmpObj.rudder, 8, tmpObj.rudder, 0);
            }
        }

        // FRONT WEAPONS:
        if (true) {

            // RAM:
            if (tmpObj.ramLength != 0) {
//     todo           contxt.moveTo((tmpObj.w / 2.5f), (tmpObj.length / 2) - tmpObj.noseLength);
//                render.line((tmpObj.w / 20f), (tmpObj.length / 2) + tmpObj.ramLength);
//                render.line(-((tmpObj.w / 20f)), (tmpObj.length / 2) + tmpObj.ramLength);
//                render.line(-(tmpObj.w / 2.5f), (tmpObj.length / 2) - tmpObj.noseLength);
//                contxt.closePath();
//                contxt.stroke();
//                contxt.fill();
            }

            // CHASE CANNONS:
            if (tmpObj.chaseCannons != 0) {
                float cWid = tmpObj.cannonWidth / 2.5f;
                roundRect(-(tmpObj.w / 2), 0, cWid, ((tmpObj.length / 2) - tmpObj.noseLength) + ((tmpObj.cannonLength * 2.3f) * (tmpObj.animMults.size() > 3 ? (tmpObj.animMults.get(3).mult == 0 ? 1 : tmpObj.animMults.get(3).mult) : 1)), 0);
                roundRect((tmpObj.w / 2) - cWid, 0, cWid, ((tmpObj.length / 2) - tmpObj.noseLength) + ((tmpObj.cannonLength * 2.3f) * (tmpObj.animMults.size() > 3 ? (tmpObj.animMults.get(3).mult == 0 ? 1 : tmpObj.animMults.get(3).mult) : 1)), 0);
            }
        }

        // HULL:
        float nsLngthPls = (tmpObj.length / 1.85f);
        render.setColor(Utility.tmpColor.set(tmpS.color1));
//    todo    contxt.moveTo(0, -(tmpObj.length / 2));
//        contxt.lineTo((tmpObj.w / 2) * 0.55f, -(tmpObj.length / 2));
//        contxt.lineTo((tmpObj.w / 2), -(tmpObj.length / 2) + tmpObj.rearLength);
//        contxt.lineTo((tmpObj.w / 2), (tmpObj.length / 2) - tmpObj.noseLength);
//        contxt.quadraticCurveTo((tmpObj.w / 2), nsLngthPls - (tmpObj.noseLength / 2), 0, nsLngthPls);
//        contxt.quadraticCurveTo(-(tmpObj.w / 2), nsLngthPls - (tmpObj.noseLength / 2),
//                -(tmpObj.w / 2), nsLngthPls - tmpObj.noseLength);
//        contxt.lineTo(-(tmpObj.w / 2), -(tmpObj.length / 2) + tmpObj.rearLength);
//        contxt.lineTo(-(tmpObj.w / 2) * 0.55f, -(tmpObj.length / 2));
//        contxt.closePath();
        //Todo contxt.stroke();

        // DECK:
        if (true) {
            float tmpVal, tmpDir, cY;
            float tmpL = tmpObj.length;
            Utility.tmpColor.set(tmpS.color2);

            // CROWS NEST:
            if (tmpObj.swivelCannons == 0 && tmpObj.gatlinCannons == 0
                    && tmpObj.twinCannons == 0 && tmpObj.quadCannons == 0 && tmpObj.autoCannons == 0
                    && tmpObj.bigCannon == 0 && tmpObj.sniperCannon == 0 && tmpObj.trippleCannons == 0) {
                drawCircle(0, 0, (tmpObj.cannonWidth / 1.8f));
            } else {

                // GATLIN CANNONS:
                int tmpMax = tmpObj.gatlinCannons;
                if (tmpMax != 0) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = (float) (((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2);
                    for (int c = 0; c < tmpObj.gatlinCannons; ++c) {
                        tmpVal = cY - ((tmpL / tmpMax) * c);
//                 todo       contxt.translate(0, tmpVal);
//                        contxt.rotate(tmpDir);
                        roundRect(0, -(tmpObj.cannonWidth / 2.5f), (tmpObj.cannonLength * 2.1f) * (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.get(1).mult == 0 ? 1 : tmpObj.animMults.get(1).mult) : 1), (tmpObj.cannonWidth / 1.25f), 0);
//     todo                   contxt.moveTo(0, 0);
                        render.line(0, 0, (tmpObj.cannonLength * 2.1f) * (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.get(1).mult == 0 ? 1 : tmpObj.animMults.get(1).mult) : 1), 0);
//      todo                  contxt.closePath();
//                        contxt.stroke();
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.8f));
                    }
                }

                // BIG CANNON:
                tmpMax = tmpObj.bigCannon;
                if (tmpMax != 0) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = (float) (((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2);
                    for (int c = 0; c < tmpObj.bigCannon; ++c) {
                        tmpVal = cY - ((tmpL / tmpMax) * c);
//                    todo    contxt.translate(0, tmpVal);
//                        contxt.rotate(tmpDir - (Math.PI / 2));
                        roundRect(-(tmpObj.cannonWidth / 2), 0, (tmpObj.cannonWidth / 2) * 2, (tmpObj.cannonLength * 3) * (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.get(1).mult == 0 ? 1 : tmpObj.animMults.get(1).mult) : 1), 1.2f);
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.2f));
                    }
                }

                // SNIPER CANNON:
                tmpMax = tmpObj.sniperCannon;
                if (tmpMax != 0) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = (float) (((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2);
                    for (int c = 0; c < tmpObj.sniperCannon; ++c) {
                        tmpVal = cY - ((tmpL / tmpMax) * c);
//                      Todo  contxt.translate(0, tmpVal);
//                        contxt.rotate(tmpDir - (Math.PI / 2));
                        roundRect(-(tmpObj.cannonWidth / 2.2f), 0, (tmpObj.cannonWidth / 2.2f) * 2, (tmpObj.cannonLength * 3.5f) * (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.get(1).mult == 0 ? 1 : tmpObj.animMults.get(1).mult) : 1) : 1), 0);
                        roundRect(-(tmpObj.cannonWidth / 2), 0, (tmpObj.cannonWidth / 2) * 2, (tmpObj.cannonLength * 2.5f) * (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.get(1).mult == 0 ? 1 : tmpObj.animMults.get(1).mult) : 1) : 1), 1.2f);
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.2f));
                    }
                }

                // SWIVEL CANNONS:
                tmpMax = tmpObj.swivelCannons;
                if (tmpMax != 0) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = (float) (((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2);
                    for (int c = 0; c < tmpObj.swivelCannons; ++c) {
                        tmpVal = cY - ((tmpL / tmpMax) * c);
//             todo           contxt.translate(0, tmpVal);
//                        contxt.rotate(tmpDir);
                        roundRect(0, -(tmpObj.cannonWidth / 2.4f), (tmpObj.cannonLength * 2) * (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.get(1).mult == 0 ? 1 : tmpObj.animMults.get(1).mult) : 1) : 1), tmpObj.cannonWidth / 1.2f, 0);
                        drawCircle(0, 0, Math.max((tmpObj.cannonWidth / 1.8f), 13));
                    }
                }

                // TWIN/QUAD CANNONS:
                tmpMax = (tmpObj.twinCannons == 0 ? tmpObj.quadCannons : tmpObj.twinCannons);
                if (tmpMax != 0) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    int tmpCount = 2;
                    float rotPlus = (float) Math.PI;
                    if (tmpObj.quadCannons != 0) {
                        tmpCount = 4;
                        rotPlus /= 2;
                    }
                    tmpDir = (float) (((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2);
                    for (int c = 0; c < tmpMax; ++c) {
                        tmpVal = cY - ((tmpL / tmpMax) * c);
//                   todo     contxt.translate(0, tmpVal);
//                        contxt.rotate(tmpDir);
                        for (int c2 = 0; c2 < tmpCount; ++c2) {
                            roundRect(0, -(tmpObj.cannonWidth / 2.4f), (tmpObj.cannonLength * 2.1f) * (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.get(1).mult == 0 ? 1 : tmpObj.animMults.get(1).mult) : 1) : 1), (tmpObj.cannonWidth / 1.25f), 0);
//                    todo        contxt.rotate(rotPlus);
                        }
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.4f));
                    }
                }

                // TRIPPLE CANNONS:
                tmpMax = tmpObj.trippleCannons;
                if (tmpMax != 0) {
                    float tmpS1 = tmpObj.cannonWidth / 1.3f;
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    tmpDir = (float) (((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + Math.PI / 2);
                    for (int c = 0; c < tmpMax; ++c) {
                        tmpVal = cY - ((tmpL / tmpMax) * c);
//        todo                contxt.translate(0, tmpVal);
//                        contxt.rotate(tmpDir);
                        roundRect(0, -tmpS1, (tmpObj.cannonLength * 2.1f) * (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.get(1).mult == 0 ? 1 : tmpObj.animMults.get(1).mult) : 1) : 1), (tmpS1 / 1.4f), 0);
                        roundRect(0, tmpS1 - (tmpS1 / 1.4f), (tmpObj.cannonLength * 2.1f) * (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.size() > 0 ? (tmpObj.animMults.get(1).mult == 0 ? 1 : tmpObj.animMults.get(1).mult) : 1) : 1), (tmpS1 / 1.4f), 0);
                        drawCircle(0, 0, tmpS1);
                    }
                }

                // AUTO CANNONS:
                tmpMax = tmpObj.autoCannons;
                if (tmpMax != 0) {
                    cY = (tmpMax - 1) * ((tmpL / tmpMax + 1) / 2);
                    for (int c = 0; c < tmpObj.autoCannons; ++c) {
                        tmpVal = cY - ((tmpL / tmpMax) * c);
                        tmpDir = (float) (tmpObj.aimDir - tmpObj.dir + Math.PI / 2);
//                     Todo   contxt.rotate(tmpDir);
//                        contxt.translate(0, tmpVal);
                        roundRect(0, -(tmpObj.cannonWidth / 2.6f), (tmpObj.cannonLength * 2) * ((tmpObj.animMults != null) ? tmpObj.animMults.get(1).mult : 1), tmpObj.cannonWidth / 1.3f, 0);
                        drawCircle(0, 0, (tmpObj.cannonWidth / 1.85f));
                    }
                }
            }
        }

        // EFFECT OVERLAY:
        if (tmpS.overlay != 0) {
            render.rect(player.x - player.w / 2, player.y - player.h / 2, player.w,
                    player.h);
        }
    }

    // GAME INPUT:
    public static void enterGame() {
        if (SocketManager.isConnected()) {
            showMainMenuText(randomLoadingTexts[Utility.randInt(0, randomLoadingTexts.length - 1)]);
            final JSONObject obj = new JSONObject();
            obj.put("name", userNameInput);
            obj.put("skin", skinIndex);
            SocketManager.socket.emit("respawn", obj);
            Utility.saveString("lstnmdbl", userNameInput);
        }
    }

    // LEAVE GAME TO MENU:
    public static void leaveGame() {
        gameState = 0;
        toggleGameUI(false);
        toggleMenuUI(true);
    }

    // DO UPGRADE:
    public static void doUpgrade(int index, int pos, int tp) {
        SocketManager.socket.emit("3", index, pos, tp);
    }

    public static void roundRect(float x, float y, float w, float h, float r) {
        final ShapeRenderer renderer = shapeRenderer(fill);

        //Todo renderer.setColor(Utility.tmpColor.set(color));
        renderer.rect(x + r, y, w - r - r, h);
        renderer.rect(x, y + r, r, h - r - r);
        renderer.rect(x + w - r, y + r, r, h - r - r);

        renderer.arc(x, y + h, r, 180, 90);
        renderer.arc(x + r, y + r, r, 90, 90);
        renderer.arc(x + w - r, y + h - r, r, 270, 90);
        renderer.arc(x + w - r, y + r, r, 0, 90);

    }

    public static int initSkins() {
        final Object[][] objects = {{
                "Default",
                0xeb6d6d,
                0x949494
        }, {
                "Purple",
                0xb96bed,
                0x949494
        }, {
                "Green",
                0x6FED6B,
                0x949494
        }, {
                "Orange",
                0xEDB76B,
                0x949494
        }, {
                "Black",
                0x696969,
                0x949494
        }, {
                "Navy",
                0xadadad,
                0x949494
        }, {
                "Ghostly",
                0x9BEB6D,
                0x949494,
                0.7f,
                0x00ff0049
        }, {
                "Glass",
                0x6DEBDE,
                0x949494,
                0.6f,
                0x0000ff49
        }, {
                "Pirate",
                0x5E5E5E,
                0x737373
        }, {
                "Sketch",
                0xE8E8E8,
                0xE8E8E8
        }, {
                "Gold",
                0xe9cd5f,
                0x949494
        }, {
                "Hazard",
                0x737373,
                0xe9cd5f
        }, {
                "Apples",
                0x91DB30,
                0xeb6d6d
        }, {
                "Beach",
                0xeac086,
                0xFFD57D
        }, {
                "Wood",
                0x8c5d20,
                0x949494
        }, {
                "Diamond",
                0x6FE8E2,
                0xEDEDED
        }, {
                "Midnight",
                0x5E5E5E,
                0xA763BA
        }, {
                "Valentine",
                0xFE4998,
                0xF9A7FE
        }, {
                "Cheddar",
                0xFCA403,
                0xFDFC08
        }, {
                "PewDie",
                0x09BBDF,
                0x1D3245
        }, {
                "Crimson",
                0x6B3333,
                0xAD3E3E
        }, {
                "Banana",
                0xfbf079,
                0xf9f9f9
        }, {
                "Cherry",
                0xF8E0F7,
                0xF5A9F2
        }, {
                "Moon",
                0x1C1C1C,
                0xF2F5A9
        }, {
                "Master",
                0xfce525,
                0xbb5e0e
        }, {
                "Reddit",
                0xfe562d,
                0xf9f9f9
        }, {
                "4Chan",
                0xffd3b4,
                0x3c8d2e
        }, {
                "Necron",
                0x808080,
                0x80ff80
        }, {
                "Ambient",
                0x626262,
                0x80ffff
        }, {
                "Uranium",
                0x5a9452,
                0x80ff80
        }, {
                "XPlode",
                0xfe4c00,
                0xf8bf00
        }, {
                "巧克力",
                0x804029,
                0xf9ebb4
        }};


        for (Object[] object : objects) {
            final Skin s = new Skin();
            s.name = String.valueOf(object[0]);
            s.color1 = (Integer) (object[1]);
            s.color2 = (Integer) (object[2]);
            if (object.length > 3)
                s.opacity = (Float) (object[3]);
            if (object.length > 4)
                s.overlay = (Integer) (object[4]);

        }
        return 0;
        //[0] = name, [1] = color1, [2] = color2;
    }

    public static void sendTarget(boolean force) {
        long tmpTime = currentTime;
        if (player != null && !player.dead) {
            target = (float) (Math.atan2(mouseY - (screenHeight / 2), mouseX - (screenWidth / 2)));
            if (force || tmpTime - lastUpdated > tUpdateFrequency) {
                if (controlIndex == 1) {
                    targetD = (float) Math.sqrt(Math.pow(mouseY - (screenHeight / 2), 2) + Math.pow(mouseX - screenWidth / 2, 2));
                    targetD *= Math.min(maxScreenWidth / screenWidth, maxScreenHeight / screenHeight);
                    targetD /= (maxScreenHeight / 3.5);
                    if (targetD > 1)
                        targetD = 1;
                    else if (targetD < 0.5)
                        targetD = 0.5f;
                }
                lastUpdated = tmpTime;
            }
            if (force || tmpTime - lastSent > sendFrequency) {
                lastSent = tmpTime;
                if (controlIndex == 1) {
                    SocketManager.socket.emit("1", target, targetD);
                } else {
                    SocketManager.socket.emit("1", target);
                }
            }
        }
    }

    public static void sendMoveTarget() {
        //Todo might have to do keys[]
        if (keys.r == 0 && keys.l == 0)
            turnDir = 0;
        if (keys.u == 0 && keys.d == 0)
            speedInc = 0;
        SocketManager.socket.emit("4", turnDir, speedInc);
    }

    public static void renderGameObject(Obstacle tmpObj) {

        //Todo tmpCanvas.width = (tmpObj.s * 2) + 10;
//        renderer.strokeStyle = darkColor;
//        renderer.lineWidth = 8.5;
        final ShapeRenderer renderer = shapeRenderer(fill);
        final Color color = Utility.tmpColor;
        if (tmpObj.c == 0) {
            color.set(0x797979);
        } else if (tmpObj.c == 1) {
            color.set(0xe89360);
        } else if (tmpObj.c == 2) {
            color.set(0xc8c8c8);
        } else if (tmpObj.c == 3) {
            color.set(0xe9cd5f);
        } else if (tmpObj.c == 4) {
            color.set(0xEB6565);
        } else if (tmpObj.c == 5) {
            color.set(0x6FE8E2);
        } else if (tmpObj.c == 6) {
            color.set(0x7BE86F);
        }

        renderer.setColor(color);
        if (tmpObj.shp == 1) {
            int spikes = 6;
            float rot = (float) Math.PI / 2 * 3;
            float rad = tmpObj.s / 2;
            float step = (float) Math.PI / spikes;

            float startX = tmpObj.x, startY = tmpObj.y - rad;
            for (int s = 0; s < spikes; s++) {
                renderer.line(startX, startY, tmpObj.x + (float) Math.cos(rot) * rad, tmpObj.y + (float) Math.sin(rot) * rad);
                rot += step;

                startX = tmpObj.x + (float) Math.cos(rot) * rad;
                startY = (float) Math.sin(rot) * rad;

                renderer.line(startX, startY, tmpObj.x + (float) Math.cos(rot) * (rad * 0.8f), tmpObj.y + (float) Math.sin(rot) * (rad * 0.8f));
                rot += step;

                startX = tmpObj.x + (float) Math.cos(rot) * (rad * 0.8f);
                startY = tmpObj.y + (float) Math.sin(rot) * (rad * 0.8f);
            }
            renderer.line(startX, startY, tmpObj.x, tmpObj.y - rad);
// Todo           renderer.closePath();
//            renderer.stroke();
//            renderer.fill();
        } else if (tmpObj.shp == 2) {
            float rad = tmpObj.s / 1.6f;

            renderer.line(tmpObj.x, tmpObj.y - rad, tmpObj.x + rad, tmpObj.y);
            renderer.line(tmpObj.x + rad, tmpObj.y, tmpObj.x, tmpObj.y + rad);
            renderer.line(tmpObj.x, tmpObj.y + rad, tmpObj.x - rad, tmpObj.y);
// Todo           renderer.closePath();
//            renderer.stroke();
//            renderer.fill();
        } else if (tmpObj.shp == 3) {
            float rad = tmpObj.s / 1.6f;

            renderer.line(tmpObj.x, tmpObj.y - rad, tmpObj.x + (rad / 1.5f), tmpObj.y);
            renderer.line(tmpObj.x + (rad / 1.5f), tmpObj.y, tmpObj.x, tmpObj.y + rad);
            renderer.line(tmpObj.x, tmpObj.y + rad, tmpObj.x - (rad / 1.5f), tmpObj.y);
//  todo          renderer.closePath();
//            renderer.stroke();
//            renderer.fill();
        } else {

            renderer.circle(tmpObj.x, tmpObj.y, tmpObj.s / 2);
        }
    }

    // UPDATE MENU:
    public static void updateMenuLoop(float delta) {
        if (gameState != 1) {

            // MENU INSTRUCTIONS TEXT:
            insturctionsCountdown -= delta;
            if (insturctionsCountdown <= 0) {
                insturctionsCountdown = instructionsSpeed;
                instructionsText = instructionsList[instructionsIndex];
                instructionsIndex++;
                if (instructionsIndex >= instructionsList.length)
                    instructionsIndex = 0;
            }
        }
    }

    public static void getIP() {
        //Todo
        if (true) {
            SocketManager.init("10.0.0.38", "5000");
            return;
        }
        try {
            final URL dob = new URL("http://doblons.io/getMobileIP?connectToServer");
            final URLConnection yc = dob.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            //Todo  String inputLine =  in.toString();
            String inputLine, ip = "", port = "";
            String[] inputLines;
            while ((inputLine = in.readLine()) != null) {
                inputLines = inputLine.replace("connectToServer({\"ip\":\"", "").split("\",\"port\":");
                ip = inputLines[0];
                port = inputLines[1].replace("})", "");
            }

            in.close();
            SocketManager.init(ip, port);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Utility.log("getIP IO exception: ");
            e.printStackTrace();
        }
    }

    @Override
    public void create() {
        super.create();
        HUDManager.getHUDManager();

        skinIndex = Utility.getInt("sknInx");
        userNameInput = Utility.getString("lstnmdbl");
        MyTextInputListener listener = new MyTextInputListener();
//        Gdx.input.getTextInput(listener, "Dialog Title", "Initial Textfield Value", "Hint Value");
        Gdx.input.setOnscreenKeyboardVisible(true);
        getIP();
    }

    @Override
    public void resume() {
        super.resume();
//        resumeSounds();
        paused = false;
    }

    @Override
    public void resize(int width, int height) {
        HUDManager.camera.setToOrtho(false, width, height);
        HUDManager.camera.position.set(width / 2, height / 2, 0);
        HUDManager.camera.update();
        super.resize(width, height);
        final EditText editText = new EditText();
        editText.w = screenWidth / 2;
        editText.h = screenHeight / 2;
        editText.x = screenWidth / 4;
        editText.y = screenHeight / 4;

        HUDManager.addView(editText);
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
        HUDManager.getHUDManager().draw(spriteBatch(), shapeRenderer(fill));
    }

    @Override
    public void pause() {
//        pauseSounds();
        paused = true;
        super.pause();
    }

    public void getStorage() {
        cid = Utility.getString("sckt");
        if (cid.length() < 2) {
//            cid = Utility.getUniqueID();
            Utility.saveString("sckt", cid);
        }
    }

    public static class MyTextInputListener implements Input.TextInputListener {
        @Override
        public void input(String text) {
            Utility.print("input: " + text);
        }

        @Override
        public void canceled() {

        }
    }
}
