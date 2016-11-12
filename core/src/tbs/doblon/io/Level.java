package tbs.doblon.io;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

import static tbs.doblon.io.Game.darkColor;
import static tbs.doblon.io.Game.dayTimeValue;
import static tbs.doblon.io.Game.gameData;
import static tbs.doblon.io.Game.maxScreenHeight;
import static tbs.doblon.io.Game.maxScreenWidth;
import static tbs.doblon.io.Game.player;
import static tbs.doblon.io.Game.screenSkX;
import static tbs.doblon.io.Game.screenSkY;
import static tbs.doblon.io.Game.skullIconSize;
import static tbs.doblon.io.Game.users;
import static tbs.doblon.io.GameBase.fill;
import static tbs.doblon.io.GameBase.shapeRenderer;

/**
 * Created by mike on 3/4/16.
 */
public class Level {
    public static ArrayList<Player> players = new ArrayList<Player>();

    public static void update(float delta){
        for (Player player : players) {
            player.update(Game.delta);
        }

        // IF PLAYER IS SET:

            // INTERPOLATION & VISIBILITY:
            Player tmpObj;
            for (int i = 0; i < users.size(); ++i) {
                tmpObj = users.get(i);
                if (tmpObj.visible && !tmpObj.dead) {
                    if (tmpObj.forcePos!=0) {
                        tmpObj.localX = tmpObj.x;
                        tmpObj.localY = tmpObj.y;
                        tmpObj.forcePos = 0;
                    } else {
                        float difference = tmpObj.x - tmpObj.localX;
                        tmpObj.localX += (difference * delta * 0.0175);
                        difference = tmpObj.y - tmpObj.localY;
                        tmpObj.localY += (difference * delta * 0.0175);
                    }
                }
            }

            // SET OFFSET:
            tmpObj = users.get(Game.getPlayerIndex(player.sid));
            Player tmpPlayer = tmpObj;
            float xOffset =0, yOffset =0;
            if (tmpObj!=null) {
                xOffset = tmpObj.localX;
                yOffset = tmpObj.localY;
            }
            float camX = xOffset - (maxScreenWidth / 2) - screenSkX;
            float camY = yOffset - (maxScreenHeight / 2) - screenSkY;
            float tmpX, tmpY;

            // RENDER MAP:
            if (gameData!=null) {
                final ShapeRenderer renderer = shapeRenderer(fill);
                renderer.setColor(gameData.outerColor);
                // OUTSIDE WALLS:
                //Todo check mainContext.lineWidth = 7;
              
                renderer.rect(0, 0, maxScreenWidth, maxScreenHeight);
                renderer.setColor(gameData.waterColor);
                Game.roundRect(Math.max(-7, -gameData.mapScale - camX), Math.max(-7, -gameData.mapScale - camY),
                        Math.min(maxScreenWidth + 14, gameData.mapScale - camX + 7), Math.min(maxScreenHeight + 14, gameData.mapScale - camY + 7), 0).fill();
                mainContext.stroke();

                // GRID:
                mainContext.lineWidth = 5;
                mainContext.strokeStyle = darkColor;
                mainContext.globalAlpha = 0.18;
                mainContext.beginPath();
                int grdX = -camX - gameData.mapScale - (1920 / 2);
                for (int x = grdX; x < maxScreenWidth; x += 40) {
                    mainContext.moveTo(x, 0);
                    mainContext.lineTo(x, maxScreenHeight);
                }
                int grdY = -camY - gameData.mapScale - (1920 / 2);
                for (int y = grdY; y < maxScreenHeight; y += 40) {
                    mainContext.moveTo(0, y);
                    mainContext.lineTo(maxScreenWidth, y);
                }
                mainContext.stroke();
                mainContext.globalAlpha = 1;

                // RENDER ISLANDS:
                if (gameData.islands) {
                    var tmpIsl;
                    for (int i = 0; i < gameData.islands.length; ++i) {
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
            for (int i = 0; i < gameObjects.length; ++i) {
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
            for (int i = 0; i < users.length; ++i) {
                tmpObj = users[i];
                if (tmpObj.visible && !tmpObj.dead) {
                    tmpX = tmpObj.localX - camX;
                    tmpY = tmpObj.localY - camY;
                    tmpS = userSkins[tmpObj.skin || 0];
                    if (!tmpS)
                        tmpS = userSkins[0];

                    // CANNON ANIMATIONS:
                    if (tmpObj.animMults) {
                        for (int a = 0; a < tmpObj.animMults.length; ++a) {
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
                    mainContext.rotate(tmpObj.dir - (Math.PI / 2));
                    mainContext.drawImage(playerCanvas, -(playerCanvas.width / 2), -(playerCanvas.height / 2));
                    mainContext.restore();
                    mainContext.globalAlpha = 1;
                    mainContext.globalCompositeOperation = "source-over";
                }
            }

            // PLAYER UI:
            float UIPadding, maxBarWidth, barPadding,
                    percentage, barWidth, barHeight, szMult, tmpSID;
        final  ShapeRenderer renderer = shapeRenderer(fill);
            for (int i = 0; i < users.size(); ++i) {
                tmpObj = users.get(i);
                if (tmpObj.visible && !tmpObj.dead) {
                    tmpX = tmpObj.localX - camX;
                    tmpY = tmpObj.localY - camY;
                    szMult = (1 + (tmpObj.length / 270));
                    UIPadding = (tmpObj.length / 3.4f);

                    // RENDER NAME UI TO SPRITE:
                    if (tmpObj.name) {
                        tmpSID = (tmpObj.name + "-" + tmpObj.lvl + "-" + szMult);
                        if (tmpObj.nameSpriteID != tmpSID) {

                            float nameTextSize = 25 * szMult;
                            renderer.font = nameTextSize + "px regularF";
                            float nameMeasure = renderer.measureText(tmpObj.name);
                            renderer.font = (nameTextSize * 1.3) + "px regularF";
                            var lvlMeasure = renderer.measureText(tmpObj.lvl ? tmpObj.lvl + "" : "");
                            renderer.font = nameTextSize + "px regularF";
                            tmpCanvas.width = (nameMeasure.width + (lvlMeasure.width * 2)) + 20;
                            tmpCanvas.height = (nameTextSize * 2);
                            renderer.translate(tmpCanvas.width / 2, tmpCanvas.height / 2);
                            renderer.font = nameTextSize + "px regularF";
                            renderer.fillStyle = '#ffffff';
                            renderer.strokeStyle = darkColor;
                            renderer.lineWidth = 6.5;
                            renderer.textAlign = "center";
                            if (tmpCanvas.width <= 600) {
                                renderer.strokeText(tmpObj.name, 0, 0);
                                renderer.fillText(tmpObj.name, 0, 0);
                                if (tmpObj.lvl) {
                                    renderer.font = (nameTextSize * 1.3) + "px regularF";
                                    var tmpLvlX = -(nameMeasure.width / 2) - (10 + (lvlMeasure.width / 2));
                                    renderer.strokeStyle = darkColor;
                                    renderer.strokeText(tmpObj.lvl, tmpLvlX, 0);
                                    renderer.fillText(tmpObj.lvl, tmpLvlX, 0);
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
                    renderer.setColor(Utility.tmpColor.set(darkColor));
                    Game.roundRect(tmpX - (maxBarWidth / 2) - barPadding, tmpY + UIPadding + UIPadding - barPadding,
                            maxBarWidth + (barPadding * 2), barHeight + (barPadding * 2), 6,0);
                    renderer.setColor(Utility.tmpColor.set((tmpObj.team == player.team) ? 0x78d545: 0xED6B6B));
                    Game.roundRect(tmpX - (maxBarWidth / 2), tmpY + UIPadding + UIPadding, barWidth, barHeight, 6,0);
                }
            }

            if (iconsList[1]) {
                mainContext.drawImage(iconsList[1], 500, 500, skullIconSize, skullIconSize);
            }

            // DAY/NIGHT TIME:
            if (dayTimeValue < 0) {
                // NIGHT:
                renderer.setColor(Utility.tmpColor.set(0,0,0,(dayTimeValue * -1)));
                renderer.rect(0, 0, maxScreenWidth, maxScreenHeight);
            } else if (dayTimeValue > 0) {
                // DAY:
                renderer.setColor(Utility.tmpColor.set(1,1,1,(dayTimeValue )));
                renderer.rect(0, 0, maxScreenWidth, maxScreenHeight);
            }

            // UPDATE TEXTS:
            Game.updateAnimTexts(delta);

    }
}
