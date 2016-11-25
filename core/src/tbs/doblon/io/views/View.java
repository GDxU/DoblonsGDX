package tbs.doblon.io.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import java.util.ArrayList;

/**
 * Created by Michael on 2/9/2015.
 */
public abstract class View implements InteractiveObject, Viewable {
    public static final Color color = new Color();
    public static final com.badlogic.gdx.math.Rectangle scissors = new com.badlogic.gdx.math.Rectangle(), clipBounds = new com.badlogic.gdx.math.Rectangle();
    //Todo implement some kind of wrapContent, and fill parent
    public State state = State.TOUCH_UP;
    public float x, y, w, h;
    public ArrayList<Drawable> drawables = new ArrayList<Drawable>();
    public OnClickListener onClickListener;
    public OnTouchListener onTouchListener;
    public OnLongClickListener onLongClickListener;
    public Object tag;
    public Background background;
    //Todo tmp
    public boolean debugDraw;
    public long tic;
    protected float lastRelX, lastRelY;
    protected int id;

    public final ShapeRenderer initShapeRenderer(SpriteBatch batch, ShapeRenderer renderer, float relX, float relY) {
        if (batch.isDrawing()) {
            try {
                batch.end();
            } catch (Exception e) {
            }
            try {
                ScissorStack.popScissors();
            } catch (Exception e) {
            }
        }

        if (!renderer.isDrawing()) {
            try {
                clipBounds.set(relX + x, relY + y, w, h);
                ScissorStack.calculateScissors(HUDManager.camera, renderer.getTransformMatrix(), clipBounds, scissors);
                ScissorStack.pushScissors(scissors);
                renderer.begin(ShapeRenderer.ShapeType.Filled);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                renderer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ScissorStack.popScissors();
            } catch (Exception e) {
            }
        }

        return renderer;
    }

    @Override
    public boolean fling(float vx, float vy) {
        return false;
    }

    public final SpriteBatch initSpriteBatch(SpriteBatch batch, ShapeRenderer renderer, float relX, float relY) {
        if (renderer.isDrawing()) {
            try {
                renderer.end();
            } catch (Exception e) {
            }
            try {
                ScissorStack.popScissors();
            } catch (Exception e) {
            }
        }

        if (!batch.isDrawing()) {
            try {
                clipBounds.set(relX + x, relY + y, w, h);
                ScissorStack.calculateScissors(HUDManager.camera, batch.getTransformMatrix(), clipBounds, scissors);
                ScissorStack.pushScissors(scissors);
                batch.begin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                batch.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ScissorStack.popScissors();
            } catch (Exception e) {
            }
        }

        return batch;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public boolean click( int xPos, int yPos) {
        if (state == State.DISABLED || (onTouchListener == null))
            return false;
        rect.set(lastRelX + x, lastRelY + y, w, h);
        final boolean clicked = rect.contains(xPos, yPos);

        if (clicked) {
         //Todo
        }
        return clicked;
    }

    public ShapeRenderer getShapeRenderer(float relX, float relY) {
        return initShapeRenderer(HUDManager.getSpriteBatch(), HUDManager.getShapeRenderer(), relX, relY);
    }

    public SpriteBatch getSpriteBatch(float relX, float relY) {
        return initSpriteBatch(HUDManager.getSpriteBatch(), HUDManager.getShapeRenderer(), relX, relY);
    }

    @Override
    public boolean longClick(int xPos, int yPos) {
        if (state == State.DISABLED && (onTouchListener == null))
            return false;
        rect.set(lastRelX + x, lastRelY + y, w, h);
        final boolean clicked = rect.contains(xPos, yPos);
        if (onLongClickListener != null)
            onLongClickListener.onLongClick(this, xPos, yPos);

        return clicked;
    }

    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void setDrawables(ArrayList<Drawable> drawables) {
        this.drawables = drawables;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void setTouchDown(boolean touchDown) {
        state = touchDown ? State.TOUCH_DOWN : State.TOUCH_UP;
    }

    public Rect getViewBounds() {
        rect.set(lastRelX + x, lastRelY + y, w, h);
//        if (debugDraw)
//            print("getViewBounds > " + rect.toString());
        return rect;
    }

    @Override
    public boolean drag(float startX, float startY, float dx, float dy) {
        return false;
    }

    public float getWidth() {
        return w;
    }

    public void setWidth(float w) {
        this.w = w;
    }

    public float getHeight() {
        return h;
    }

    public void setHeight(float h) {
        this.h = h;
    }

    @Override
    public abstract void dispose();

    public void print(String str) {
        System.out.println(str);
    }

    public void drawBackground(final float relX, final float relY) {
        lastRelX = relX;
        lastRelY = relY;

        if (background != null)
            background.drawRelative(relX + x, relY + y, w, h);
    }

    public void setLastRelX(float lastRelX) {
        this.lastRelX = lastRelX;
    }

    public void setLastRelY(float lastRelY) {
        this.lastRelY = lastRelY;
    }

    @Override
    public String toString() {
        rect.set(lastRelX + x, y + lastRelY, w, h);
        return rect.toString();
    }

    public interface OnClickListener {
        void onClick(View view, int x, int y);
    }

    public interface OnLongClickListener {
        void onLongClick(View view, int x, int y);
    }

    public interface OnTouchListener {
        void onTouch(View view, int x, int y);
    }
}
