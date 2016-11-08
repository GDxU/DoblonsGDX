package tbs.doblon.io;

/**
 * Created by linde on 11/8/2016.
 */

public class AnimText {
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
    this.update = public static void (delta) {
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
    this.show = public static void (x, y, txt, scale, fadeDelay, sclPlus) {
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
}
