
String partyKey = null;
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

// WEAPON POPUP:
var activePopup;
public static void showWeaponPopup(indx) {
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


// SKINS:
var skinIndex = 0;
if (hasStorage) {
    skinIndex = parseInt(localStorage.getItem("sknInx")) || 0;
}

var renderedSkins = [];
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
        Utility.saveString("sknInx", skinIndex);
    }
}
changeSkin(0);
$('#skinSelector').bind("contextmenu", public static void (e) {
    changeSkin(-1);
    return false;
});
if (hasStorage) {
    if (localStorage.getItem("isFollDob")) {
        unlockSkins(0);
    }
}
public static void unlockSkins(indx) {
    if (!indx) {
        skinInfo.style.display = "inline-block";
        skinSelector.style.display = "inline-block";
        if (hasStorage) {
            Utility.saveString("isFollDob", 1);
        }
    }
}

// UPDATE THE GAME:
var playerContext = playerCanvas.getContext('2d');
var updateGameLoop = public static void (delta) {

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
                        var shapeRenderer(fill) = tmpCanvas.getContext('2d');
                        var nameTextSize = 25 * szMult;
                        shapeRenderer(fill).font = nameTextSize + "px regularF";
                        var nameMeasure = shapeRenderer(fill).measureText(tmpObj.name);
                        shapeRenderer(fill).font = (nameTextSize * 1.3) + "px regularF";
                        var lvlMeasure = shapeRenderer(fill).measureText(tmpObj.lvl ? tmpObj.lvl + "" : "");
                        shapeRenderer(fill).font = nameTextSize + "px regularF";
                        tmpCanvas.width = (nameMeasure.width + (lvlMeasure.width * 2)) + 20;
                        tmpCanvas.height = (nameTextSize * 2);
                        shapeRenderer(fill).translate(tmpCanvas.width / 2, tmpCanvas.height / 2);
                        shapeRenderer(fill).font = nameTextSize + "px regularF";
                        shapeRenderer(fill).fillStyle = '#ffffff';
                        shapeRenderer(fill).strokeStyle = darkColor;
                        shapeRenderer(fill).lineWidth = 6.5;
                        shapeRenderer(fill).textAlign = "center";
                        if (tmpCanvas.width <= 600) {
                            shapeRenderer(fill).strokeText(tmpObj.name, 0, 0);
                            shapeRenderer(fill).fillText(tmpObj.name, 0, 0);
                            if (tmpObj.lvl) {
                                shapeRenderer(fill).font = (nameTextSize * 1.3) + "px regularF";
                                var tmpLvlX = -(nameMeasure.width / 2) - (10 + (lvlMeasure.width / 2));
                                shapeRenderer(fill).strokeStyle = darkColor;
                                shapeRenderer(fill).strokeText(tmpObj.lvl, tmpLvlX, 0);
                                shapeRenderer(fill).fillText(tmpObj.lvl, tmpLvlX, 0);
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
public static void renderPlayer(contxt, tmpObj, tmpX, tmpY, tmpS, delta) {

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
public static void drawCircle(x, y, s, ctxt) {
    ctxt.beginPath();
    ctxt.arc(x, y, s, 0, 2 * Math.PI);
    ctxt.stroke();
    ctxt.fill();
}

// RENDER GAME OBJECT:
var gameObjSprites = [];
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
            var rot = MathPI / 2 * 3;
            var rad = tmpObj.s / 2;
            var step = MathPI / spikes;
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
public static void drawIsland(x, y, s, indx, ctxt) {
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
            shapeRenderer(fill).lineTo((tmpOff * MathCOS(i * 2 * MathPI / tmpIsl.sides)), (tmpOff * MathSIN(i * 2 * MathPI / tmpIsl.sides)));
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

// SEND TARGET TO SERVER:
var sendFrequency = (10);
var tUpdateFrequency = (10);
var lastUpdated = 0;
var lastSent = 0;
public static void sendTarget(force) {
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
public static void sendMoveTarget() {
    if (!keys.r && !keys.l)
        turnDir = 0;
    if (!keys.u && !keys.d)
        speedInc = 0;
    socket.emit('4', turnDir, speedInc);
}

// KICK PLAYER:
var kickReason = null;
public static void kickPlayer(reason) {
    leaveGame();
    if (!kickReason)
        kickReason = reason;
    showMainMenuText(kickReason);
    socket.close();
    window.history.pushState("", "Doblons.io", "/");
}

// UPDATE OR PUSH PLAYER:
public static void updateOrPushUser(user) {
    var tmpIndx = getPlayerIndex(user.sid);
    if (tmpIndx != null) {
        users[tmpIndx] = user;
    } else {
        users.push(user);
    }
}
