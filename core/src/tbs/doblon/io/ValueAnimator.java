package tbs.doblon.io;

/**
 * Created by root on 4/11/14.
 */
public class ValueAnimator {
    public double duration = 1200;
    //Todo fix this
    private Interpolator interpolator = Interpolator.LINEAR;
    private boolean running, autoRemove = true;
    private double animated_value = 1;
    private UpdateListener updateListener;
    private long startTime = System.currentTimeMillis();


    public ValueAnimator() {
    }

    public ValueAnimator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public ValueAnimator(Interpolator interpolator, UpdateListener listener) {
        this.interpolator = interpolator;
        updateListener = listener;
    }

    public ValueAnimator(UpdateListener listener) {
        updateListener = listener;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
        animated_value = 0;
        if (updateListener != null)
            updateListener.onAnimationStart();
        //Todo Screen.addAnimator(this);
    }

    public void reset() {
        animated_value = 0;
        startTime = 0;
        duration = 0;
        running = false;
        //Todo  Screen.removeAnimator(this);
    }

    public void stop() {
        startTime = -1;

        if (updateListener != null)
            updateListener.onAnimationFinish();

        running = false;

//        if (autoRemove)
        //Todo  Screen.removeAnimator(this);
    }

    public void setAutoRemove(boolean autoRemove) {
        this.autoRemove = autoRemove;
    }

    public boolean isRunning() {
        return running;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public synchronized void update() {
        if (!running)
            return;

        final double x = (System.currentTimeMillis() - startTime) / duration;
        switch (interpolator) {
            case DECELERATE:
                animated_value = (float) (Math.pow(x, 2) - (2 * x) + 1);
                break;
            case ACCELERATE:
                animated_value = (float) ((-0.643f * Math.pow(x, 3)) + (-0.3357f * Math.pow(x, 2)) + (0.02143f * x) + 1);
                break;
            case OVER_SHOOT:
                break;
            case LINEAR:
                animated_value = 1 - x;
                break;
            case SPRING:
                animated_value = (float) (
                        (-43.6 * Math.pow(x, 5)) +
                                (102.46 * Math.pow(x, 4)) +
                                (-76.88 * Math.pow(x, 3)) +
                                (18.83 * Math.pow(x, 2)) +
                                (1.806 * x)
                );
                break;
        }

        animated_value = animated_value > 1 ? 1 : animated_value;
        animated_value = 1 - animated_value;
        if (animated_value > 0.99) {
            if (updateListener != null)
                updateListener.update(1);
            stop();
            animated_value = 1;
            return;
        }
        if (updateListener != null)
            updateListener.update(animated_value);

        if ((System.currentTimeMillis() - startTime) >= duration) {
            running = false;
        }
    }

    public float getCurrentValue() {
        return (float) animated_value;
    }

    public void setDuration(double duration) {
        this.duration = duration;
        Utility.print("setDuration>" + duration);
    }

    public enum Interpolator {
        ACCELERATE, DECELERATE, OVER_SHOOT, SPRING, LINEAR
    }

    public interface UpdateListener {
        void update(double animatedValue);

        void onAnimationStart();

        void onAnimationFinish();


    }

}
