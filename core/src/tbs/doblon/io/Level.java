package tbs.doblon.io;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import java.util.ArrayList;

import static tbs.doblon.io.Game.darkColor;
import static tbs.doblon.io.Game.dayTimeValue;
import static tbs.doblon.io.Game.drawIsland;
import static tbs.doblon.io.Game.gameData;
import static tbs.doblon.io.Game.gameObjects;
import static tbs.doblon.io.Game.maxFlashAlpha;
import static tbs.doblon.io.Game.maxScreenHeight;
import static tbs.doblon.io.Game.maxScreenWidth;
import static tbs.doblon.io.Game.player;
import static tbs.doblon.io.Game.screenSkX;
import static tbs.doblon.io.Game.screenSkY;
import static tbs.doblon.io.Game.skullIconSize;
import static tbs.doblon.io.Game.userNameInput;
import static tbs.doblon.io.Game.userSkins;
import static tbs.doblon.io.Game.users;
import static tbs.doblon.io.GameBase.fill;
import static tbs.doblon.io.GameBase.shapeRenderer;
import static tbs.doblon.io.GameBase.spriteBatch;

/**
 * Created by mike on 3/4/16.
 */
public class Level {
    public static ArrayList<Player> players = new ArrayList<Player>();

    public static void update(float delta) {
        if (users.size()<1){
//            Utility.log("no users");
            return;
        }

        for (Player player : players) {
            player.update(Game.delta);
        }

        // IF PLAYER IS SET:

        // INTERPOLATION & VISIBILITY:
        for (int i = 0; i < users.size(); ++i) {
            Player tmpObj = users.get(i);
            if (tmpObj.visible && !tmpObj.dead) {
                if (tmpObj.forcePos != 0) {
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

        ShapeRenderer renderer = shapeRenderer(fill);
        // SET OFFSET:
        //Todo
//        if (true)
//            return;

        Player tmpPlayer = users.get(Game.getPlayerIndex(player.sid));
        float xOffset = 0, yOffset = 0;
        if (tmpPlayer != null) {
            xOffset = tmpPlayer.localX;
            yOffset = tmpPlayer.localY;
        }
        float camX = xOffset - (maxScreenWidth / 2) - screenSkX;
        float camY = yOffset - (maxScreenHeight / 2) - screenSkY;
        float tmpX, tmpY;

        // RENDER MAP:
        if (gameData != null) {
            renderer = shapeRenderer(fill);
            renderer.setColor(Utility.tmpColor.set(gameData.outerColor));
            // OUTSIDE WALLS:
            //Todo check mainContext.lineWidth = 7;

            renderer.rect(0, 0, maxScreenWidth, maxScreenHeight);
            renderer = shapeRenderer(ShapeRenderer.ShapeType.Line);
            renderer.setColor(Utility.tmpColor.set(gameData.waterColor));
            Game.roundRect(Math.max(-7, -gameData.mapScale - camX), Math.max(-7, -gameData.mapScale - camY),
                    Math.min(maxScreenWidth + 14, gameData.mapScale - camX + 7), Math.min(maxScreenHeight + 14, gameData.mapScale - camY + 7), 0);
//                mainContext.stroke();

            // GRID:
//             Todo   mainContext.lineWidth = 5;
            renderer.setColor(Utility.tmpColor.set(darkColor));
            Utility.tmpColor.a = 0.18f;
            int grdX = (int) (-camX - gameData.mapScale - (1920 / 2));
            for (int x = grdX; x < maxScreenWidth; x += 40) {
                renderer.line(x, 0, x, maxScreenHeight);
            }
            int grdY = (int) (-camY - gameData.mapScale - (1920 / 2));
            for (int y = grdY; y < maxScreenHeight; y += 40) {
                renderer.line(0, y, maxScreenWidth, y);
            }
            Utility.tmpColor.a = 1;

            // RENDER ISLANDS:
            if (gameData.islands.size() > 0) {
                Island tmpIsl;
                for (int i = 0; i < gameData.islands.size(); ++i) {
                    tmpIsl = gameData.islands.get(i);
                    tmpX = tmpIsl.x - camX;
                    tmpY = tmpIsl.y - camY;
                    if (tmpX + tmpIsl.s + 125 >= 0 && tmpY + tmpIsl.s + 125 >= 0
                            && tmpX - tmpIsl.s - 125 <= maxScreenWidth && tmpY - tmpIsl.s - 125 <= maxScreenHeight) {
                        drawIsland(tmpX, tmpY, tmpIsl.s, tmpIsl.i);
                    }
                }
                tmpIsl = null;
            }
        }

        // RENDER GAMEOBJECTS:
//         todo   mainContext.lineWidth = 8.5;
        for (int i = 0; i < gameObjects.size(); ++i) {
            Obstacle tmpObj = gameObjects.get(i);
            if (tmpObj.active) {
                tmpObj.x += (tmpObj.xS * (delta / 1000));
                tmpObj.y += (tmpObj.yS * (delta / 1000));
                tmpX = tmpObj.x - camX;
                tmpY = tmpObj.y - camY;
                if (tmpX + tmpObj.s >= 0 && tmpY + tmpObj.s >= 0
                        && tmpX - tmpObj.s <= maxScreenWidth && tmpY - tmpObj.s <= maxScreenHeight) {
//                     todo   mainContext.translate(tmpX, tmpY);
                    Game.renderGameObject(tmpObj);
                }
            }
        }

        // RENDER PLAYERS:
        Skin tmpS;
        for (int i = 0; i < users.size(); ++i) {
            final Player tmpObj = users.get(i);
            if (tmpObj.visible && !tmpObj.dead) {
                tmpX = tmpObj.localX - camX;
                tmpY = tmpObj.localY - camY;
                tmpS = userSkins.get(tmpObj.skin);
                if (tmpS != null)
                    tmpS = userSkins.get(0);

                // CANNON ANIMATIONS:
                if (tmpObj.animMults != null) {
                    for (int a = 0; a < tmpObj.animMults.size(); ++a) {
                        if (tmpObj.animMults.get(a).plus != 0) {
                            tmpObj.animMults.get(a).mult += tmpObj.animMults.get(a).plus;
                            if (tmpObj.animMults.get(a).mult >= 1) {
                                tmpObj.animMults.get(a).mult = 1;
                                tmpObj.animMults.get(a).plus = 0;
                            }
                            if (tmpObj.animMults.get(a).mult < 0.8f) {
                                tmpObj.animMults.get(a).mult = 0.8f;
                                tmpObj.animMults.get(a).plus *= -1;
                            }
                        }
                    }
                }

                // RENDER PLAYER:
                Game.renderPlayer(tmpObj, tmpX, tmpY, tmpS, delta);

                // SPAWN PROT:
                if (tmpObj.spawnProt > 0) {
                    tmpObj.flashAlpha = maxFlashAlpha;
                    tmpObj.flashInc = 0.0005f;

                    tmpObj.flashAlpha += (tmpObj.flashInc * delta);
                    if (tmpObj.flashAlpha > maxFlashAlpha) {
                        tmpObj.flashAlpha = maxFlashAlpha;
                        tmpObj.flashInc *= -1;
                    } else if (tmpObj.flashAlpha <= 0) {
                        tmpObj.flashAlpha = 0;
                        tmpObj.flashInc *= -1;
                    }
                    renderer.setColor(Utility.tmpColor.set(1, 1, 1, tmpObj.flashAlpha));
                    renderer.rect(-player.w / 2, -player.h / 2, player.w,
                            player.h);
                }
//                    playerContext.translate(-(playerCanvas.width / 2), -(playerCanvas.height / 2));

                // EFFECTS:

                // ACTUAL RENDER:
//   Todo                 spriteBatch().draw();
//                    mainContext.rotate(tmpObj.dir - (Math.PI / 2));
            }
        }

        // PLAYER UI:
        float UIPadding, maxBarWidth, barPadding,
                percentage, barWidth, barHeight, szMult;
        String tmpSID;
        renderer = shapeRenderer(fill);
        for (int i = 0; i < users.size(); ++i) {
            Player tmpObj = users.get(i);
            if (tmpObj.visible && !tmpObj.dead) {
                tmpX = tmpObj.localX - camX;
                tmpY = tmpObj.localY - camY;
                szMult = (1 + (tmpObj.length / 270));
                UIPadding = (tmpObj.length / 3.4f);

                // RENDER NAME UI TO SPRITE:
                if (tmpObj.name != null) {
                    tmpSID = (tmpObj.name + "-" + tmpObj.lvl + "-" + szMult);
                    if (tmpObj.nameSpriteID != tmpSID) {

                        float nameTextSize = 25 * szMult;

//                            renderer.strokeText(tmpObj.name, 0, 0);
                        Utility.drawCenteredText(spriteBatch(), 0xffffff,tmpObj.name, tmpObj.x, tmpObj.y, nameTextSize);
                            if (tmpObj.lvl != null) {
//                                    renderer.strokeStyle = darkColor;
//                                    renderer.strokeText(tmpObj.lvl, tmpLvlX, 0);
                                Utility.drawCenteredText(spriteBatch(), 0xffffff, tmpObj.lvl, tmpObj.x, tmpObj.y, nameTextSize * 1.3f);
                            }
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
                        maxBarWidth + (barPadding * 2), barHeight + (barPadding * 2), 6);
                renderer.setColor(Utility.tmpColor.set((tmpObj.team == player.team) ? 0x78d545 : 0xED6B6B));
                Game.roundRect(tmpX - (maxBarWidth / 2), tmpY + UIPadding + UIPadding, barWidth, barHeight, 6);
            }
        }

        if (Game.iconsList[1] != null) {
            spriteBatch().draw(Game.iconsList[1], 500, 500, skullIconSize, skullIconSize);
        }

        // DAY/NIGHT TIME:
        if (dayTimeValue !=null) {
            // NIGHT:
//todo            renderer.setColor(Utility.tmpColor.set(1, 1, 1, (dayTimeValue)));
//            renderer.rect(0, 0, maxScreenWidth, maxScreenHeight);
        }

        // UPDATE TEXTS:
        Game.updateAnimTexts(delta);

    }
}
