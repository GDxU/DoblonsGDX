package tbs.doblon.io.views;


import tbs.doblon.io.ValueAnimator;

/**
 * Created by Michael on 2/11/2015.
 */
public class ScrollView extends LinearLayout {
    protected int scrollX, scrollY, initScrollX, initScrollY, lastMeasuredHeight, lastMeasuredWidth;
    protected final ValueAnimator panAnimator = new ValueAnimator(ValueAnimator.Interpolator.DECELERATE, new ValueAnimator.UpdateListener() {
        @Override
        public void update(double animatedValue) {
//Todo setScroll(x,y);
//            if (!isTouchDownSinceLastPan)
//                camera.setPosition((float) (initX - (animatedValue * flingX)), (float) (initY + (animatedValue * flingY)));
        }

        @Override
        public void onAnimationStart() {
            initScrollX = scrollX;
            initScrollY = scrollY;
        }

        @Override
        public void onAnimationFinish() {

        }
    });
    protected boolean canScrollX, canScrollY;
    protected float flingX, flingY;
    protected ValueAnimator.UpdateListener flingListener = new ValueAnimator.UpdateListener() {
        @Override
        public void update(double animatedValue) {
//    Todo        if (UniversalClickListener.isTouchDownSinceLastPan) {
//                panAnimator.stop();
//            } else {
//                setScrollX(Math.round((float) (initScrollX + (animatedValue * flingX))));
//                setScrollY(Math.round((float) (initScrollY + (animatedValue * flingY))));
//            }
        }

        @Override
        public void onAnimationStart() {
            initScrollX = scrollX;
            initScrollY = scrollY;
        }

        @Override
        public void onAnimationFinish() {

        }
    };


    public ScrollView(boolean resizeChildrenWhenParentResized, boolean canScrollX, boolean canScrollY) {
        super(resizeChildrenWhenParentResized);
        this.canScrollX = canScrollX;
        this.canScrollY = canScrollY;
    }

    public ScrollView(boolean resizeChildrenWhenParentResized) {
        super(resizeChildrenWhenParentResized);
        this.canScrollX = false;
        this.canScrollY = true;
    }

    public void setCanScrollX(boolean canScrollX) {
        this.canScrollX = canScrollX;
    }

    public void setCanScrollY(boolean canScrollY) {
        this.canScrollY = canScrollY;
    }

    @Override
    public void dispose() {
        //Todo fill in
    }

    @Override
    public void draw(float relX, float relY, float parentRight, float parentTop) {
        tic = System.nanoTime();
        updatePanAnimator();
        drawBackground(relX, relY);

        final float viewTop = relY + y + h;

        int cumulative = 0;

        for (int i = 0; i < views.size(); i++) {
            final View v = views.get(i);
            if (resizeChildrenWhenParentResized)
                v.w = v.w > w ? w : v.w;

            cumulative += v.h;
            lastMeasuredWidth = (int) Math.max(v.w, lastMeasuredWidth);
//            v.x += scrollX;
//            v.y -= scrollY;
            v.setLastRelX(relX + x + scrollX);
            v.setLastRelY(viewTop - cumulative - scrollY);
            if (cullView(v))
                v.draw(relX + x + scrollX, viewTop - cumulative - scrollY, Math.min(relX + x + scrollX + w, parentRight), Math.min(viewTop - cumulative - scrollY + h, parentTop));
        }
        lastMeasuredHeight = cumulative;
        print("scrollView tic toc > " + (System.nanoTime() - tic));
    }

    public int getScrollX() {
        return scrollX;
    }

    public void setScrollX(int scrollX) {
        if (canScrollX)
            this.scrollX = scrollX;
    }

    public int getScrollY() {
        return scrollY;
    }

    public void setScrollY(int scrollY) {
        if (canScrollY) {
            scrollY = scrollY > lastMeasuredHeight ? lastMeasuredHeight : scrollY;
//            print("setScrollY > " + scrollY + " lastMH > " + lastMeasuredHeight + " h > " + h);
//            scrollY = (int) (scrollY > lastMeasuredHeight - h ? lastMeasuredHeight - h : scrollY);
            this.scrollY = scrollY;
        }
    }

    public void updatePanAnimator() {
//   Todo     if (panAnimator.isRunning()) {
//            if (UniversalClickListener.isTouchDownSinceLastPan)
//                panAnimator.stop();
//            else
//                panAnimator.update();
//        }
    }

    @Override
    public boolean drag(float startX, float startY, float dx, float dy) {
        rect.set(lastRelX + x, lastRelY + y, w, h);
        if (rect.contains(startX, startY)) {
            setScrollX(scrollX + (int) dx);
            setScrollY(scrollY + (int) dy);
            return true;
        }
        return false;
    }

    @Override
    public boolean fling(float vx, float vy) {
        rect.set(lastRelX + x, lastRelY + y, w, h);
        print("fling sv > " + vx + ", " + vy);

// todo       if (rect.contains(UniversalClickListener.getInitialTouchDownX(), UniversalClickListener.getInitialTouchDownY())) {
//            //Todo pan animator
//            HUDManager.setContinueCheckingClicks(false);
//            final double vector = Math.sqrt((vx * vx) + (vy * vy));
//            print("vector > " + vector);
//            final double vectorScreen = Math.sqrt((w * w) + (h * h));
//            panAnimator.setDuration((vector / vectorScreen) * 250);
//            if (panAnimator.duration > 20) {
//                panAnimator.setUpdateListener(flingListener);
//                panAnimator.start();
//                flingX = (int) (vx * Math.pow(1.000075, Math.abs((int) vx)));
//                flingY = (int) (vy * Math.pow(1.000075, Math.abs((int) vy)));
//
//                print("FX > " + flingX);
//                print("FY > " + flingY);
//            }
//            initScrollX = scrollX;
//            initScrollY = scrollY;
//        }
        return false;
    }

    @Override
    public void setTouchDown(boolean touchDown) {

    }
}
