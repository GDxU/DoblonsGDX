package tbs.doblon.io.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import tbs.doblon.io.Utility;


/**
 * Created by Michael on 2/9/2015.
 */
public class TextView extends View {
    //Todo make it so that the text view cuts off the drawing once the text drawing position exceeds the height.. ellipsize
    public int padding, maxNumLines;
    public boolean isHeightSetManually;
    protected String text;
    protected float textScale = 0.2f, startingPoint;
    protected Color textColor = new Color(0xffffffff);
    protected float textHeight;
    protected String[] textStrings = {};
    protected Gravity gravity;


    public TextView(int w) {
        this.w = w;
    }

    public TextView(Color textColor, int w) {
        this.textColor = textColor;
        this.w = w;
    }

    public TextView(String text, int w) {
        setText(text);
        this.w = w;
    }

    public TextView(String text, Color textColor, int w) {
        this.textColor = textColor;
        setText(text);
        this.w = w;
    }

    @Override
    public void setWidth(float w) {
        this.w = w;
        getTextStrings();
    }

    @Override
    public void setHeight(float h) {
        super.setHeight(h);
        isHeightSetManually = true;
    }

    public void setAutoHeight(boolean autoHeight) {
        isHeightSetManually = !autoHeight;
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        isHeightSetManually = true;
    }

    @Override
    public void setX(int x) {
        this.x = x;
        getTextStrings();
    }


    @Override
    public void setY(int y) {
        this.y = y;
        getTextStrings();
    }

    public void setMaxNumLines(int maxNumLines) {
        this.maxNumLines = maxNumLines;
    }

    public void setTextScale(float textScale) {
        this.textScale = textScale;
    }

    public void setText(String text) {
        this.text = text;
        getTextStrings();
    }


    public void getTextStrings() {
        if (text == null || text.length() < 1)
            return;
        Utility.getFont().getData().setScale(textScale);
        final GlyphLayout layout = Utility.glyphLayout;
        layout.setText(Utility.getFont(), text);

        int numLines = (int) Math.ceil(layout.width / (w));
        numLines = numLines > 25 ? 25 : numLines;

        final int numLettersInOneLine = (int) Math.ceil(text.length() / numLines);
        textHeight = layout.height;
        h = Math.round((textHeight * numLines) + (padding * (numLines + 1)));
        startingPoint = (y + h - padding) - (textHeight / 2);
        if (textStrings == null || numLines != textStrings.length)
            textStrings = new String[numLines];
        for (int i = 0; i < numLines; i++) {
            String out = "";
            if (!(numLines - 1 == i)) {
                int index = i * numLettersInOneLine;
                if (!(index + numLettersInOneLine > text.length()))
                    out = text.substring(index, index + numLettersInOneLine);
            } else {
                out = text.substring(i * numLettersInOneLine, text.length());
            }
            textStrings[i] = out;
        }
    }

    protected void drawText(final SpriteBatch batch, final float relX, final float relY) {
        for (int i = 0; i < textStrings.length; i++) {
            if (gravity == null)
                gravity = Gravity.LEFT;
            switch (gravity) {
                case CENTER:

                    break;
                case LEFT:

                    break;
                case RIGHT:

                    break;
                default:
            }
            Utility.drawCenteredText(batch, textStrings[i], textColor, textScale, relX + x + (w / 2), relY + startingPoint - (i * textHeight));
        }
    }

    @Override
    public void draw(float relX, float relY, float parentRight, float parentTop) {
        drawBackground(relX, relY);
        final SpriteBatch batch = getSpriteBatch(relX, relY);
        for (int i = 0; i < drawables.size() - 1; i++) {
            if (w > 0 && h > 0) {
                batch.draw(drawables.get(i).sprite, relX, relY);
            }
        }

        drawText(batch, relX, relY);
    }

    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
        getTextStrings();
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void dispose() {
        textStrings = null;
    }


    public enum Gravity {
        CENTER, LEFT, RIGHT
    }
}
