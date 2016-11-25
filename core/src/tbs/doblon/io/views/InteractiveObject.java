package tbs.doblon.io.views;


/**
 * Created by Michael on 2/1/2015.
 */
public interface InteractiveObject {
    Rect rect = new Rect();
    Rect rect2 = new Rect();

    boolean click(int xPos, int yPos);

    boolean longClick(int xPos, int yPos);

    boolean drag(float startX, float startY, float dx, float dy);

    boolean fling(float vx, float vy);
}
