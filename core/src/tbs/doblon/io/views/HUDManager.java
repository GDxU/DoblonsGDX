package tbs.doblon.io.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import java.util.ArrayList;

import tbs.doblon.io.Utility;


/**
 * Created by Michael on 2/20/2015.
 */
public class HUDManager implements InteractiveObject, Viewable {
    private static final ArrayList<View> views = new ArrayList<View>();
    public static OrthographicCamera camera;
    public static boolean continueCheckingClicks, continueCheckingForFling, continueChceckingForLongClicks;
    public static int w, h;
    private static HUDManager hudManager;
    private static Matrix4 proj;
    private static ShapeRenderer shapeRenderer;
    private static SpriteBatch spriteBatch;
    private static boolean continueCheckingForDrag;

    private HUDManager() {
        if (hudManager == null) {
            try {
                w = Gdx.graphics.getWidth();
                h = Gdx.graphics.getHeight();
                camera = new OrthographicCamera(w, h);
//                UniversalClickListener.getUniversalClickListener();
                camera.setToOrtho(false, camera.viewportWidth, camera.viewportHeight);
                camera.position.x = camera.viewportWidth / 2;
                camera.position.y = camera.viewportHeight / 2;
                camera.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isContinueCheckingForDrag() {
        return continueCheckingForDrag;
    }

    public static void setContinueCheckingForDrag(boolean continueCheckingForDrag) {
        HUDManager.continueCheckingForDrag = continueCheckingForDrag;
    }

    public static ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public static SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public static void addView(View view) {
        if (hudManager == null)
            getHUDManager();

        if (!views.contains(view))
            views.add(view);
    }

    public static int getViewIndex(View view) {
        return views.indexOf(view);
    }

    public static void removeView(View view) {
        if (views.contains(view))
            views.remove(view);
    }

    public static void removeViewByID(int iD) {
        for (int i = 0; i < views.size() - 1; i++) {
            final View v = views.get(i);
            if (v.getID() == iD)
                views.remove(v);
        }
    }

    public static void removeViewByTag(Object tag) {
        for (int i = 0; i < views.size() - 1; i++) {
            final View v = views.get(i);
            if (tag.equals(v.getTag()))
                views.remove(v);
        }
    }

    public static boolean isContinueCheckingForFling() {
        return continueCheckingForFling;
    }

    public static void setContinueCheckingForFling(boolean continueCheckingForFling) {
        HUDManager.continueCheckingForFling = continueCheckingForFling;
    }

    public static void print(String str) {
        System.out.println(str);
    }

    public static boolean isContinueCheckingClicks() {
        return continueCheckingClicks;
    }

    public static void setContinueCheckingClicks(boolean continueCheckingClicks) {
        HUDManager.continueCheckingClicks = continueCheckingClicks;
    }

    public static HUDManager getHUDManager() {
        if (hudManager == null)
            hudManager = new HUDManager();
        return hudManager;
    }

    @Override
    public void draw(float relX, float relY, float parentRight, float parentTop) {

    }

    @Override
    public boolean click(int xPos, int yPos) {
        continueCheckingClicks = true;

//        switch (touchType) {
//            case CLICK:


        for (int i = (views.size() - 1); i >= 0; i--) {
            final View view = views.get(i);
            if (view.click(xPos, yPos)) {
                Utility.log("checkClik > " + xPos + ", " + yPos + " | viewNum: " + views.size());
                continueCheckingClicks = false;
                return true;
            } else {
                Utility.log("failed > " + view.toString() + " | " + xPos + ", " + yPos);
            }
        }
//                break;
//            case TOUCH_DOWN:
//                setTouchDown(xPos, yPos);
//                break;
//            case TOUCH_UP:
//                setTouchUp();
//                break;
//        }
        return false;
    }

    @Override
    public boolean longClick(int xPos, int yPos) {
        continueChceckingForLongClicks = true;
        for (int i = (views.size() - 1); i >= 0; i--) {
            if (!continueChceckingForLongClicks) {
                return true;
            }
            final View view = views.get(i);
            if (view.longClick(xPos, yPos)) {
                continueChceckingForLongClicks = false;
            }
        }
        return false;
    }

    @Override
    public boolean fling(float vx, float vy) {
        continueCheckingForFling = true;
        for (int i = views.size() - 1; i >= 0; i--) {
            if (continueCheckingClicks)
                if (views.get(i).fling(vx, vy))
                    return true;
        }
        return false;
    }


    @Override
    public void setState(State state) {

    }

    @Override
    public void setTouchDown(boolean touchDown) {

    }

    @Override
    public Object getTag() {
        return null;
    }

    @Override
    public int getID() {
        return 0;
    }

    public void draw(final SpriteBatch batch, final ShapeRenderer renderer) {
        shapeRenderer = renderer;
        spriteBatch = batch;

        if (batch.isDrawing())
            batch.end();

        if (renderer.isDrawing())
            renderer.end();

        proj = batch.getProjectionMatrix().cpy();
        if (camera != null) {
            batch.setProjectionMatrix(camera.combined);
            renderer.setProjectionMatrix(camera.combined);
        }

        if (views.size() < 1)
            return;

        for (int i = views.size() - 1; i >= 0; i--) {
            views.get(i).draw(0, 0, w, h);
        }

        if (renderer.isDrawing())
            renderer.end();

        if (!batch.isDrawing())
            batch.begin();

//        Utility.drawCenteredText(batch, String.valueOf(Gdx.graphics.getFramesPerSecond()) + "fps", Color.WHITE, Gdx.graphics.getWidth() / 9, Gdx.graphics.getWidth() / 9, 0.25f);

        if (batch.isDrawing()) {
            batch.end();
        }

        if (renderer.isDrawing()) {
            renderer.end();
        }
        try {
            ScissorStack.popScissors();
        } catch (Exception e) {
        }

//        renderer.begin(ShapeRenderer.ShapeType.Filled);
//        renderer.setColor(Color.RED);
//        renderer.circle(UniversalClickListener.getInitialTouchDownX(), UniversalClickListener.getInitialTouchDownY(), 39);
        if (renderer.isDrawing()) {
            renderer.end();
        }

        batch.setProjectionMatrix(proj);
        renderer.setProjectionMatrix(proj);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean drag(float startX, float startY, float dx, float dy) {
        continueCheckingForDrag = true;
        for (int i = (views.size() - 1); i >= 0; i--) {
            if (!continueCheckingForDrag) {
                return true;
            }
            if (views.get(i).drag(startX, startY, dx, dy)) {
                continueCheckingForDrag = false;
            }
        }
        return false;
    }

    public void setTouchDown(int x, int y) {
        for (int i = views.size() - 1; i >= 0; i--) {
            final View view = views.get(i);

            if (view.getState() != State.DISABLED && view.click(x, y)) {
                view.setState(State.TOUCH_DOWN);
                return;
            }
        }
    }

    public void setTouchUp() {
        for (int i = 0; i < views.size() - 1; i++) {
            final View view = views.get(i);

            if (view.getState() != State.DISABLED) {
                view.setState(State.TOUCH_UP);
            }
        }
    }

}
