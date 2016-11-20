package tbs.doblon.io;


import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.StringBuilder;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedWriter;
import java.lang.reflect.Array;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static tbs.doblon.io.Game.coinDisplayText;
import static tbs.doblon.io.Game.controlIndex;
import static tbs.doblon.io.Game.gameData;
import static tbs.doblon.io.Game.gameObjects;
import static tbs.doblon.io.Game.gameState;
import static tbs.doblon.io.Game.hideMainMenuText;
import static tbs.doblon.io.Game.leaveGame;
import static tbs.doblon.io.Game.minimap;
import static tbs.doblon.io.Game.player;
import static tbs.doblon.io.Game.showAd;
import static tbs.doblon.io.Game.upgradeItems;
import static tbs.doblon.io.Game.users;
import static tbs.doblon.io.GameBase.fill;
import static tbs.doblon.io.MiniMap.miniMapItems;


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

    public static void init(final String ip, String port) {
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.timeout = 2500;
        opts.reconnection = false;
        opts.query = "rmid=" + Game.lobbyRoomID + "&apid=19ytahhsb";
        try {
//            socket = IO.socket(data.ip + ":+ + data.port, opts);
            socket = IO.socket("http://" + ip + ":" + port, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Utility.print("socket.on: connected");
                numReconnect = 0;
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Utility.print("socket.on: disconnected");
                numReconnect++;
                if (numReconnect < 15)
                    socket.connect();
            }
        }).on("connect_error", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Utility.print("socket.on: connection_error");
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
        ).on("disconnect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Utility.print("socket.on: disconnect");
                Game.kickPlayer("Disconnected.> reason");
            }
        }).on("error", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Game.kickPlayer("Disconnected. The server may have updated. [" + String.valueOf(args[0]) + "]");
                        Utility.print("socket.on: error");
                    }
                }
        ).on("kick", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Utility.print("socket.on: kick");
                Game.kickPlayer(String.valueOf(args[0]));
            }
        }).on("lk", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
//           wokring     Utility.print("socket.on: lk");
                Game.partyKey = String.valueOf(args[0]);
            }
        }).on("mds", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
//             working   Utility.print("socket.on: mds");
                Game.modeList.clear();
                JSONArray a = new JSONArray();
                final JSONArray modes = (JSONArray) args[0];
                for (int i = 0; i < modes.length(); i++)
                    Game.modeList.add(String.valueOf(modes.get(i)));
                // Todo modeSelector.innerHTML = data[crnt].name + "  <i style="vertical-align: middle;" class="material-icons">&#xE5C5;</i>";
                Game.modeIndex = String.valueOf(args[1]);
                Game.currentMode = Game.modeList.get(Integer.parseInt(String.valueOf(args[1])));
            }
        }).on("v", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //Todo sw, sh, mult
//          working      Utility.log(".on(v)> " + Arrays.toString(args));
                float sw = Float.parseFloat(String.valueOf(args[0])), sh = Float.parseFloat(String.valueOf(args[1])), mult = Float.parseFloat(String.valueOf(args[2]));
                if (Game.viewMult != mult) {
                    Game.viewMult = mult;
                    Game.maxScreenWidth = Math.round(sw * mult);
                    Game.maxScreenHeight = Math.round(sh * mult);
                }
            }
        }).on("spawn", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
//      working          Utility.print("socket.on: spawn");
                //Todo gameObject, data
                final JSONObject tmpPlayer = new JSONObject(String.valueOf(args[0]));
                Player foundPlayer = Game.getPlayerIndexById(tmpPlayer.getString("id"));
                if (foundPlayer == null) {
                    foundPlayer = new Player(tmpPlayer);

                    users.add(foundPlayer);
                } else {
                    Game.updateOrPushUser(tmpPlayer);
                }
                player = foundPlayer;
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
                Utility.print("socket.on: d");
                final Player tmpIndx = Game.getPlayerIndexById(String.valueOf(args[0]));

                if (tmpIndx != null) {
                    users.remove(tmpIndx);
                }
            }
        }).on("dnt", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
//                   working     Utility.print("socket.on: dnt");
                        //todo timeStr, dnt
                        // timeDisplay.innerHTML = timeStr;
                        Game.dayTimeValue = String.valueOf(args[0]);
                        Game.enterGame();
                    }
                }
        ).on("0", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
//         working       Utility.print("socket.on: 0");
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
                final JSONArray list = (JSONArray) args[0];
//      working          Utility.print(list.toString());
                for (int i = 0; i < list.length(); ) {
                    if (player != null && (((Integer) list.get(i)) == player.sid))
                        pos = rank;
                    i += 4;
                    rank++;
                }
                Game.leaderboardText = "pos " + (pos < 0 ? "10+" : pos) + " of " + rank;
            }
        }).on("1", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
//                        Utility.print("socket.on: 1");
                        if (args.length > 1)
                            updateUserData(args[0], (JSONArray) args[1]);
                        else
                            updateUserData(args[0], null);
                    }
                }
        ).on("2", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
//                  working      Utility.print("socket.on: 2");
                        //todo  upgrC, fleetC, data, points
                        final JSONArray data = (JSONArray) (args[2]);
                        if (data != null) {
                            final int numItems = (data.length() / 4);
                            final int diff = upgradeItems.size() - numItems;

                            if (diff > 0)
                                for (int i = 0; i < diff; i++)
                                    upgradeItems.add(new UpgradeItem());
                            else if (diff < 0)
                                for (int i = 0; i < Math.abs(diff); i++)
                                    try {
                                        upgradeItems.remove(upgradeItems.size() - 1);
                                    } catch (Exception e) {
                                    }

                            Game.upgradesText = "~ Upgrades " + String.valueOf(args[0]) + " ~ Fleet " + String.valueOf(args[1]);
                            int num = 0;
                            for (int i = 0; num < upgradeItems.size(); ) {

                                final UpgradeItem upgradeItem = Game.upgradeItems.get(num);

                                upgradeItem.name = String.valueOf(data.get(i));
                                upgradeItem.price = (Integer) (data.get(i + 1));
                                upgradeItem.progress = (Integer) (data.get(i + 2));
                                upgradeItem.maxProgress = (Integer) (data.get(i + 3));

                                //Todo doUpgrade(" + (num - 1) + ", 0, 1)

                                i += 4;
                                num++;
                            }
                        }
                        Game.coinDisplayText = "Coins: $" + args[3];
                    }
                }
        ).on("8", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
//           todo             Utility.print("socket.on: 8");
                        //todo data, points, prog
//                        if (!data && points == undefined && prog == undefined) {
//                            weaponsProgress.style.display = "none";
//                            weaponsList.style.display = "none";
//                            weaponsPopups.style.display = "none";
//                            upgradesInfo.style.display = "none";
//                        } else {
//                            if (!points) {
//                                weaponsProgress.style.display = "inline-block";
//                                weaponsList.style.display = "none";
//                                weaponsPopups.style.display = "none";
//                                upgradesInfo.style.display = "none";
//                            } else if (data) {
//                                weaponsProgress.style.display = "none";
//                                weaponsList.style.display = "block";
//                                weaponsPopups.style.display = "block";
//
//                                // WEAPONS LIST:
//                                String tmpHTML = "";
//                                int tmpHTML2 = "";
//                                int num = 0;
//                                int bot = 32;
//                                for (int i = 0; i < data.length; ) {
//                                    tmpHTML += "<div onclick="
//                                    showWeaponPopup(" + num + ")
//                                    " cass=" weaponItem
//                                    "><div class="
//                                    upgradeTxt
//                                    ">" + data[i] + "</div><div class="
//                                    upgradeNum ">Tier " +
//                                            data[i + 1] + "</div></div>";
//                                    tmpHTML2 += "<div id="
//                                    popupRow " + num + "
//                                    " class=" weaponPopupRow
//                                    ">";
//                                    for (int x = 0; x < data[i + 2].length; ++x) {
//                                        tmpHTML2 += "<div onclick="
//                                        doUpgrade(" + x + ", " + num + ")
//                                        " class="
//                                        weaponPopupItem
//                                        " style=" bottom:
//                                        " + (bot * x / 3) + "
//                                        vh
//                                        ">" + data[i + 2][x] + "</div>";
//                                    }
//                                    tmpHTML2 += "</div>";
//                                    i += 3;
//                                    num++;
//                                }
//                                weaponsList.innerHTML = tmpHTML;
//                                weaponsPopups.innerHTML = tmpHTML2;
//                            }
//                            if (prog != undefined) {
//                                wpnsProgressBar.style.width = weaponsProgress.clientWidth * prog + "px";
//                                wpnsProgressTxt.innerHTML = Math.round(prog * 100) + "%";
//                            }
//                            if (points) {
//                                upgradesInfo.style.display = "inline-block";
//                                upgradesInfo.innerHTML = "Items (" + points + ")";
//                            }
//                        }
                    }
                }
        ).on("3", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        //todo sid, values
                        Utility.print("socket.on: 3");
                        final int tmpIndx = Game.getPlayerIndex((Integer) (args[0]));
                        final JSONArray values = (JSONArray) args[1];
                        if (tmpIndx >= 0) {
                            final Player tmpUser = users.get(tmpIndx);
                            if (tmpUser != null)
                                for (int i = 0; i < values.length(); ) {

                                    final String key = String.valueOf(values.get(i));
                                    if (key.equals("lvl")) {
                                        tmpUser.lvl = String.valueOf(values.get(i + 1));

                                    } else if (key.equals("maxHealth")) {
                                        tmpUser.maxHealth = (Integer) (values.get(i + 1));

                                    } else if (key.equals("health")) {
                                        tmpUser.health = (Integer) (values.get(i + 1));
                                    } else if (key.equals("speedDiv")) {
                                        tmpUser.speedDiv = (Float) (values.get(i + 1));
                                    } else if (key.equals("width")) {
                                        tmpUser.w = (Float) (values.get(i + 1));
                                    } else if (key.equals("healthRegen")) {
                                        tmpUser.healthRegen = (Float) (values.get(i + 1));
                                    } else if (key.equals("cannonSpeed")) {
                                        tmpUser.cannonSpeed = (Float) (values.get(i + 1));
                                    } else if (key.equals("cannonWidth")) {
                                        tmpUser.cannonWidth = (Float) (values.get(i + 1));
                                    } else if (key.equals("cannonLength")) {
                                        tmpUser.cannonLength = (Float) (values.get(i + 1));
                                    } else if (key.equals("cannonDmg")) {
                                        tmpUser.cannonDmg = (Float) (values.get(i + 1));
                                    } else if (key.equals("length")) {
                                        tmpUser.length = (Float) (values.get(i + 1));
                                    } else if (key.equals("reloadDiv")) {
                                        tmpUser.reloadDiv = (Float) (values.get(i + 1));
                                    } else if (key.equals("speed")) {
                                        tmpUser.speed = (Float) (values.get(i + 1));
                                    } else if (key.equals("turnSpeed")) {
                                        tmpUser.turnSpeed = (Float) (values.get(i + 1));
                                    } else if (key.equals("crashDamage")) {
                                        tmpUser.crashDamage = (Float) (values.get(i + 1));
                                    }
                                    i += 2;
                                }
                        }
                    }
                }
        ).on("4", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        //todo indx, data
                        Utility.print("socket.on: 4");
                        final int index = (Integer) args[0];
                        final int diff = index - gameObjects.size();

                        if (diff > 0)
                            for (int i = 0; i < diff + 5; i++)
                                gameObjects.add(new Obstacle());
                        final Obstacle tmpObj = gameObjects.get(index);
                        if (args.length < 2)
                            return;
                        final JSONArray data = (JSONArray) args[1];
                        if (tmpObj != null) {
                            if (data != null) {
                                tmpObj.x = ((Double) Double.parseDouble(String.valueOf(data.get(0)))).intValue();
                                tmpObj.xS = ((Double) Double.parseDouble(String.valueOf(data.get(1)))).intValue();
                                tmpObj.y = ((Double) Double.parseDouble(String.valueOf(data.get(2)))).intValue();
                                tmpObj.yS = ((Double) Double.parseDouble(String.valueOf(data.get(3)))).intValue();
                                tmpObj.s = ((Double) Double.parseDouble(String.valueOf(data.get(4)))).intValue();
                                tmpObj.c = ((Double) Double.parseDouble(String.valueOf(data.get(5)))).intValue();
                                tmpObj.shp = ((Double) Double.parseDouble(String.valueOf(data.get(6)))).intValue();
                                tmpObj.active = true;
                            } else {
                                tmpObj.active = false;
                            }
                        }
                    }
                }
        ).on("5", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
//                   working     Utility.print("socket.on: 5");
                        //Todo sid, wpnIndex
                        int tmpIndx = Game.getPlayerIndex((Integer) args[0]);
                        int wpnIndex = Game.getPlayerIndex((Integer) args[1]);
                        if (tmpIndx >= 0) {
                            final Player tmpUser = users.get(tmpIndx);
                            for (int i = 0; i < 4; i++)
                                tmpUser.animMults.get(i).mult = 1;

                            tmpUser.animMults.get(wpnIndex).plus = -0.03f;
                        }
                    }
                }
        ).on("6", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final String val = String.valueOf(args[1]);
                        if (args.length < 2 || val.equals("null"))
                            return;
                        //Todo  sid, value
                        final int sid = (Integer) args[0];
                        int tmpIndx = Game.getPlayerIndex(sid);
//           working             Utility.print("socket.on: 6");
                        float value = Float.parseFloat(val);
                        if (tmpIndx >= 0) {
                            final Player tmpUser = users.get(tmpIndx);
                            tmpUser.health = value;
                            if (tmpUser.health <= 0) {
                                tmpUser.dead = true;
                                if (player != null && (sid) == player.sid) {
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
                    public void call(Object... args) {
//                     working   Utility.print("socket.on: 7");
                        //Todo value
                        Game.scoreText = String.valueOf(args[0]);
                    }
                }

        ).on("n", new Emitter.Listener() {

                    @Override
                    public void call
                            (Object... args) {
                        Utility.print("socket.on: n");
                        //todo txt
                        Game.showNotification(String.valueOf(args[0]));
                    }
                }

        ).on("s", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        //todo val
                        Utility.print("socket.on: s");
                        Game.showScoreNotif((Integer) (args[0]));
                    }
                }

        ).on("m", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        //Todo datas
//             working           Utility.print("socket.on: m");
                        final JSONArray data = (JSONArray) args[0];
                        final int numItems = data.length() / 4;
                        if (miniMapItems.size() < numItems)
                            for (int i = 0; i < miniMapItems.size() - numItems; i++)
                                miniMapItems.add(new MiniMapItem());
                        int item = 0;
                        //Todo might have to add minimapItems
                        for (int i = 0; item < miniMapItems.size(); ) {
                            final MiniMapItem miniMapItem = miniMapItems.get(i);
                            if (item >= numItems) {
                                miniMapItem.active = false;
                            } else {
                                miniMapItem.active = true;
                                miniMapItem.color = Game.pingColors[(Integer) data.get(i)];
                                miniMapItem.w = Game.pingColors[(Integer) data.get(i)];
                                miniMapItem.x = (((Float) data.get(i + 1) + gameData.mapScale) / (gameData.mapScale * 2)) * minimap.width;
                                miniMapItem.y = (((Float) data.get(i + 2) + gameData.mapScale) / (gameData.mapScale * 2)) * minimap.height;
                                miniMapItem.w = ((Float) data.get(i + 3)) * 2;
                                miniMapItem.h = miniMapItem.w;
                            }

                            i += 4;
                            item++;
                        }
                    }
                }

        );

        // LOAD DATA:
        // setControlSheme(controlIndex);
        Utility.saveInt("contrlSt", 1);
        socket.emit("6", "cont", 1);
        controlIndex = 1;
        socket.connect();
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
    public static void updateUserData(Object singleObject, JSONArray listObj) {

        if (singleObject != null && String.valueOf(singleObject).length() > 5) {
            final JSONObject obj = (JSONObject) singleObject;
            obj.put("visible", true);
            Game.updateOrPushUser(obj);
        } else if (listObj != null) {
            for (int i = 0; i < users.size(); ++i) {
                if (!users.get(i).visible)
                    users.get(i).forcePos = 1;
                users.get(i).visible = false;
            }
            for (int i = 0; i < listObj.length(); ) {
                final int tmpIndx = Game.getPlayerIndex((Integer) listObj.get(i));
                if (tmpIndx < 0 || tmpIndx >= users.size())
                    continue;
                final Player p = users.get(tmpIndx);
                if (tmpIndx >= 0) {
                    p.x = (Float) listObj.get(i + 1);
                    p.y = (Float) listObj.get(i + 2);
                    p.dir = (Float) listObj.get(i + 3);
                    p.targetDir = (Float) listObj.get(i + 4);
                    p.aimDir = (Float) listObj.get(i + 5);
                    p.visible = true;
                }
                i += 6;
            }
        }
    }
}
