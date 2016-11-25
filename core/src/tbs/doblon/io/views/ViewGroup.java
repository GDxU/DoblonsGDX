package tbs.doblon.io.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import java.util.ArrayList;


/**
 * Created by linde on 07-Dec-15.
 */
public abstract class ViewGroup extends View {
    protected final ArrayList<View> views = new ArrayList<View>();

    @Override
    public boolean click( int xPos, int yPos) {
        for (int i = (views.size() - 1); i >= 0; i--) {
            final View view = views.get(i);
            if (view.click( xPos, yPos)) {
                return true;
            }
        }
        return super.click(xPos, yPos);
    }

    public boolean cullView(final View v) {
        rect2.set(lastRelX + x, lastRelY + y, w, h);
        return rect2.contains(v.getViewBounds());
    }

    public void drawDebug(View v) {
        if (v.debugDraw) {
            final ShapeRenderer renderer = HUDManager.getShapeRenderer();
            final SpriteBatch batch = HUDManager.getSpriteBatch();
            if (batch.isDrawing())
                try {
                    try {
                        batch.end();
                    } catch (Exception e) {
                    }
                    try {
                        ScissorStack.popScissors();
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            if (!renderer.isDrawing()) {
                try {
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


            renderer.setColor(Color.BROWN);
            renderer.rect(rect.x, rect.y, rect.w, rect.h);
        }
    }

    public void addView(View view) {
        if (view == null || views.contains(view))
            return;
        views.add(view);
    }

}
