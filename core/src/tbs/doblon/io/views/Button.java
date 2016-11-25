package tbs.doblon.io.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import tbs.doblon.io.Utility;

public class Button extends View {
    private String text;
    private Color textColor = Color.WHITE;

    public Button() {
    }

    public Button(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    @Override
    public void draw(float relX, float relY, float relW, float relH) {
        tic = System.nanoTime();
        drawBackground(relX, relY);
        final SpriteBatch batch = getSpriteBatch(relX, relY);
//        for (int i = drawables.size() - 1; i >= 0; i--) {
//            if (w > 0 && h > 0) {
//                batch.draw(drawables.get(i).sprite, x, y, w, h);
//            }
//        }

        Utility.drawCenteredText(batch, text, textColor, 0.5f, relX + x + (w / 2), relY + y + (h / 2));
        print("button tic toc > " + (System.nanoTime() - tic));

    }

    public void flushRenderer(Batch shapeRendererOrSpriteBatch) {
        try {
            shapeRendererOrSpriteBatch.flush();
        } catch (Exception e) {
        }
        try {
            ScissorStack.popScissors();
        } catch (Exception e) {
        }
    }

    @Override
    public void dispose() {
        //Todo fill in
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public int getID() {
        return 0;
    }
}
