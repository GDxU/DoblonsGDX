package tbs.doblon.io;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by linde on 11/7/2016.
 */

public class Player extends GameObject {
    float aimDir;
    int autoCannons, bigCannon;
    float cannonLength, cannonWidth, cannonSpeed,rowRot,rowSpeed;
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
    String lvl;
    int maxHealth, mineDropper;
    String name, nameSpriteID;
    int noseLength, ownerID, quadCannons, ramLength, rearCannon, rearLength;
    int rows, scatterCannons, sid, skin, sniperCannon, spawnProt, swivelCannons;
    float targetDir;
    String team;
    int trippleCannons, twinCannons, viewMult;
    boolean visible;
    final ArrayList<AnimMult> animMults = new ArrayList<AnimMult>();

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

    public Player(JSONObject player) {
        super();
        updateData(player);
    }

    public void updateData(JSONObject p) {
        if (p.has("aimDir"))
            aimDir = (float) p.getDouble("aimDir");
        if (p.has("autoCannons"))
            autoCannons = p.getInt("autoCannons");
        if (p.has("bigCannon"))
            bigCannon = p.getInt("bigCannon");
        if (p.has("cannonLength"))
            cannonLength = (float) p.getDouble("cannonLength");
        if (p.has("cannonWidth"))
            cannonWidth = (float) p.getDouble("cannonWidth");
        if (p.has("cannonSpeed"))
            cannonSpeed = (float) p.getDouble("");
        if (p.has("cannons"))
            cannons = p.getInt("cannons");
        if (p.has("chaseCannons"))
            chaseCannons = p.getInt("chaseCannons");
        if (p.has("classIndex"))
            classIndex = p.getInt("");
        if (p.has("dead"))
            dead = p.getBoolean("dead");
        if (p.has("dir"))
            dir = (float) p.getDouble("dir");
        if (p.has("cannonDmg"))
            cannonDmg = (float) p.getDouble("cannonDmg");
        if (p.has("speed"))
            speed = (float) p.getDouble("speed");
        if (p.has("turnSpeed"))
            turnSpeed = (float) p.getDouble("turnSpeed");
        if (p.has("crashDamage"))
            crashDamage = (float) p.getDouble("crashDamage");
        if (p.has("speedDiv"))
            speedDiv = (float) p.getDouble("speedDiv");
        if (p.has("reloadDiv"))
            reloadDiv = (float) p.getDouble("reloadDiv");
        if (p.has("healthRegen"))
            healthRegen = (float) p.getDouble("healthRegen");
        if (p.has("flashAlpha"))
            flashAlpha = (float) p.getDouble("flashAlpha");
        if (p.has("flashInc"))
            flashInc = (float) p.getDouble("flashInc");
        if (p.has("forcePos"))
            forcePos = p.getInt("forcePos");
        if (p.has("gatlinCannons"))
            gatlinCannons = p.getInt("gatlinCannons");
        if (p.has("health"))
            health = p.getInt("health");
        if (p.has("id"))
            id = p.getString("id");
        if (p.has("length"))
            length = (float) p.getDouble("length");
        if (p.has("localX"))
            localX = (float) p.getDouble("localX");
        if (p.has("localY"))
            localY = (float) p.getDouble("localY");
        if (p.has("lvl"))
            lvl = String.valueOf(p.getInt("lvl"));
        if (p.has("maxHealth"))
            maxHealth = p.getInt("maxHealth");
        if (p.has("mineDropper"))
            mineDropper = p.getInt("");
        if (p.has("name"))
            name = p.getString("name");

        if (p.has("nameSpriteID"))
            nameSpriteID = p.getString("nameSpriteID");
        if (p.has("noseLength"))
            noseLength = p.getInt("");
        if (p.has("ownerID"))
            ownerID = p.getInt("ownerID");
        if (p.has("quadCannons"))
            quadCannons = p.getInt("quadCannons");
        if (p.has("ramLength"))
            ramLength = p.getInt("ramLength");
        if (p.has("rearCannon"))
            rearCannon = p.getInt("rearCannon");
        if (p.has("rearLength"))
            rearLength = p.getInt("rearLength");
        if (p.has("rows"))
            rows = p.getInt("rows");
        if (p.has("scatterCannons"))
            scatterCannons = p.getInt("scatterCannons");
        if (p.has("sid"))
            sid = p.getInt("sid");
        if (p.has("skin"))
            skin = p.getInt("skin");
        if (p.has("sniperCannon"))
            sniperCannon = p.getInt("sniperCannon");
        if (p.has("spawnProt"))
            spawnProt = p.getInt("spawnProt");
        if (p.has("swivelCannons"))
            swivelCannons = p.getInt("swivelCannons");
        if (p.has("targetDir"))
            targetDir = (float) p.getDouble("targetDir");
        if (p.has("team"))
            team = p.getString("team");
        if (p.has("trippleCannons"))
            trippleCannons = p.getInt("trippleCannons");
        if (p.has("twinCannons"))
            twinCannons = p.getInt("twinCannons");
        if (p.has("viewMult"))
            viewMult = p.getInt("viewMult");
        if (p.has("visible"))
            visible = p.getBoolean("visible");
    }

    @Override
    public void draw() {

    }

    @Override
    public void update(float delta) {

    }
}
