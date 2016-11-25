package tbs.doblon.io.views;

/**
 * Created by Michael on 3/10/2015.
 */
public class Rect {
    private static final Rect rect1 = new Rect();
    private static boolean b;
    public float x, y, w, h;

    public Rect() {
    }

    public Rect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void set(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void set(Rect r) {
        x = r.x;
        y = r.y;
        w = r.w;
        h = r.h;
    }

    public boolean contains(float x, float y) {
        return (x > this.x && y > this.y) && (x < this.x + w && y < this.y + h);
    }

    public boolean contains(float x, float y, float w, float h) {
        rect1.set(x, y, w, h);
        return contains(rect1);
    }

    public boolean contains(Rect r) {
        return (x < r.x + r.w && x + w > r.x && y < r.y + r.h && y + h > r.y);
    }

    @Override
    public String toString() {
        return String.format("x : %.0f, y : %.0f | w : %.0f, h : %.0f", x, y, w, h);
    }
}
