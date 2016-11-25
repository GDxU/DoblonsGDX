package tbs.doblon.io;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by linde on 11/8/2016.
 */

public class AnimText extends GameObject {
    public float x;
    public float y;
    public float alpha;
    public float scale;
    public float minScale;
    public float maxScale;
    public float scalePlus;
    public float fadeDelay;
    public String type;
    public float fadeSpeed;
    public String text;

    boolean active = false;

    @Override
    public void draw() {
        // DRAW:
        if (this.active) {
            final Color color = Utility.tmpColor;
            color.set(Color.WHITE);
            color.a = alpha;
            Utility.drawCenteredText(Game.spriteBatch(), text, color, x, y, scale);
        }
    }

    @Override
    public void update(float delta) {
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
        }
    }

    public void show(float x, float y, String txt, float scale, float fadeDelay, float sclPlus) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.minScale = scale;
        this.maxScale = scale * 1.35f;
        this.scalePlus = sclPlus;
        this.text = txt;
        this.alpha = 1;
        this.fadeDelay = fadeDelay;
        this.fadeSpeed = 0.003f;
        this.active = true;
    }
}
