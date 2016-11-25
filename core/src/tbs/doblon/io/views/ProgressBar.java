package tbs.doblon.io.views;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


/**
 * Created by Michael on 1/31/2015.
 */
public class ProgressBar extends View {
    public int progress, backgroundColor, progressColor;
    private float max = 100, innerR, intermidLength, r, diff;
    private boolean complete = false;
    private ProgressListener listener;

    public ProgressBar(int max, int background, int progressColor, int w, int h) {
        this.max = max;
        this.progressColor = progressColor;
        this.backgroundColor = background;
        this.w = w;
        this.h = h;
        setRadii();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (complete)
            return;

        progress = progress < 0 ? 0 : progress;
        progress = progress > max ? (int) max : progress;

        complete = progress >= max;

        if (listener != null) {
            if (complete)
                listener.onComplete();
            else
                listener.onProgressChanged(progress, max);
        }
        this.progress = progress;
    }

    private void setRadii() {
        r = Math.min(w, h) / 2;
        innerR = r * 0.85f;
        diff = r - innerR;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void draw(float relX, float relY, float parentRight, float parentTop) {
        lastRelX = relX;
        lastRelY = relY;

        color.set(backgroundColor);

        final ShapeRenderer renderer = getShapeRenderer(relX, relY);

        renderer.setColor(color);
        renderer.circle(relX + x + r, relY + y + r, r);
        renderer.rect(relX + x + r, relY + y, w - r - r, h);
        renderer.circle(relX + x + w - r, relY + y + r, r);

        intermidLength = w - h;
        final float innerIntermidLength = intermidLength * (progress / max);

        color.set(progressColor);
        renderer.setColor(color);
        renderer.circle(relX + x + r, relY + y + r, innerR);
        renderer.rect(relX + x + r, relY + y + diff, innerIntermidLength, h - diff - diff);
        renderer.circle(relX + x + r + innerIntermidLength, relY + y + r, innerR);
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void reset() {
        complete = false;
        progress = 0;
    }

    @Override
    public void setHeight(float h) {
        super.setHeight(h);
        setRadii();
    }

    @Override
    public void setWidth(float w) {
        super.setWidth(w);
        setRadii();
    }

    public float getMax() {
        max = max < 1 ? 1 : max;
        return max;
    }


    public void setProgressChangeListener(ProgressListener listener) {
        this.listener = listener;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isRunning() {
        return !complete;
    }


    public interface ProgressListener {
        void onProgressChanged(int progress, float max);

        void onComplete();

        void onStart();
    }
}
