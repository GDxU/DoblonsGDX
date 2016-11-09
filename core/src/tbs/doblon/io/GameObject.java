package tbs.doblon.io;

/**
 * Created by linde on 11/8/2016.
 */

public abstract class GameObject {
    public GameObject() {
    }

    public GameObject(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public float x, y, w, h;

    public abstract void draw();

    public abstract void update(float delta);
}
