package tbs.doblon.io;


import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.StringBuilder;

import org.json.JSONObject;


import java.io.BufferedWriter;
import java.lang.reflect.Array;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static tbs.doblon.io.Game.gameData;
import static tbs.doblon.io.Game.gameObjects;
import static tbs.doblon.io.Game.hideMainMenuText;
import static tbs.doblon.io.Game.leaveGame;
import static tbs.doblon.io.Game.minimap;
import static tbs.doblon.io.Game.player;
import static tbs.doblon.io.Game.showAd;
import static tbs.doblon.io.Game.users;


/**
 * Created by mike on 5/29/16.
 */
public class SocketManager {
    //    JSONObject obj = new JSONObject();
//    obj.put("hello", "server");
//    obj.put("binary", new byte[42]);
//    socket.emit("foo", obj);

    static Socket socket;

    static boolean isConnected() {
        if (socket == null)
            return false;
        else return socket.connected();
    }

    static int numReconnect = 0;

    public static void init() {
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.timeout = 2500;
        opts.reconnection = false;
        opts.query = "rmid=" + Game.lobbyRoomID + "&apid=19ytahhsb";
        try {
//            socket = IO.socket(data.ip + ":+ + data.port, opts);
            socket = IO.socket("http://10.0.0.38:5000", opts);
        } catch (Exception e) {

        }

        socket.on("mds", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Utility.log("connected");
                String a = "";
                for (Object arg : args) {
                    a += String.valueOf(arg) + " , ";
                }
                Utility.log(String.valueOf(a));
            }

        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                numReconnect = 0;
                socket.emit("foo", "hi");
                socket.disconnect();
            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                numReconnect++;
                if (numReconnect < 15)
                    socket.connect();
            }

        }).on("connect_error", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        numReconnect++;
                        if (numReconnect < 15)
                            socket.connect();

                        if (Game.lobbyURLIP != null) {
                            Game.kickPlayer("Connection failed. Please check your lobby ID");
                        } else {
                            Game.kickPlayer("Connection failed. Check your internet and firewall settings");
                        }
                    }
                }
        ).on("disconnect", new Emitter.Listener()

        {

            @Override
            public void call(Object... args) {
                Game.kickPlayer("Disconnected.> reason");
            }
        }).on("error", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Game.kickPlayer("Disconnected. The server may have updated. [" + String.valueOf(args[0]) + "]");
                    }
                }
        ).on("kick", new Emitter.Listener()

        {

            @Override
            public void call(Object... args) {

                Game.kickPlayer(String.valueOf(args[0]));
            }
        }).on("lk", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Game.partyKey = String.valueOf(args[0]);
            }

        }).on("mds", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Game.modeList = String.valueOf(args[0]);
                // Todo modeSelector.innerHTML = data[crnt].name + "  <i style="vertical-align: middle;" class="material-icons">&#xE5C5;</i>";
                Game.modeIndex = String.valueOf(args[1]);
                Game.currentMode = Game.modeList.get(Integer.parseInt(String.valueOf(args[1])));
            }
        }).on("v", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                //Todo sw, sh, mult
                Utility.log(".on(v)> " + Arrays.toString(args));
//                if (Game.viewMult != Game.mult) {
//                    Game.viewMult = Game.mult;
//                    Game.maxScreenWidth = Math.round(Game.screenWidth * Game.mult);
//                    Game.maxScreenHeight = Math.round(Game.screenHeight * Game.mult);
//                    Game.resize();
//                }
            }

        }).on("spawn", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //Todo gameObject, data
                final Player tmpPlayer = new Player(String.valueOf(args[0]));
                if (!Game.objectExists(tmpPlayer)) {
                    users.add(tmpPlayer);
                } else {
                    Game.updateOrPushUser(tmpPlayer);
                }
                player = tmpPlayer;
                Game.gameState = 1;
                Game.toggleMenuUI(false);
                Game.toggleGameUI(true);
                String data = String.valueOf(args[0]);
                if (data != null && data.length() > 0) {
                    if (gameData == null)
                        gameData = new GameData(data);
                    else
                        gameData.parse(data);
                    gameObjects.clear();
//                    for (int i = 0; i < data.objCount; ++i) {
//                        gameObjects.push({});
//                    }
//                    data = null;
                }
                Game.targetD = 1;
                Game.resetKeys();
            }
        }).on("d", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                //todo id
                final Player tmpIndx = Game.getPlayerIndexById(String.valueOf(args[0]));

                if (tmpIndx != null) {
                    users.remove(tmpIndx);
                }
            }
        }).on("dnt", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        //todo timeStr, dnt

                        // timeDisplay.innerHTML = timeStr;
                        Game.dayTimeValue = Float.parseFloat(String.valueOf(args[0]));
                    }
                }

        ).on("0", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                //Todo 0
                // :
                // 2
                // 1
                // :
                // "ksasdfssa0"
                // 2
                // :
                // 10
                // 3
                // :
                // 0
                // String[]
                //Todo list
                int rank = 1;
                int pos = -1;
                final Object[] list = (Object[]) args[0];
                for (int i = 0; i < list.length; ) {
                    if ((((Integer) list[i]) == player.sid))
                        pos = rank;
                    i += 4;
                    rank++;
                }

                Game.leaderboardText = "pos " + (pos < 0 ? "10+" : pos) + " of " + rank;
            }

        }).on("1", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {

                    }
                }
        ).on("2", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                //todo  upgrC, fleetC, data, points
                final Object[] data = (Object[]) (args[2]);
                if (data!=null){
                    if (Game.upgradeItems.size()<(data.length/4))
                        for (int i=0;i< (data.length/4)-Game.upgradeItems.size();i++ )
                            Game.upgradeItems.add(new UpgradeItem());


                    Game.upgradesText = "~ Upgrades " + String.valueOf(args[0]) + " ~ Fleet " + String.valueOf(args[1]);
                    int num = 1;
                    for (int i = 0; i < data.length; ) {

                        final UpgradeItem upgradeItem = Game.upgradeItems.get(num-1);

                        upgradeItem.name = String.valueOf(data[i]);
                        upgradeItem.price = (Integer) (data[i+1]);
                        upgradeItem.progress = (Integer) (data[i+2]);
                        upgradeItem.maxProgress = (Integer) (data[i+3]);

                        //Todo doUpgrade(" + (num - 1) + ", 0, 1)

                        i += 4;
                        num++;
                    }
                }
                Game.coinDisplayText = "Coins: $" + args[3];
            }}

            ).on("8",new Emitter.Listener() {
                @Override
                public void call (Object...args){
                    //todo data, points, prog

                    if (!data && points == undefined && prog == undefined) {
                        weaponsProgress.style.display = "none";
                        weaponsList.style.display = "none";
                        weaponsPopups.style.display = "none";
                        upgradesInfo.style.display = "none";
                    } else {
                        if (!points) {
                            weaponsProgress.style.display = "inline-block";
                            weaponsList.style.display = "none";
                            weaponsPopups.style.display = "none";
                            upgradesInfo.style.display = "none";
                        } else if (data) {
                            weaponsProgress.style.display = "none";
                            weaponsList.style.display = "block";
                            weaponsPopups.style.display = "block";

                            // WEAPONS LIST:
                            String tmpHTML = "";
                            int tmpHTML2 = "";
                            int num = 0;
                            int bot = 32;
                            for (int i = 0; i < data.length; ) {
                                tmpHTML += "<div onclick="
                                showWeaponPopup(" + num + ")
                                " cass=" weaponItem
                                "><div class="
                                upgradeTxt
                                ">" + data[i] + "</div><div class="
                                upgradeNum ">Tier " +
                                        data[i + 1] + "</div></div>";
                                tmpHTML2 += "<div id="
                                popupRow " + num + "
                                " class=" weaponPopupRow
                                ">";
                                for (int x = 0; x < data[i + 2].length; ++x) {
                                    tmpHTML2 += "<div onclick="
                                    doUpgrade(" + x + ", " + num + ")
                                    " class="
                                    weaponPopupItem
                                    " style=" bottom:
                                    " + (bot * x / 3) + "
                                    vh
                                    ">" + data[i + 2][x] + "</div>";
                                }
                                tmpHTML2 += "</div>";
                                i += 3;
                                num++;
                            }
                            weaponsList.innerHTML = tmpHTML;
                            weaponsPopups.innerHTML = tmpHTML2;
                        }
                        if (prog != undefined) {
                            wpnsProgressBar.style.width = weaponsProgress.clientWidth * prog + "px";
                            wpnsProgressTxt.innerHTML = Math.round(prog * 100) + "%";
                        }
                        if (points) {
                            upgradesInfo.style.display = "inline-block";
                            upgradesInfo.innerHTML = "Items (" + points + ")";
                        }
                    }
                }}

                ).on("3", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        //todo sid, values

                        final int tmpIndx = Game.getPlayerIndex((Integer)(args[0]));
                        final Object[] values = (Object[]) args[1];
                        if (tmpIndx >=0) {
                            final Player tmpUser = users.get(tmpIndx);
                            if (tmpUser!=null)
                            for (int i = 0; i < values.length; ) {

                            final String key = String.valueOf(values[i]);
                                if (key.equals("lvl")){
                                tmpUser.lvl =  (Integer) (values[i + 1]);

                                }else if (key.equals("maxHealth")){
                                    tmpUser.maxHealth =  (Integer) (values[i + 1]);

                                }else if (key.equals("health")){
                                    tmpUser.health =  (Integer) (values[i + 1]);
                                }else if (key.equals("speedDiv")){
                                tmpUser.speedDiv =  (Float) (values[i + 1]);
                                }else if (key.equals("width")){
                                tmpUser.w =  (Float) (values[i + 1]);
                                }else if (key.equals("healthRegen")){
tmpUser.healthRegen =  (Float) (values[i + 1]);
                                }else if (key.equals("cannonSpeed")){
tmpUser.cannonSpeed =  (Float) (values[i + 1]);
                                }else if (key.equals("cannonWidth")){
    tmpUser.cannonWidth= (Float) (values[i + 1]);
                                }else if (key.equals("cannonLength")){
tmpUser.cannonLength =  (Float) (values[i + 1]);
                                }else if (key.equals("cannonDmg")){
tmpUser.cannonDmg =  (Float) (values[i + 1]);
                                }else if (key.equals("length")){
tmpUser.length =  (Float) (values[i + 1]);
                                }else if (key.equals("reloadDiv")){
tmpUser.reloadDiv =  (Float) (values[i + 1]);
                                }else if (key.equals("speed")){
tmpUser.speed =  (Float) (values[i + 1]);
                                }else if (key.equals("turnSpeed")){
tmpUser.turnSpeed =  (Float) (values[i + 1]);
                                }else if (key.equals("crashDamage")){
tmpUser.crashDamage = (Float) (values[i + 1]);
                                }
                                i += 2;
                            }
                        }
                        }
                    }
                    ).

                    on("4",new Emitter.Listener() {
                        @Override
                        public void call (Object...args){
                            //todo indx, data

                          final Obstacle tmpObj =  gameObjects.get((Integer)args[0]);
                            final Object[] data = (Object[])args[1];
                            if (tmpObj!=null) {
                                if (data!=null) {
                                    tmpObj.x = (Integer) data[0];
                                    tmpObj.xS = (Integer) data[1];
                                    tmpObj.y = (Integer) data[2];
                                    tmpObj.yS = (Integer) data[3];
                                    tmpObj.s = (Integer) data[4];
                                    tmpObj.c = (Integer) data[5];
                                    tmpObj.shp = (Integer) data[6];
                                    tmpObj.active = true;
                                } else {
                                    tmpObj.active = false;
                                }
                            }
                        }
                        }
                        ).on("5", new Emitter.Listener() {

                                    @Override
                                    public void call(Object...
                                                             args) {
                                        //Todo sid, wpnIndex
                                        int tmpIndx = Game.getPlayerIndex((Integer) args[0]);
                                        int wpnIndex = Game.getPlayerIndex((Integer) args[1]);
                                        if (tmpIndx >=0) {
                                            final Player tmpUser = users.get(tmpIndx);
                                            for (int i =0;i<4;i++)
                                                tmpUser.animMults.get(i).mult = 1;

                                            tmpUser.animMults.get(wpnIndex).plus = -0.03f;
                                        }
                                    }
                                    }

                                    ).

                                    on("6",new Emitter.Listener() {

                                        @Override
                                        public void call (Object...args){
                                            //Todo  sid, value
                                            final int sid = (Integer) args[0];
                                            int tmpIndx = Game.getPlayerIndex(sid);
                                            int value = (Integer) args[1];
                                            if (tmpIndx >=0) {
                                                final Player tmpUser = users.get(tmpIndx);
                                                tmpUser.health = value;
                                                if (tmpUser.health <= 0) {
                                                    tmpUser.dead = true;
                                                    if (player!=null && (sid) == player.sid) {
                                                        player.dead = (value <= 0);
                                                        if (player.dead) {
                                                            hideMainMenuText();
                                                            leaveGame();
                                                            showAd();
                                                        }
                                                    }
                                                }
                                            }
                                            }
                                        }
                                        ).on("7", new Emitter.Listener() {

                                                    @Override
                                                    public void call
                                                            (Object... args) {
                                                        //Todo value
                                                        Game.scoreText = String.valueOf(args[0]);
                                                    }
                                                }

                                        ).on("n", new Emitter.Listener() {

                                            @Override
                                            public void call
                                                    (Object... args) {
                                                //todo txt
                                                Game.showNotification(String.valueOf(args[0]));
                                            }
                                            }

                                            ).

                                            on("s",new Emitter.Listener() {

                                                @Override
                                                public void call (Object...args){
                                                    //todo val
                                                    Game.showScoreNotif((Integer)(args[0]));
                                                }}

                                                ).on("m", new Emitter.Listener()

                                                {

                                                    @Override
                                                    public void call
                                                            (Object...
                                                                     args) {
                                                        //Todo data
                                                        final Object[] data = (Object[]) args[0];
                                                        for (int i = 0; i < data.length; ) {
                                                            minimapContext.fillStyle = Game.pingColors((Integer)data[i]);
                                                            minimapContext.font = "10px regularF";
                                                            minimapContext.beginPath();
                                                            minimapContext.arc(((data[i + 1] + gameData.mapScale) / (gameData.mapScale * 2)) * minimap.width,
                                                                    ((data[i + 2] + gameData.mapScale) / (gameData.mapScale * 2)) * minimap.height, ((Float)data[i + 3]) * 2, 0, M * 2, true);
                                                            minimapContext.closePath();
                                                            minimapContext.fill();
                                                            i += 4;
                                                        }
                                                    }

                                                    );

                                                    // LOAD DATA:
                                                    // setControlSheme(controlIndex);
                                                    Utility.saveInt("contrlSt",1);
                                                    socket.emit("6","cont",1);
                                                    controlIndex=1;
                                                    socket.connect();
                                                }
                                            }
                                            }

                                            );
                                        }

                                    public void send() {
                                        // Sending an object
                                        JSONObject obj = new JSONObject();
                                        obj.put("hello", "server");
                                        obj.put("binary", new byte[42]);
                                        socket.emit("foo", obj);


                                    }

                                    public void receive() {
                                        // Receiving an object
                                        socket.on("foo", new Emitter.Listener() {
                                            @Override
                                            public void call(Object... args) {
                                                JSONObject obj = (JSONObject) args[0];
                                                String k = obj.getString("k");
                                            }
                                        });
                                    }

                                    // GET USER DATA:
                                    public void updateUserData(singleObj, listObj) {
                                        if (singleObj) {
                                            singleObj.visible = true;
                                            updateOrPushUser(singleObj);
                                            delete singleObj;
                                        } else if (listObj) {
                                            for (int i = 0; i < users.length; ++i) {
                                                if (!users[i].visible)
                                                    users[i].forcePos = 1;
                                                users[i].visible = false;
                                            }
                                            var tmpIndx;
                                            for (int i = 0; i < listObj.length; ) {
                                                tmpIndx = getPlayerIndex(listObj[i]);
                                                if (tmpIndx != null) {
                                                    users[tmpIndx].x = listObj[i + 1];
                                                    users[tmpIndx].y = listObj[i + 2];
                                                    users[tmpIndx].dir = listObj[i + 3];
                                                    users[tmpIndx].targetDir = listObj[i + 4];
                                                    users[tmpIndx].aimDir = listObj[i + 5];
                                                    users[tmpIndx].visible = true;
                                                }
                                                i += 6;
                                            }
                                        }
                                    }
