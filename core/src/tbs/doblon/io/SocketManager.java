package tbs.doblon.io;


import com.badlogic.gdx.utils.StringBuilder;

import org.json.JSONObject;


import java.io.BufferedWriter;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


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

                        if (Game.lobbyURLIP) {
                            Game.kickPlayer("Connection failed. Please check your lobby ID");
                        } else {
                            Game.kickPlayer("Connection failed. Check your internet and firewall settings");
                        }
                    }
                }
            ).on("disconnect",new Emitter.Listener()

            {

                @Override
                public void call (Object...args){
                Game.kickPlayer("Disconnected.> reason");
            }}).on("error", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        kickPlayer("Disconnected. The server may have updated. ["+err+"]");
                    }
                }
                    ).on("kick",new Emitter.Listener()

                    {

                        @Override
                        public void call (Object...args){
                        Game.kickPlayer(reason);
                    }}).on("lk", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Game.partyKey = lKey;
            }

        } ).on("mds",new Emitter.Listener(){

                                @Override
                                public void call (Object...args) {
                                    Game.modeList = data;
                                    // Todo modeSelector.innerHTML = data[crnt].name + "  <i style="vertical-align: middle;" class="material-icons">&#xE5C5;</i>";
                                    Game.modeIndex = crnt;
                                    Game.currentMode = Game.modeList[crnt];
                                } }).on("v", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                //Todo sw, sh, mult
                if (Game.viewMult != Game.mult) {
                    Game.viewMult = Game.mult;
                    Game.maxScreenWidth = Math.round(Game.screenWidth * Game.mult);
                    Game.maxScreenHeight = Math.round(Game.screenHeight * Game.mult);
                    Game.resize();
                }
            }

        }).on("spawn",new Emitter.Listener(){
                                        @Override
                                        public void call (Object...args) {
                                            //Todo gameObject, data

                                            if (!Game.gameObjects.c(gameObject)) {
                                                Game.users.add(gameObject);
                                            } else {
                                                Game.updateOrPushUser(gameObject);
                                            }
                                            Game.player = gameObject;
                                            Game.gameState = 1;
                                            Game.toggleMenuUI(false);
                                            Game.toggleGameUI(true);
                                            if (data) {
                                                gameData = data;
                                                Game.gameObjects.clear();
                                                for (var i = 0; i < data.objCount; ++i) {
                                                    gameObjects.push({});
                                                }
                                                data = null;
                                            }
                                            Game.targetD = 1;
                                            Game.resetKeys();
                                            Game.gameObject = null;
                                        }}).on("d", new Emitter.Listener() {

                                            @Override
                                            public void call(Object... args) {
                                                //todo id
                                                int tmpIndx = getPlayerIndexById(id);
                                            }

                                            if(Game.tmpIndx!=null){
                                                Game.users.remove(tmpIndx);
                                            }
                                        }).on("dnt", new Emitter.Listener() {

                                            @Override
                                            public void call(Object... args) {
                                                //todo timeStr, dnt

                                                // timeDisplay.innerHTML = timeStr;
                                                Game.dayTimeValue = dnt;
                                            }
                                            }

                                            ).on("0",new Emitter.Listener()

        {

            @Override
            public void call(Object... args) {
                //Todo list
                int rank = 1;
                int pos = -1;

                for (int i = 0; i < list.length; ) {
                    if (Game.player && list[i] == Game.player.sid)
                        pos = rank;
                    i += 4;
                    rank++;
                }

                Game.leaderboardText = "pos " + (pos < 0 ? "10+" : pos) + " of " + rank;
            }

        }).on("1",new Emitter.Listener() {

                                                @Override
                                                public void call (Object...args){

                                            }
                                            }

                                            ).on("2",
                                                    new Emitter.Listener() {

                                                @Override
                                                public void call (Object...args){
                                                //todo  upgrC, fleetC, data, points
                                                if (data) {
                                                    StringBuilder tmpHTML = new StringBuilder();
                                                    Game.upgradesText = "~ Upgrades " + upgrC + " ~ Fleet " + fleetC;
                                                    int num = 1;
                                                    for (int i = 0; i < data.length; ) {
                                                        float tmpW = ((data[i + 2] - 1) / (data[i + 3] - 1)) * (39 / 65 * Game.screenWidth);
                                                        if (num == 9){
                                                            //Todo add a line seperator
                                                        }
                                                        //Todo doUpgrade(" + (num - 1) + ", 0, 1)
                                                       tmpHTML.app
                                                                (data[i]) + " <span class="
                                                        greyMenuText
                                                        ">" + ((data[i + 2] != data[i + 3]) ? ("$" + data[i + 1]) : "max")
                                                                + "</span></div></br>";
                                                        i += 4;
                                                        num++;
                                                    }
                                                    upgradeList.innerHTML = tmpHTML;
                                                    if (!Game.upgradesHidden)
                                                        upgradeContainer.style.display = "inline-block";
                                                }
                                                if (Game.points != undefined)
                                                    coinDisplay.innerHTML = "Coins <span class="
                                                greyMenuText ">$" + (points || 0) + "</span>";
                                            }).on("8", new Emitter.Listener() {
                                                            @Override
                                                            public void call(Object... args) {
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
                                                                        var tmpHTML = "";
                                                                        var tmpHTML2 = "";
                                                                        var num = 0;
                                                                        var bot = 32;
                                                                        for (var i = 0; i < data.length; ) {
                                                                            tmpHTML += "<div onclick="
                                                                            showWeaponPopup(" + num + ")
                                                                            " class=" weaponItem
                                                                            "><div class="
                                                                            upgradeTxt
                                                                            ">" + data[i] + "</div><div class="
                                                                            upgradeNum ">Tier " +
                                                                                    data[i + 1] + "</div></div>";
                                                                            tmpHTML2 += "<div id="
                                                                            popupRow " + num + "
                                                                            " class=" weaponPopupRow
                                                                            ">";
                                                                            for (var x = 0; x < data[i + 2].length; ++x) {
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
                                                                data = null;
                                                                points = null;
                                                                prog = null;
                                                            }

                                                            ).on("3",new Emitter.Listener()

                                                            {

                                                                @Override
                                                                public void call (Object...args){
                                                                //todo sid, values

                                                                var tmpIndx = getPlayerIndex(sid);
                                                                if (tmpIndx != null) {
                                                                    var tmpUser = users[tmpIndx];
                                                                    for (var i = 0; i < values.length; ) {
                                                                        tmpUser[values[i]] = values[i + 1];
                                                                        i += 2;
                                                                    }
                                                                }
                                                            }).on("4", new Emitter.Listener() {

                                                                    @Override
                                                                    public void call(Object... args) {
                                                                        //todo indx, data

                                                                        tmpObj = gameObjects[indx];
                                                                        if (tmpObj) {
                                                                            if (data) {
                                                                                tmpObj.x = data[0];
                                                                                tmpObj.xS = data[1];
                                                                                tmpObj.y = data[2];
                                                                                tmpObj.yS = data[3];
                                                                                tmpObj.s = data[4];
                                                                                tmpObj.c = data[5];
                                                                                tmpObj.shp = data[6];
                                                                                tmpObj.active = true;
                                                                            } else {
                                                                                tmpObj.active = false;
                                                                            }
                                                                        }
                                                                    }

                                                                    ).on("5",
                                                                            new Emitter.Listener()

                                                                    {

                                                                        @Override
                                                                        public void call (Object...
                                                                        args){
                                                                        //Todo sid, wpnIndex
                                                                        int tmpIndx = Game.getPlayerIndex(sid);
                                                                        if (tmpIndx != null) {
                                                                            final Player tmpUser = Game.users.get(tmpIndx);
                                                                            if (!tmpUser.animMults)
                                                                                tmpUser.animMults =[
                                                                            {
                                                                                mult:
                                                                                1
                                                                            },{
                                                                                mult:
                                                                                1
                                                                            },{
                                                                                mult:
                                                                                1
                                                                            },{
                                                                                mult:
                                                                                1
                                                                            }];
                                                                            tmpUser.animMults[wpnIndex].plus = -0.03;
                                                                        }
                                                                    }).on("6", new Emitter.Listener() {

                                                                            @Override
                                                                            public void call(Object... args) {
                                                                                //Todo  sid, value
                                                                                var tmpIndx = getPlayerIndex(sid);
                                                                                if (tmpIndx != null) {
                                                                                    var tmpUser = users[tmpIndx];
                                                                                    tmpUser.health = value;
                                                                                    if (tmpUser.health <= 0) {
                                                                                        tmpUser.dead = true;
                                                                                        if (player && sid == player.sid) {
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
                                                                            ).on("7",new Emitter.Listener()

                                                                            {

                                                                                @Override
                                                                                public void call
                                                                                (Object...args){
                                                                                //Todo value
                                                                                scoreText.innerHTML = value;
                                                                            }
                                                                            }

                                                                            ).on("n",new Emitter.Listener(){

                                                                                @Override
                                                                                public void call
                                                                                (Object...args){
                                                                                //todo txt
                                                                                showNotification(txt);
                                                                            }).on("s", new Emitter.Listener() {

                                                                                    @Override
                                                                                    public void call(Object... args) {
                                                                                        //todo val
                                                                                        showScoreNotif(val);
                                                                                    }

                                                                                    ).on("m",new Emitter.Listener()

                                                                                    {

                                                                                        @Override
                                                                                        public void call
                                                                                        (Object...
                                                                                        args){
                                                                                        //Todo data
                                                                                        minimap.width = minimap.width;

                                                                                        for (var i = 0; i < data.length; ) {
                                                                                            minimapContext.fillStyle = Game.pingColors[data[i]];
                                                                                            minimapContext.font = "10px regularF";
                                                                                            minimapContext.beginPath();
                                                                                            minimapContext.arc(((data[i + 1] + gameData.mapScale) / (gameData.mapScale * 2)) * minimap.width,
                                                                                                    ((data[i + 2] + gameData.mapScale) / (gameData.mapScale * 2)) * minimap.height, data[i + 3] * 2, 0, MathPI * 2, true);
                                                                                            minimapContext.closePath();
                                                                                            minimapContext.fill();
                                                                                            i += 4;
                                                                                        }
                                                                                    });

                                                                                        // LOAD DATA:
                                                                                        // setControlSheme(controlIndex);
                                                                                        Utility.saveInt("contrlSt", 1);
                                                                                        socket.emit("6", "cont", 1);
                                                                                        controlIndex = 1;
                                                                                        socket.connect();
                                                                                    }
                                                                                });
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
                                                                                for (var i = 0; i < users.length; ++i) {
                                                                                    if (!users[i].visible)
                                                                                        users[i].forcePos = 1;
                                                                                    users[i].visible = false;
                                                                                }
                                                                                var tmpIndx;
                                                                                for (var i = 0; i < listObj.length; ) {
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
