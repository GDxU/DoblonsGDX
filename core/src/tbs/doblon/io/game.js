// INIT:
var targetFPS = 60;
var delta, delta2, currentTime, oldTime = 0;
var gameState = 0;
var gameData;
var upgradesHidden = false;


// SOCKET:
var socket, port;

// OVERWRITES:
Number.prototype.round = function (places) {
    return +this.toFixed(places);
}
CanvasRenderingContext2D.prototype.roundRect = function (x, y, w, h, r, s) {
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

// MATHS:
var MathPI = Math.PI;
var MathCOS = Math.cos;
var MathSIN = Math.sin;
var MathABS = Math.abs;
var MathPOW = Math.pow;
var MathMIN = Math.min;
var MathMAX = Math.max;
var MathATAN2 = Math.atan2;

// HTML ELEMENTS:
var mainCanvas = document.getElementById('mainCanvas');
var mainContext = mainCanvas.getContext('2d');
var gameTitle = document.getElementById('gameTitle');
var instructionsText = document.getElementById('instructionsText');
var gameUiContainer = document.getElementById('gameUiContainer');
var fireButton = document.getElementById('fireButton');
var userInfoContainer = document.getElementById('userInfoContainer');
var loadingContainer = document.getElementById('loadingContainer');
var enterGameButton = document.getElementById('enterGameButton');
var userNameInput = document.getElementById('userNameInput');
var menuContainer = document.getElementById('menuContainer');
var darkener = document.getElementById('darkener');
var leaderboardText = document.getElementById('leaderboardText');
var leaderboardList = document.getElementById('leaderboardList');
var upgradeContainer = document.getElementById('upgradeContainer');
var upgrades = document.getElementById('upgrades');
var coinDisplay = document.getElementById('coinDisplay');
var upgradeList = document.getElementById('upgradeList');
var upgradesText = document.getElementById('upgradesText');
var scoreText = document.getElementById('scoreText');
var className = document.getElementById('className');
var minimap = document.getElementById('minimap');
var minimapContext = minimap.getContext('2d');
var weaponsProgress = document.getElementById('weaponsProgress');
var wpnsProgressBar = document.getElementById('wpnsProgressBar');
var weaponsList = document.getElementById('weaponsList');
var weaponsPopups = document.getElementById('weaponsPopups');
var upgradesInfo = document.getElementById('upgradesInfo');
var skinInfo = document.getElementById('skinInfo');
var skinSelector = document.getElementById('skinSelector');
var skinName = document.getElementById('skinName');
var skinIcon = document.getElementById('skinIcon');
var wpnsProgressTxt = document.getElementById('wpnsProgressTxt');

// SRTINGS:
var instructionsIndex = 0;
var instructionsSpeed = 5500;
var insturctionsCountdown = 0;
var instructionsList = [
    "use A & D or Arrow Keys control the direction of your ship",
    "use your mouse to aim and fire your cannons",
    "destroy enemy ships and collect doblons to upgrade your ship"
];
instructionsIndex = UTILS.randInt(0, instructionsList.length - 1);
var randomLoadingTexts = [
    "discovering treasure...",
    "setting sail..."
];

// INPUTS:
var upgrInputsToIndex = {
    "k48": 9,
    "k49": 0,
    "k50": 1,
    "k51": 2,
    "k52": 3,
    "k53": 4,
    "k54": 5,
    "k55": 6,
    "k56": 7,
    "k57": 8,
    "k84": 10,
    "k89": 11
};

var keys = {
    l: 0,
    r: 0,
    u: 0,
    d: 0
};
function resetKeys() {
    keys.l = 0;
    keys.r = 0;
}

// GAMEOBJECTS & VALUES:
var hasStorage = (typeof(Storage) !== "undefined");
if (hasStorage) {
    var cid = localStorage.getItem("sckt");
    if (!cid) {
        cid = UTILS.getUniqueID();
        localStorage.setItem("sckt", cid);
    }
}
var partyKey = null;
var player = null;
var modeList = null;
var modeIndex = null;
var currentMode = null;
var dayTimeValue = 0;
var users = [];
var gameObjects = [];
var target = 0;
var targetD = 1;
var turnDir = 0;
var speedInc = 0;
var mTarget = 0;

// CONTROL SETTINGS:
var controlIndex = 0;
if (hasStorage) {
    var contIndx = localStorage.getItem("contrlSt");
    if (contIndx)
        controlIndex = contIndx;
}
// var controlShemes = [{
//     id: 1,
//     name: "<i style='vertical-align: middle;' class='material-icons'>&#xE312;</i> Keyboard & Mouse"
// }, {
//     id: 2,
//     name: "<i style='vertical-align: middle;' class='material-icons'>&#xE323;</i> Mouse Only"
// }];
// function setControlSheme(indx) {
//     controlsButton.innerHTML = controlShemes[indx].name;
//     localStorage.setItem("contrlSt", indx);
//     socket.emit('6', 'cont', indx);
// }
// function toggleControls() {
//     controlIndex++;
//     if (controlIndex >= controlShemes.length)
//         controlIndex = 0;
//     setControlSheme(controlIndex);
// }

// SCALING:
var viewMult = 1;
var maxScreenWidth = 2208; // 1920;
var maxScreenHeight = 1242; // 1080;
var originalScreenWidth = maxScreenWidth;
var originalScreenHeight = maxScreenHeight;
var screenWidth, screenHeight;

// GLOBAL COLORS:
var darkColor = "#4d4d4d";

// PAGE IS READY:
function getURLParam(name, url) {
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
window.onload = function () {

    // SETUP MENU BUTTON LISTENERS:
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

    // CONNECT TO SERVER:
    $.get("/getIP", {sip: lobbyURLIP}, function (data) {
        port = data.port;

        // SETUP SOCKET:
        if (!socket) {
            socket = io.connect('http://' + (data.ip) + ':' + data.port, {
                'connect timeout': 3000,
                'reconnection': true,
                'query': ('cid=' + cid + '&rmid=' + lobbyRoomID + '&apid=19ytahhsb')
            });
            setupSocket();
        }
    });
}

// GAME INPUT:
mainCanvas.addEventListener('mousemove', gameInput, false);
mainCanvas.addEventListener('mousedown', mouseDown, false);
mainCanvas.addEventListener('mouseup', mouseUp, false);
var mouseX, mouseY;
var forceTarget = true;
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
var shooting = false;

fireButton.addEventListener('touchstart', function () {
    socket.emit('2', 1);
});

fireButton.addEventListener('touchend', function () {
    socket.emit('2');
});

upgrades.addEventListener('touchstart', function () {
    toggleUpgrades();
});
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

toggleUpgrades();

function toggleUpgrades() {
    if (upgradesHidden) {
        upgradeContainer.style.display = 'inline-block'
    } else {
        upgradeContainer.style.display = 'none'
    }

    upgradesHidden = (!upgradesHidden);
}

// SETUP SOCKET:
function setupSocket() {

    // ERROR HANDLING:
    socket.on('connect_error', function () {
        if (lobbyURLIP) {
            kickPlayer("Connection failed. Please check your lobby ID");
        } else {
            kickPlayer("Connection failed. Check your internet and firewall settings");
        }
    });
    socket.on('disconnect', function (reason) {
        kickPlayer("Disconnected.");
        console.log("Send this to the dev: " + reason);
    });
    socket.on('error', function (err) {
        kickPlayer("Disconnected. The server may have updated.");
        console.log("Send this to the dev: " + err);
    });
    socket.on('kick', function (reason) {
        kickPlayer(reason);
    });

    // LOBBY KEY:
    socket.on('lk', function (lKey) {
        partyKey = lKey;
    });

    // MODES LIST:
    socket.on('mds', function (data, crnt) {
        modeList = data;
        // Todo modeSelector.innerHTML = data[crnt].name + "  <i style='vertical-align: middle;' class='material-icons'>&#xE5C5;</i>";
        modeIndex = crnt;
        currentMode = modeList[crnt];
    });

    // VIEW MULT:
    socket.on('v', function (sw, sh, mult) {
        if (viewMult != mult) {
            viewMult = mult;
            maxScreenWidth = Math.round(sw * mult);
            maxScreenHeight = Math.round(sh * mult);
            resize();
        }
    });

    // YOU SPAWN:
    socket.on('spawn', function (gameObject, data) {
        if (!objectExists(gameObject)) {
            users.push(gameObject);
        } else {
            updateOrPushUser(gameObject);
        }
        player = gameObject;
        gameState = 1;
        toggleMenuUI(false);
        toggleGameUI(true);
        mainCanvas.focus();
        if (data) {
            gameData = data;
            gameObjects.length = 0;
            for (var i = 0; i < data.objCount; ++i) {
                gameObjects.push({});
            }
            data = null;
        }
        targetD = 1;
        resetKeys();
        gameObject = null;
    });

    // USER LEFT:
    socket.on('d', function (id) {
        var tmpIndx = getPlayerIndexById(id);
        if (tmpIndx != null) {
            users.splice(tmpIndx, 1);
        }
    });

    // DAY/NIGHT TIME:
    socket.on('dnt', function (timeStr, dnt) {
        // timeDisplay.innerHTML = timeStr;
        dayTimeValue = dnt;
    });

    // GET LEADERBOARD DATA:
    socket.on('0', function (list) {
        var rank = 1;
        var pos = -1;

        for (var i = 0; i < list.length;) {
            if (player && list[i] == player.sid)
                pos = rank;
            i += 4;
            rank++;
        }

        leaderboardText.innerHTML = "pos " + (pos < 0 ? '10+' : pos) + ' of ' + rank;

        // CLEAR:
        delete list;
    });

    // GET USER DATA:
    function updateUserData(singleObj, listObj) {
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
            for (var i = 0; i < listObj.length;) {
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
            delete listObj;
        }
    }

    socket.on('1', updateUserData);

    // UPGRADES LIST & COINS:
    socket.on('2', function (upgrC, fleetC, data, points) {
        if (data) {
            var tmpHTML = "";
            upgradesText.innerHTML = "~ Upgrades " + upgrC + ' ~ Fleet ' + fleetC;
            var num = 1;
            for (var i = 0; i < data.length;) {
                var tmpW = ((data[i + 2] - 1) / (data[i + 3] - 1)) * (39 / 65 * weaponsProgress.clientWidth);
                if (num == 9)
                    tmpHTML += "</br>"
                tmpHTML += "<div class='upgradeItem' onclick='doUpgrade(" + (num - 1) + ",0,1)'><div class='upgrProg' style='width:" + tmpW + "px'></div>" +
                    (data[i]) + " <span class='greyMenuText'>" + ((data[i + 2] != data[i + 3]) ? ("$" + data[i + 1]) : "max")
                    + "</span></div></br>";
                i += 4;
                num++;
            }
            upgradeList.innerHTML = tmpHTML;
            if (!upgradesHidden)
                upgradeContainer.style.display = "inline-block";
        }
        if (points != undefined)
            coinDisplay.innerHTML = "Coins <span class='greyMenuText'>$" + (points || 0) + "</span>";
    });

    // WEAPONS LIST AND PROGRESS:
    socket.on('8', function (data, points, prog) {
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
                for (var i = 0; i < data.length;) {
                    tmpHTML += "<div onclick='showWeaponPopup(" + num + ")' class='weaponItem'><div class='upgradeTxt'>" + data[i] + "</div><div class='upgradeNum'>Tier " +
                        data[i + 1] + "</div></div>";
                    tmpHTML2 += "<div id='popupRow" + num + "' class='weaponPopupRow'>";
                    for (var x = 0; x < data[i + 2].length; ++x) {
                        tmpHTML2 += "<div onclick='doUpgrade(" + x + "," + num + ")' class='weaponPopupItem' style='bottom:" + (bot * x / 3) + "vh'>" + data[i + 2][x] + "</div>";
                    }
                    tmpHTML2 += "</div>";
                    i += 3;
                    num++;
                }
                weaponsList.innerHTML = tmpHTML;
                weaponsPopups.innerHTML = tmpHTML2;
            }
            if (prog != undefined) {
                wpnsProgressBar.style.width = weaponsProgress.clientWidth * prog + 'px';
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
    });

    // UPDATE VALUE:
    socket.on('3', function (sid, values) {
        var tmpIndx = getPlayerIndex(sid);
        if (tmpIndx != null) {
            var tmpUser = users[tmpIndx];
            for (var i = 0; i < values.length;) {
                tmpUser[values[i]] = values[i + 1];
                i += 2;
            }
        }
    });

    // UPDATE GAMEOBJECT:
    var tmpObj;
    socket.on('4', function (indx, data) {
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
    });

    // SOMEONE SHOT:
    socket.on('5', function (sid, wpnIndex) {
        var tmpIndx = getPlayerIndex(sid);
        if (tmpIndx != null) {
            var tmpUser = users[tmpIndx];
            if (!tmpUser.animMults)
                tmpUser.animMults = [{mult: 1}, {mult: 1}, {mult: 1}, {mult: 1}];
            tmpUser.animMults[wpnIndex].plus = -0.03;
        }
    });

    // CHANGE HEALTH:
    socket.on('6', function (sid, value) {
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
    });

    // CHANGE SCORE:
    socket.on('7', function (value) {
        scoreText.innerHTML = value;
    });

    // NOTIFICATION:
    socket.on('n', function (txt) {
        showNotification(txt);
    });

    // SCORE NOTIFICATION:
    socket.on('s', function (val) {
        showScoreNotif(val);
    });

    // MINIMAP:
    var pingColors = ["#fff", "#fff", "#ff6363", "#ff6363", "rgba(103,255,62,0.3)", "rgba(255,255,255,0.4)", "#63b0ff"];
    socket.on('m', function (data) {
        minimap.width = minimap.width;
        for (var i = 0; i < data.length;) {
            minimapContext.fillStyle = pingColors[data[i]];
            minimapContext.font = "10px regularF";
            minimapContext.beginPath();
            minimapContext.arc(((data[i + 1] + gameData.mapScale) / (gameData.mapScale * 2)) * minimap.width,
                ((data[i + 2] + gameData.mapScale) / (gameData.mapScale * 2)) * minimap.height, data[i + 3] * 2, 0, MathPI * 2, true);
            minimapContext.closePath();
            minimapContext.fill();
            i += 4;
        }
        data = null;
    });

    // LOAD DATA:
    // setControlSheme(controlIndex);
    localStorage.setItem("contrlSt", 1);
    socket.emit('6', 'cont', 1);
    controlIndex = 1;
}

// MODE:
/* Todo modes function showModeList() {
 if (modeList) {
 if (modeListView.style.display == "block") {
 modeListView.style.display = "none";
 } else {
 var tmpHTML = "";
 for (var i = 0; i < modeList.length; ++i) {
 tmpHTML += "<div onclick='changeMode(" + i + ")' class='modeListItem'>" + modeList[i].name + "</div>";
 }
 modeListView.style.display = "block";
 modeListView.innerHTML = tmpHTML;
 }
 }
 }
 function changeMode(indx) {
 if (modeList && modeList[indx] && indx !== modeIndex) {
 modeListView.style.display = "none";
 modeSelector.innerHTML = modeList[indx].name + "<i style='vertical-align: middle;' class='material-icons'>&#xE5C5;</i>";
 window.location.href = modeList[indx].url;
 }
 }

 // PARTY:
 function loadPartyKey() {
 if (partyKey) {
 window.history.pushState("", "Doblons.io", "/?l=" + partyKey);
 lobbyKeyText.innerHTML = "send the url above to a friend";
 lobbyKey.className = "deadLink";
 }
 }*/

// ENTER THE GAME:
if (hasStorage && localStorage.getItem("lstnmdbl")) {
    userNameInput.value = localStorage.getItem("lstnmdbl");
}
function enterGame() {
    if (socket) {
        showMainMenuText(randomLoadingTexts[UTILS.randInt(0, randomLoadingTexts.length - 1)]);
        socket.emit('respawn', {
            name: userNameInput.value,
            skin: skinIndex
        });
        localStorage.setItem("lstnmdbl", userNameInput.value);
        mainCanvas.focus();
    }
}

// LEAVE GAME TO MENU:
function leaveGame() {
    gameState = 0;
    toggleGameUI(false);
    toggleMenuUI(true);
}

// DO UPGRADE:
function doUpgrade(index, pos, tp) {
    socket.emit('3', index, pos, tp);
}

// WEAPON POPUP:
var activePopup;
function showWeaponPopup(indx) {
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

// SKULL ICONS:
var iconsCount = 5;
var skullIconSize = 50;
var iconsList = [];
for (var i = 1; i < iconsCount; ++i) {
    var tmpImg = new Image();
    tmpImg.onload = (function (val) {
        return function () {
            this.onLoad = null;
            iconsList[val] = this;
        }
    })(i);
    tmpImg.src = ".././img/icons/skull_" + i + ".png";
}

// PLAYER CANVAS:
var playerCanvas = document.createElement("canvas");
var playerCanvasScale = 430;
var maxFlashAlpha = 0.25;
playerCanvas.width = playerCanvas.height = playerCanvasScale;

// SKINS:
var skinIndex = 0;
if (hasStorage) {
    skinIndex = parseInt(localStorage.getItem("sknInx")) || 0;
}
var userSkins = [{
    name: "Default",
    color1: "#eb6d6d",
    color2: "#949494"
}, {
    name: "Purple",
    color1: "#b96bed",
    color2: "#949494"
}, {
    name: "Green",
    color1: "#6FED6B",
    color2: "#949494"
}, {
    name: "Orange",
    color1: "#EDB76B",
    color2: "#949494"
}, {
    name: "Black",
    color1: "#696969",
    color2: "#949494"
}, {
    name: "Navy",
    color1: "#adadad",
    color2: "#949494"
}, {
    name: "Ghostly",
    color1: "#9BEB6D",
    color2: "#949494",
    opacity: 0.7,
    overlay: "rgba(0,255,0,0.3)"
}, {
    name: "Glass",
    color1: "#6DEBDE",
    color2: "#949494",
    opacity: 0.6,
    overlay: "rgba(0,0,255,0.3)"
}, {
    name: "Pirate",
    color1: "#5E5E5E",
    color2: "#737373"
}, {
    name: "Sketch",
    color1: "#E8E8E8",
    color2: "#E8E8E8"
}, {
    name: "Gold",
    color1: "#e9cd5f",
    color2: "#949494"
}, {
    name: "Hazard",
    color1: "#737373",
    color2: "#e9cd5f"
}, {
    name: "Apples",
    color1: "#91DB30",
    color2: "#eb6d6d"
}, {
    name: "Beach",
    color1: "#eac086",
    color2: "#FFD57D"
}, {
    name: "Wood",
    color1: "#8c5d20",
    color2: "#949494"
}, {
    name: "Diamond",
    color1: "#6FE8E2",
    color2: "#EDEDED"
}, {
    name: "Midnight",
    color1: "#5E5E5E",
    color2: "#A763BA"
}, {
    name: "Valentine",
    color1: "#FE4998",
    color2: "#F9A7FE"
}, {
    name: "Cheddar",
    color1: "#FCA403",
    color2: "#FDFC08"
}, {
    name: "PewDie",
    color1: "#09BBDF",
    color2: "#1D3245"
}, {
    name: "Crimson",
    color1: "#6B3333",
    color2: "#AD3E3E"
}, {
    name: "Banana",
    color1: "#fbf079",
    color2: "#f9f9f9"
}, {
    name: "Cherry",
    color1: "#F8E0F7",
    color2: "#F5A9F2"
}, {
    name: "Moon",
    color1: "#1C1C1C",
    color2: "#F2F5A9"
}, {
    name: "Master",
    color1: "#fce525",
    color2: "#bb5e0e"
}, {
    name: "Reddit",
    color1: "#fe562d",
    color2: "#f9f9f9"
}, {
    name: "4Chan",
    color1: "#ffd3b4",
    color2: "#3c8d2e"
}, {
    name: "Necron",
    color1: "#808080",
    color2: "#80ff80"
}, {
    name: "Ambient",
    color1: "#626262",
    color2: "#80ffff"
}, {
    name: "Uranium",
    color1: "#5a9452",
    color2: "#80ff80"
}, {
    name: "XPlode",
    color1: "#fe4c00",
    color2: "#f8bf00"
}, {
    name: "巧克力",
    color1: "#804029",
    color2: "#f9ebb4"
}];
var renderedSkins = [];
var skinDisplayIconSize = 200;
function changeSkin(val) {
    skinIndex += val;
    if (skinIndex >= userSkins.length)
        skinIndex = 0;
    else if (skinIndex < 0)
        skinIndex = userSkins.length - 1;
    if (!renderedSkins[skinIndex]) {
        var tmpCanvas = document.createElement('canvas');
        tmpCanvas.width = tmpCanvas.height = skinDisplayIconSize;
        var tmpContext = tmpCanvas.getContext('2d');
        tmpContext.translate((tmpCanvas.width / 2), (tmpCanvas.height / 2));
        tmpContext.lineJoin = "round";
        renderPlayer(tmpContext, {
            dir: (MathPI),
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
        localStorage.setItem("sknInx", skinIndex);
    }
}
changeSkin(0);
$('#skinSelector').bind("contextmenu", function (e) {
    changeSkin(-1);
    return false;
});
if (hasStorage) {
    if (localStorage.getItem("isFollDob")) {
        unlockSkins(0);
    }
}
function unlockSkins(indx) {
    if (!indx) {
        skinInfo.style.display = "inline-block";
        skinSelector.style.display = "inline-block";
        if (hasStorage) {
            localStorage.setItem("isFollDob", 1);
        }
    }
}

// UPDATE THE GAME:
var playerContext = playerCanvas.getContext('2d');
var updateGameLoop = function (delta) {

    // IF PLAYER IS SET:
    if (player) {

        // INTERPOLATION & VISIBILITY:
        var tmpObj;
        for (var i = 0; i < users.length; ++i) {
            tmpObj = users[i];
            if (tmpObj.visible && !tmpObj.dead) {
                if (tmpObj.forcePos || tmpObj.localX == undefined || tmpObj.localY == undefined) {
                    tmpObj.localX = tmpObj.x;
                    tmpObj.localY = tmpObj.y;
                    tmpObj.forcePos = 0;
                } else {
                    var difference = tmpObj.x - tmpObj.localX;
                    tmpObj.localX += (difference * delta * 0.0175);
                    difference = tmpObj.y - tmpObj.localY;
                    tmpObj.localY += (difference * delta * 0.0175);
                }
            }
        }

        // SET OFFSET:
        tmpObj = users[getPlayerIndex(player.sid)];
        var tmpPlayer = tmpObj;
        var xOffset, yOffset;
        if (tmpObj) {
            xOffset = tmpObj.localX;
            yOffset = tmpObj.localY;
        }
        var camX = (xOffset || 0) - (maxScreenWidth / 2) - screenSkX;
        var camY = (yOffset || 0) - (maxScreenHeight / 2) - screenSkY;
        var tmpX, tmpY;

        // RENDER MAP:
        if (gameData) {

            // OUTSIDE WALLS:
            mainContext.lineWidth = 7;
            mainContext.fillStyle = gameData.outerColor;
            mainContext.fillRect(0, 0, maxScreenWidth, maxScreenHeight);
            mainContext.fillStyle = gameData.waterColor;
            mainContext.roundRect(MathMAX(-7, -gameData.mapScale - camX), MathMAX(-7, -gameData.mapScale - camY),
                MathMIN(maxScreenWidth + 14, gameData.mapScale - camX + 7), MathMIN(maxScreenHeight + 14, gameData.mapScale - camY + 7), 0).fill();
            mainContext.stroke();

            // GRID:
            mainContext.lineWidth = 5;
            mainContext.strokeStyle = darkColor;
            mainContext.globalAlpha = 0.18;
            mainContext.beginPath();
            var grdX = -camX - gameData.mapScale - (1920 / 2);
            for (var x = grdX; x < maxScreenWidth; x += 40) {
                mainContext.moveTo(x, 0);
                mainContext.lineTo(x, maxScreenHeight);
            }
            var grdY = -camY - gameData.mapScale - (1920 / 2);
            for (var y = grdY; y < maxScreenHeight; y += 40) {
                mainContext.moveTo(0, y);
                mainContext.lineTo(maxScreenWidth, y);
            }
            mainContext.stroke();
            mainContext.globalAlpha = 1;

            // RENDER ISLANDS:
            if (gameData.islands) {
                var tmpIsl;
                for (var i = 0; i < gameData.islands.length; ++i) {
                    tmpIsl = gameData.islands[i];
                    tmpX = tmpIsl.x - camX;
                    tmpY = tmpIsl.y - camY;
                    if (tmpX + tmpIsl.s + 125 >= 0 && tmpY + tmpIsl.s + 125 >= 0
                        && tmpX - tmpIsl.s - 125 <= maxScreenWidth && tmpY - tmpIsl.s - 125 <= maxScreenHeight) {
                        drawIsland(tmpX, tmpY, tmpIsl.s, tmpIsl.i, mainContext);
                    }
                }
                tmpIsl = null;
            }
        }

        // RENDER GAMEOBJECTS:
        mainContext.lineWidth = 8.5;
        for (var i = 0; i < gameObjects.length; ++i) {
            tmpObj = gameObjects[i];
            if (tmpObj.active) {
                tmpObj.x += (tmpObj.xS * (delta / 1000));
                tmpObj.y += (tmpObj.yS * (delta / 1000));
                tmpX = tmpObj.x - camX;
                tmpY = tmpObj.y - camY;
                if (tmpX + tmpObj.s >= 0 && tmpY + tmpObj.s >= 0
                    && tmpX - tmpObj.s <= maxScreenWidth && tmpY - tmpObj.s <= maxScreenHeight) {
                    mainContext.translate(tmpX, tmpY);
                    renderGameObject(tmpObj, mainContext);
                    mainContext.translate(-tmpX, -tmpY);
                }
            }
        }

        // RENDER PLAYERS:
        var tmpS;
        for (var i = 0; i < users.length; ++i) {
            tmpObj = users[i];
            if (tmpObj.visible && !tmpObj.dead) {
                tmpX = tmpObj.localX - camX;
                tmpY = tmpObj.localY - camY;
                tmpS = userSkins[tmpObj.skin || 0];
                if (!tmpS)
                    tmpS = userSkins[0];

                // CANNON ANIMATIONS:
                if (tmpObj.animMults) {
                    for (var a = 0; a < tmpObj.animMults.length; ++a) {
                        if (tmpObj.animMults[a].plus) {
                            tmpObj.animMults[a].mult += tmpObj.animMults[a].plus;
                            if (tmpObj.animMults[a].mult >= 1) {
                                tmpObj.animMults[a].mult = 1;
                                tmpObj.animMults[a].plus = 0;
                            }
                            if (tmpObj.animMults[a].mult < 0.8) {
                                tmpObj.animMults[a].mult = 0.8;
                                tmpObj.animMults[a].plus *= -1;
                            }
                        }
                    }
                }

                // RENDER PLAYER:
                playerCanvas.width = playerCanvas.height = (tmpObj.length + tmpObj.ramLength + tmpObj.rudder) + 60;
                playerContext.translate((playerCanvas.width / 2), (playerCanvas.height / 2));
                renderPlayer(playerContext, tmpObj, tmpX, tmpY, tmpS, delta);

                // SPAWN PROT:
                if (tmpObj.spawnProt) {
                    if (tmpObj.flashAlpha == undefined) {
                        tmpObj.flashAlpha = maxFlashAlpha;
                        tmpObj.flashInc = 0.0005;
                    }
                    tmpObj.flashAlpha += (tmpObj.flashInc * delta);
                    if (tmpObj.flashAlpha > maxFlashAlpha) {
                        tmpObj.flashAlpha = maxFlashAlpha;
                        tmpObj.flashInc *= -1;
                    } else if (tmpObj.flashAlpha <= 0) {
                        tmpObj.flashAlpha = 0;
                        tmpObj.flashInc *= -1;
                    }
                    playerContext.globalCompositeOperation = "source-atop";
                    playerContext.fillStyle = "rgba(255, 255, 255, " + tmpObj.flashAlpha + ")";
                    playerContext.fillRect(-playerCanvas.width / 2, -playerCanvas.height / 2, playerCanvas.width,
                        playerCanvas.height);
                    playerContext.globalCompositeOperation = "source-over";
                }
                playerContext.translate(-(playerCanvas.width / 2), -(playerCanvas.height / 2));

                // EFFECTS:
                if (tmpS.opacity)
                    mainContext.globalAlpha = tmpS.opacity;

                // ACTUAL RENDER:
                mainContext.save();
                mainContext.translate(tmpX, tmpY);
                mainContext.rotate(tmpObj.dir - (MathPI / 2));
                mainContext.drawImage(playerCanvas, -(playerCanvas.width / 2), -(playerCanvas.height / 2));
                mainContext.restore();
                mainContext.globalAlpha = 1;
                mainContext.globalCompositeOperation = "source-over";
            }
        }

        // PLAYER UI:
        var UIPadding, maxBarWidth, barPadding,
            percentage, barWidth, barHeight, szMult, tmpSID;
        for (var i = 0; i < users.length; ++i) {
            tmpObj = users[i];
            if (tmpObj.visible && !tmpObj.dead) {
                tmpX = tmpObj.localX - camX;
                tmpY = tmpObj.localY - camY;
                szMult = (1 + (tmpObj.length / 270));
                UIPadding = (tmpObj.length / 3.4);

                // RENDER NAME UI TO SPRITE:
                if (tmpObj.name) {
                    tmpSID = (tmpObj.name + "-" + tmpObj.lvl + "-" + szMult);
                    if (tmpObj.nameSpriteID != tmpSID) {
                        var tmpCanvas = document.createElement("canvas");
                        var tmpContext = tmpCanvas.getContext('2d');
                        var nameTextSize = 25 * szMult;
                        tmpContext.font = nameTextSize + "px regularF";
                        var nameMeasure = tmpContext.measureText(tmpObj.name);
                        tmpContext.font = (nameTextSize * 1.3) + "px regularF";
                        var lvlMeasure = tmpContext.measureText(tmpObj.lvl ? tmpObj.lvl + "" : "");
                        tmpContext.font = nameTextSize + "px regularF";
                        tmpCanvas.width = (nameMeasure.width + (lvlMeasure.width * 2)) + 20;
                        tmpCanvas.height = (nameTextSize * 2);
                        tmpContext.translate(tmpCanvas.width / 2, tmpCanvas.height / 2);
                        tmpContext.font = nameTextSize + "px regularF";
                        tmpContext.fillStyle = '#ffffff';
                        tmpContext.strokeStyle = darkColor;
                        tmpContext.lineWidth = 6.5;
                        tmpContext.textAlign = "center";
                        if (tmpCanvas.width <= 600) {
                            tmpContext.strokeText(tmpObj.name, 0, 0);
                            tmpContext.fillText(tmpObj.name, 0, 0);
                            if (tmpObj.lvl) {
                                tmpContext.font = (nameTextSize * 1.3) + "px regularF";
                                var tmpLvlX = -(nameMeasure.width / 2) - (10 + (lvlMeasure.width / 2));
                                tmpContext.strokeStyle = darkColor;
                                tmpContext.strokeText(tmpObj.lvl, tmpLvlX, 0);
                                tmpContext.fillText(tmpObj.lvl, tmpLvlX, 0);
                            }
                        }
                        tmpObj.nameSpriteID = tmpSID;
                        tmpObj.nameSprite = tmpCanvas;
                    }
                    if (tmpObj.nameSprite) {
                        mainContext.drawImage(tmpObj.nameSprite, tmpX - (tmpObj.nameSprite.width / 2),
                            tmpY - (UIPadding * 2) - (tmpObj.nameSprite.height / 2), tmpObj.nameSprite.width, tmpObj.nameSprite.height);
                    }
                }

                // HEALTH BAR:
                szMult = 1 + ((tmpObj.maxHealth / 160) / 17);
                maxBarWidth = 70 * szMult;
                barPadding = 4;
                percentage = (tmpObj.health / tmpObj.maxHealth);
                barWidth = maxBarWidth * percentage;
                barHeight = (75 / 9);
                mainContext.fillStyle = darkColor;
                mainContext.roundRect(tmpX - (maxBarWidth / 2) - barPadding, tmpY + UIPadding + UIPadding - barPadding,
                    maxBarWidth + (barPadding * 2), barHeight + (barPadding * 2), 6).fill();
                mainContext.fillStyle = (tmpObj.team == player.team) ? "#78d545" : "#ED6B6B";
                mainContext.roundRect(tmpX - (maxBarWidth / 2), tmpY + UIPadding + UIPadding, barWidth, barHeight, 6).fill();
            }
        }

        if (iconsList[1]) {
            mainContext.drawImage(iconsList[1], 500, 500, skullIconSize, skullIconSize);
        }

        // DAY/NIGHT TIME:
        if (dayTimeValue < 0) {
            // NIGHT:
            mainContext.fillStyle = "rgba(0, 0, 0, " + (dayTimeValue * -1) + ")";
            mainContext.fillRect(0, 0, maxScreenWidth, maxScreenHeight);
        } else if (dayTimeValue > 0) {
            // DAY:
            mainContext.fillStyle = "rgba(255, 255, 255, " + dayTimeValue + ")";
            mainContext.fillRect(0, 0, maxScreenWidth, maxScreenHeight);
        }

        // UPDATE TEXTS:
        updateAnimTexts(delta);

        // CLEAR:
        delete tmpObj;
    }
}

// RENDER PLAYER:
function renderPlayer(contxt, tmpObj, tmpX, tmpY, tmpS, delta) {

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
            contxt.rotate(MathPI / 2);
            var cY = -(tmpObj.scatterCannons - 1) * ((tmpL / tmpObj.scatterCannons + 1) / 2);
            for (var c = 0; c < tmpObj.scatterCannons; ++c) {
                for (var c2 = 0; c2 < 2; ++c2) {
                    contxt.roundRect(cY + ((tmpL / tmpObj.scatterCannons) * c) - (tmpObj.cannonWidth / 2), -cW / 2.4, tmpObj.cannonWidth, cW / 2.4, 0, 1.3).stroke();
                    contxt.fill();
                    contxt.rotate(MathPI);
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
                contxt.rotate(-(tmpObj.rowRot * 2) - MathPI);
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
                tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + MathPI / 2;
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
                tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + MathPI / 2;
                for (var c = 0; c < tmpObj.bigCannon; ++c) {
                    contxt.save();
                    tmpVal = cY - ((tmpL / tmpMax) * c);
                    contxt.translate(0, tmpVal);
                    contxt.rotate(tmpDir - (MathPI / 2));
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
                tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + MathPI / 2;
                for (var c = 0; c < tmpObj.sniperCannon; ++c) {
                    contxt.save();
                    tmpVal = cY - ((tmpL / tmpMax) * c);
                    contxt.translate(0, tmpVal);
                    contxt.rotate(tmpDir - (MathPI / 2));
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
                tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + MathPI / 2;
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
                var rotPlus = MathPI;
                if (tmpObj.quadCannons) {
                    tmpCount = 4;
                    rotPlus /= 2;
                }
                tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + MathPI / 2;
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
                tmpDir = ((player.sid == tmpObj.sid) ? target : tmpObj.targetDir) - tmpObj.dir + MathPI / 2;
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
                    tmpDir = tmpObj.aimDir - tmpObj.dir + MathPI / 2;
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

// DRAW CIRCLE:
function drawCircle(x, y, s, ctxt) {
    ctxt.beginPath();
    ctxt.arc(x, y, s, 0, 2 * Math.PI);
    ctxt.stroke();
    ctxt.fill();
}

// RENDER GAME OBJECT:
var gameObjSprites = [];
function renderGameObject(tmpObj, ctxt) {
    var tmpIndx = (tmpObj.c + "-" + tmpObj.shp + "-" + tmpObj.s);
    var tmpSprt = gameObjSprites[tmpIndx];
    if (!tmpSprt) {
        var tmpCanvas = document.createElement("canvas");
        var tmpContext = tmpCanvas.getContext('2d');
        tmpCanvas.width = (tmpObj.s * 2) + 10;
        tmpCanvas.height = tmpCanvas.width;
        tmpContext.strokeStyle = darkColor;
        tmpContext.lineWidth = 8.5;
        tmpContext.translate(tmpCanvas.width / 2, tmpCanvas.height / 2);
        if (tmpObj.c == 0) {
            tmpContext.fillStyle = '#797979';
        } else if (tmpObj.c == 1) {
            tmpContext.fillStyle = '#e89360';
        } else if (tmpObj.c == 2) {
            tmpContext.fillStyle = '#c8c8c8';
        } else if (tmpObj.c == 3) {
            tmpContext.fillStyle = '#e9cd5f';
        } else if (tmpObj.c == 4) {
            tmpContext.fillStyle = '#EB6565';
        } else if (tmpObj.c == 5) {
            tmpContext.fillStyle = '#6FE8E2';
        } else if (tmpObj.c == 6) {
            tmpContext.fillStyle = '#7BE86F';
        }
        if (tmpObj.shp == 1) {
            var spikes = 6;
            var rot = MathPI / 2 * 3;
            var rad = tmpObj.s / 2;
            var step = MathPI / spikes;
            tmpContext.beginPath();
            tmpContext.moveTo(0, -rad);
            for (var s = 0; s < spikes; s++) {
                tmpContext.lineTo(MathCOS(rot) * rad, MathSIN(rot) * rad);
                rot += step;
                tmpContext.lineTo(MathCOS(rot) * (rad * 0.8), MathSIN(rot) * (rad * 0.8));
                rot += step;
            }
            tmpContext.lineTo(0, -rad);
            tmpContext.closePath();
            tmpContext.stroke();
            tmpContext.fill();
        } else if (tmpObj.shp == 2) {
            var rad = tmpObj.s / 1.6;
            tmpContext.beginPath();
            tmpContext.moveTo(0, -rad);
            tmpContext.lineTo(rad, 0);
            tmpContext.lineTo(0, rad);
            tmpContext.lineTo(-rad, 0);
            tmpContext.closePath();
            tmpContext.stroke();
            tmpContext.fill();
        } else if (tmpObj.shp == 3) {
            var rad = tmpObj.s / 1.6;
            tmpContext.beginPath();
            tmpContext.moveTo(0, -rad);
            tmpContext.lineTo(rad / 1.5, 0);
            tmpContext.lineTo(0, rad);
            tmpContext.lineTo(-rad / 1.5, 0);
            tmpContext.closePath();
            tmpContext.stroke();
            tmpContext.fill();
        } else {
            tmpContext.beginPath();
            tmpContext.arc(0, 0, tmpObj.s / 2, 0, 2 * Math.PI);
            tmpContext.stroke();
            tmpContext.fill();
        }
        gameObjSprites[tmpIndx] = tmpCanvas;
        tmpSprt = gameObjSprites[tmpIndx];
    }
    ctxt.drawImage(tmpSprt, -tmpSprt.width / 2, -tmpSprt.height / 2,
        tmpSprt.width, tmpSprt.height);
}

// DRAW ISLAND:
var islandInfo = [{
    sides: 17,
    color: "#e0cca7",
    offsets: [0.92, 0.95, 1, 1.05, 1, 0.85, 0.95, 1, 1.1, 1, 0.96]
}, {
    sides: 17,
    color: "#d4c19e",
    offsets: [1, 0.94, 1, 1.13, 0.98, 1.05, 1.1, 1, 0.96]
}, {
    sides: 17,
    color: "#c7b694",
    offsets: [1.05, 0.92, 1, 1.06, 1, 0.98, 1, 0.92]
}, {
    sides: 5,
    color: "#a4a4a4",
    offsets: [1.05, 0.92, 1, 1.06, 1, 0.98, 1, 0.92]
}];
var islandSprites = [];
var tmpIsl;
function drawIsland(x, y, s, indx, ctxt) {
    var tmpIndx = (s + "-" + indx);
    var tmpSprt = islandSprites[tmpIndx];
    if (!tmpSprt) {
        tmpIsl = islandInfo[indx];
        if (!tmpIsl) {
            tmpIsl = islandInfo[0];
        }
        var tmpCanvas = document.createElement("canvas");
        var tmpContext = tmpCanvas.getContext('2d');
        tmpCanvas.width = (s * 2) + (indx < 3 ? 300 : 10);
        tmpCanvas.height = tmpCanvas.width;
        tmpContext.fillStyle = tmpIsl.color;
        tmpContext.strokeStyle = darkColor;
        tmpContext.translate(tmpCanvas.width / 2, tmpCanvas.height / 2);
        var tmpOff = (s * tmpIsl.offsets[0]);
        tmpContext.beginPath();
        tmpContext.moveTo((tmpOff * MathCOS(0)), (tmpOff * MathSIN(0)));

        // ISLAND BASE:
        var offIndx = 0;
        for (var i = 1; i <= tmpIsl.sides - 1; i++) {
            offIndx++;
            if (offIndx >= tmpIsl.offsets.length - 1)
                offIndx = 0;
            var tmpOff = (s * tmpIsl.offsets[offIndx]);
            tmpContext.lineTo((tmpOff * MathCOS(i * 2 * MathPI / tmpIsl.sides)), (tmpOff * MathSIN(i * 2 * MathPI / tmpIsl.sides)));
        }
        tmpContext.closePath();
        if (indx < 3) {
            tmpContext.lineWidth = 300;
            tmpContext.globalAlpha = 0.1;
            tmpContext.stroke();
            tmpContext.lineWidth = 120;
            tmpContext.stroke();
        }
        tmpContext.lineWidth = 8.5;
        tmpContext.globalAlpha = 1;
        tmpContext.stroke();
        tmpContext.fill();

        // PALM TREES:


        islandSprites[tmpIndx] = tmpCanvas;
        tmpSprt = islandSprites[tmpIndx];
    }
    ctxt.drawImage(tmpSprt, x - tmpSprt.width / 2, y - tmpSprt.height / 2,
        tmpSprt.width, tmpSprt.height);
}

// UPDATE MENU:
function updateMenuLoop(delta) {
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

// SEND TARGET TO SERVER:
var sendFrequency = (10);
var tUpdateFrequency = (10);
var lastUpdated = 0;
var lastSent = 0;
function sendTarget(force) {
    var tmpTime = currentTime;
    if (player && !player.dead) {
        target = MathATAN2(mouseY - (screenHeight / 2), mouseX - (screenWidth / 2));
        if (force || tmpTime - lastUpdated > tUpdateFrequency) {
            if (controlIndex == 1) {
                targetD = Math.sqrt(MathPOW(mouseY - (screenHeight / 2), 2) + MathPOW(mouseX - screenWidth / 2, 2));
                targetD *= MathMIN(maxScreenWidth / screenWidth, maxScreenHeight / screenHeight);
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
function sendMoveTarget() {
    if (!keys.r && !keys.l)
        turnDir = 0;
    if (!keys.u && !keys.d)
        speedInc = 0;
    socket.emit('4', turnDir, speedInc);
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
function updateAnimTexts(delta) {

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
function animText() {
    this.x = 0;
    this.y = 0;
    this.alpha = 0;
    this.scale = 0;
    this.minScale = 0;
    this.maxScale = 0;
    this.scalePlus = 0;
    this.fadeDelay = 0;
    this.fadeSpeed = 0;
    this.text = "";
    this.active = false;
    this.update = function (delta) {
        if (this.active) {
            // UPDATE:
            this.scale += this.scalePlus * delta;
            if (this.scale >= this.maxScale) {
                this.scalePlus *= -1;
                this.scale = this.maxScale;
            } else if (this.scale <= this.minScale) {
                this.scalePlus = 0;
                this.scale = this.minScale;
            }
            this.fadeDelay -= delta;
            if (this.fadeDelay <= 0) {
                this.alpha -= this.fadeSpeed * delta;
                if (this.alpha <= 0) {
                    this.alpha = 0;
                    this.active = false;
                }
            }

            // DRAW:
            if (this.active) {
                mainContext.globalAlpha = this.alpha;
                mainContext.font = (this.scale * viewMult / 3) + "vh regularF";
                mainContext.strokeText(this.text, this.x, this.y);
                mainContext.fillText(this.text, this.x, this.y);
            }
        }
    };
    this.show = function (x, y, txt, scale, fadeDelay, sclPlus) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.minScale = scale;
        this.maxScale = scale * 1.35;
        this.scalePlus = sclPlus;
        this.text = txt || "";
        this.alpha = 1;
        this.fadeDelay = fadeDelay || 0;
        this.fadeSpeed = 0.003;
        this.active = true;
    };
};
function showAnimText(x, y, txt, scale, fadeDelay, type, sclPlus) {
    var tmpText = animTexts[animTextIndex];
    tmpText.show(x, y, txt, scale, fadeDelay, sclPlus);
    tmpText.type = type;
    animTextIndex++;
    if (animTextIndex >= animTexts.length)
        animTextIndex = 0;
};

// NOTIFICATIONS:
function hideNotifByType(type) {
    for (var i = 0; i < animTexts.length; ++i) {
        if (animTexts[i].type == type)
            animTexts[i].active = false;
    }
}
function showNotification(text) {
    for (var i = 0; i < animTexts.length; ++i) {
        if (animTexts[i].type == "notif")
            animTexts[i].active = false;
    }
    showAnimText(maxScreenWidth / 2, maxScreenHeight / 1.27, text, 42, 1500, "notif", 0.19);
}
function showBigNotification(text) {
    hideNotifByType("bNotif");
    showAnimText(maxScreenWidth / 2, screenHeight / 3, text, 130, 1000, "bNotif", 0.26);
}
function showScoreNotif(value) {
    hideNotifByType("sNotif");
    lastScore += value;
    showAnimText(maxScreenWidth / 2, maxScreenHeight / 1.34, ("+" + lastScore), 35, scoreDisplayTime, "sNotif", 0.16);
    scoreCountdown = scoreDisplayTime;
}

// SCREEN SHAKE:
var screenSkX = 0;
var screenShackeScale = 0;
var screenSkY = 0;
var screenSkRed = 0.5;
var screenSkDir = 0;
function screenShake(scl, dir) {
    if (screenShackeScale < scl) {
        screenShackeScale = scl;
        screenSkDir = dir;
    }
}
function updateScreenShake(delta) {
    if (screenShackeScale > 0) {
        screenSkX = screenShackeScale * MathCOS(screenSkDir);
        screenSkY = screenShackeScale * MathSIN(screenSkDir);
        screenShackeScale *= screenSkRed;
        if (screenShackeScale <= 0.1)
            screenShackeScale = 0;
    }
}

// KICK PLAYER:
var kickReason = null;
function kickPlayer(reason) {
    leaveGame();
    if (!kickReason)
        kickReason = reason;
    showMainMenuText(kickReason);
    socket.close();
    window.history.pushState("", "Doblons.io", "/");
}

// UPDATE OR PUSH PLAYER:
function updateOrPushUser(user) {
    var tmpIndx = getPlayerIndex(user.sid);
    if (tmpIndx != null) {
        users[tmpIndx] = user;
    } else {
        users.push(user);
    }
}

// CHECK IF PLAYER IS IN ARRAY:
function objectExists(obj) {
    for (var i = 0; i < users.length; ++i) {
        if (users[i].sid == obj.sid)
            return true;
    }
    return false;
}

// FIND PLAYER INDEX:
function getPlayerIndex(sid) {
    for (var i = 0; i < users.length; ++i) {
        if (users[i].sid == sid)
            return i;
    }
    return null;
}
function getPlayerIndexById(id) {
    for (var i = 0; i < users.length; ++i) {
        if (users[i].id == id)
            return i;
    }
    return null;
}

// SHOW A TEXT IN THE MENU:
function showMainMenuText(text) {
    userInfoContainer.style.display = "none";
    loadingContainer.style.display = "block";
    loadingContainer.innerHTML = text;
}
function hideMainMenuText() {
    userInfoContainer.style.display = "block";
    loadingContainer.style.display = "none";
}

// TOGGLE UI:
function toggleGameUI(visible) {
    var display = visible ? "block" : "none";
    gameUiContainer.style.display = display;
}
function toggleMenuUI(visible) {

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

// SECRET:
var treasureMap = "2B=TK:KAB,SSV:100K";

// RESIZE:
window.addEventListener('resize', resize);
var screenRatio, physicalH, physicalW;

function resize() {
    screenRatio = window.devicePixelRatio;
    physicalW = Math.round(window.screen.width * screenRatio);
    physicalH = Math.round(window.screen.height * screenRatio);

    screenWidth = physicalW;
    screenHeight = physicalH;
    mainCanvas.width = screenWidth;
    mainCanvas.height = screenHeight;

}
resize();

// GAME UPDATE LOOP:
var then = window.performance.now();
window.requestAnimFrame = (function () {
    return window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        window.oRequestAnimationFrame ||
        window.msRequestAnimationFrame ||
        function (callback, element) {
            window.setTimeout(callback, 1000 / targetFPS);
        };
})();
function callUpdate() {
    requestAnimFrame(callUpdate);
    currentTime = window.performance.now();
    var elapsed = currentTime - then;
    then = currentTime;
    updateGameLoop(elapsed);
    updateMenuLoop(elapsed);
};
callUpdate();


var app = {
    // Application Constructor
    initialize: function () {
        this.bindEvents();
    },
    // Bind any event listeners that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function () {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },

    onDeviceReady: function () {
        console.log('device ready')
        app.receivedEvent('deviceready');

        getStarted();

    },
    // Update DOM on a Received Event
    receivedEvent: function (id) {
    }
};

setTimeout(function () {
    initAds();
    document.addEventListener("pause", onPause, false);
    document.addEventListener("resume", onResume, false);
}, 500)

var flipOrientation = screen.orientation != "landscape-primary";

window.addEventListener("orientationchange", function () {
    flipOrientation = screen.orientation != "landscape-primary";
    // console.log("flipOrientation: " + flipOrientation);
});

/*
 function accelerometerSuccess(acceleration) {
 if (shootingMode != shootingModes.accel)
 return;

 if (calib == null)
 calib = acceleration;

 accel = acceleration;
 var x = /!*flipOrientation ? *!/calib.x - acceleration.x/!* : acceleration.x - calib.x*!/;
 var y = flipOrientation ? calib.y - acceleration.y : acceleration.y - calib.y;
 resetJoyStickVars();

 if (x > threshold && y > threshold) {
 keys.u = 1;
 keys.r = 1;
 } else if (x < threshold && y > threshold) {
 keys.d = 1;
 keys.r = 1;
 } else if (x > threshold && y < threshold) {
 keys.u = 1;
 keys.l = 1;
 } else if (x < threshold && y < threshold) {
 keys.d = 1;
 keys.l = 1;
 } else if (x > threshold) {
 keys.u = 1;
 } else if (x < threshold) {
 keys.d = 1;
 } else if (y > threshold) {
 keys.r = 1;
 } else if (y < threshold) {
 keys.l = 1;
 }
 }
 */

app.initialize();

var forceTarget = true;


function playSound(soundID, x, y) {
    /* if (!kicked && doSounds) {
     try {
     tmpDist = getDistance(player.x, player.y, x, y);
     if (tmpDist <= maxHearDist) {
     tmpSound = tmpList[soundID];
     if (tmpSound != undefined) {
     tmpSound = tmpSound.sound;
     tmpSound.volume(mathRound((1 - (tmpDist / maxHearDist)) * 10) / 10);
     tmpSound.play();
     }
     }
     } catch (e) {
     console.log(e);
     }
     }*/
}

function stopAllSounds() {
    /* if (!doSounds)
     return false;
     for (var i = 0; i < soundList.length; ++i) {
     tmpList[soundList[i].id].sound.stop();
     }*/
}
function pauseSounds() {
    /* if (!doSounds)
     return false;
     for (var i = 0; i < soundList.length; ++i) {
     if (tmpList[soundList[i].id].sound.loop)
     tmpList[soundList[i].id].sound.pause();
     }*/
}

function resumeSounds() {
    /* if (!doSounds)
     return false;
     for (var i = 0; i < soundList.length; ++i) {
     if (tmpList[soundList[i].id].sound.loop)
     tmpList[soundList[i].id].sound.play();
     }*/
}

Math.toDegrees = function (radians) {
    return radians * 180 / Math.PI;
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

function initAds() {
    //Todo fix all other platforms
    // document.addEventListener('onAdLoaded', function () {console.log("ad loaded")});
    // document.addEventListener('onAdLoadFailed', function (reason) {console.log("ad load failed: "+reason)});
    // document.addEventListener('onAdClosed', function () {console.log("ad closed")});
    // document.addEventListener('onAdOpened', function () {console.log("ad opened")});
    ShowAds.initInterstitialAd({adId: "ca-app-pub-6350309116730071/9878078741", isTest: true});
}

// var tmpGM = document.getElementById('gameModeText');

var lastAdShown = Date.now();

function showAd() {
    console.log("showAds")
    if (!paused && (1200 < (Date.now() - lastAdShown))) {
        lastAdShown = Date.now();
        ShowAds.showInterstitialAd();
    }
}

var paused = false;

function onPause() {
    pauseSounds();
    paused = true;
}

function onResume() {
    resumeSounds();
    paused = false;
}

var shouldSendData = 0;
var tmpDelta = 0;

function connectToServer(data) {
    try {
        data = JSON.parse(data);
    } catch (e) {
    }
    if (!socket) {
        // socket.handshake.query.apid = '19ytahhsb';
        socket = io.connect('http://' + data.ip + ':' + data.port, {
            forceNew: true,
            'connect timeout': 2500,
            reconnection: true,
            query: ('cid=' + cid + '&rmid=' + lobbyRoomID + '&apid=19ytahhsb')
        });
        setupSocket();
    }
}

function getStarted() {
    //Todo init game stuff here
    //Todo fix press and hold
    // boostDisplay.addEventListener('touchstart', function (event) {
    //
    //     target[2] = 1;
    //     sendTarget(true);
    //     try {
    //         event.preventDefault();
    //         event.stopPropagation();
    //     } catch (e) {
    //     }
    // });

}
//Todo remove this
// connectToServer({ip: 'localhost', port: "5000"});
// connectToServer({ip: '10.0.0.38', port: "5000"});
// connectToServer({ip: '10.0.0.69', port: "5000"});
// connectToServer({ip: '192.168.1.9', port: "5000"});
// connectToServer({ip: '192.168.43.161', port: "5000"});
// connectToServer({ip:'10.159.50.222'});

