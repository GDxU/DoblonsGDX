package tbs.doblon.io;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by linde on 11/7/2016.
 */

public class Player extends GameObject {
    float aimDir;
    int autoCannons, bigCannon;
    float cannonLength, cannonWidth,cannonSpeed;
    int cannons, chaseCannons, classIndex;
    boolean dead;
    float dir;
    float cannonDmg;
    float speed;
    float turnSpeed;
    float crashDamage;
    float speedDiv;
    float reloadDiv;
    float healthRegen;
    float flashAlpha, flashInc;
    int forcePos, gatlinCannons, health;
    String id;
    float length;
    float localX, localY;
    int lvl, maxHealth, mineDropper;
    String name, nameSprite, nameSpriteID;
    int noseLength, ownerID, quadCannons, ramLength, rearCannon, rearLength;
    int rows, scatterCannons, sid, skin, sniperCannon, spawnProt, swivelCannons;
    float targetDir;
    String team;
    int trippleCannons, twinCannons, viewMult;
    boolean visible;
    final ArrayList<AnimMult> animMults =new ArrayList<AnimMult>();

    public void parse(String player) {
        //Todo
    }

    public Player() {
        super();
    }

    public Player(String player) {
        super();
        parse(player);
    }

    @Override
    public void draw() {

    }

    @Override
    public void update(float delta) {

    }
}
