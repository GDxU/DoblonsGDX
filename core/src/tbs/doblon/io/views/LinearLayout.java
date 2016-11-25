package tbs.doblon.io.views;


/**
 * Created by Michael on 3/10/2015.
 */
public class LinearLayout extends ViewGroup {
    public static final int VERTICAL_LAYOUT = 0;
    public static final int HORIZONTAL_LAYOUT = 1;
    protected int layoutDirection;
    protected boolean resizeChildrenWhenParentResized;

    public LinearLayout(boolean resizeChildrenWhenParentResized) {
        this.resizeChildrenWhenParentResized = resizeChildrenWhenParentResized;
    }

    @Override
    public float getHeight() {
        //Todo        switch (layoutDirection) {
//            case VERTICAL:
//                height = 0;
//                for (int i = 0; i < views.size()-1; i++) {
//                    height += views.get(i).height;
//                }
//                break;
//            case HORIZONTAL:
//                height = views.get(0).height;
//                for (int i = 1; i < views.size()-1; i++) {
//                    height = Math.max(height, views.get(i).height);
//                }
//                break;
//        }
        return h;
    }

    public boolean isResizeChildrenWhenParentResized() {
        return resizeChildrenWhenParentResized;
    }

    public void setResizeChildrenWhenParentResized(boolean resizeChildrenWhenParentResized) {
        this.resizeChildrenWhenParentResized = resizeChildrenWhenParentResized;
    }

    @Override
    public boolean click( int xPos, int yPos) {
        return super.click( xPos, yPos);
    }

    public void removeView(View view) {
        if (views != null && views.contains(view))
            views.remove(view);
    }

    public void removeAllViews() {
        if (views != null)
            views.clear();
    }

    @Override
    public float getWidth() {

//  TODO      switch (layoutDirection) {
//            case HORIZONTAL:
//                width = 0;
//                for (int i = 0; i < views.size()-1; i++) {
//                    width += views.get(i).width;
//                }
//                break;
//            case VERTICAL:
//                width = views.get(0).width;
//                for (int i = 1; i < views.size()-1; i++) {
//                    width = Math.max(width, views.get(i).width);
//                }
//                break;
//        }
        return w;
    }

    @Override
    public void setWidth(float w) {
        this.w = w;

        if (resizeChildrenWhenParentResized)
            for (int i = views.size() - 1; i >= 0; i--) {
                final View v = views.get(i);
                v.w = v.w > w ? w : v.w;
            }
    }

    @Override
    public boolean drag(float startX, float startY, float dx, float dy) {
//        rect.set(lastRelX + x, lastRelY + y, width, height);
//        if (rect.contains(x, y)) {
//            //Todo pan animator
//        }
        return false;
    }


    @Override
    public void draw(float relX, float relY, float parentRight, float parentTop) {

        lastRelX = relX;
        lastRelY = relY;
        drawBackground(relX, relY);

        int cumulative = 0;
        final float viewTop = relY + y + h;
        for (int i = 0; i < views.size(); i++) {
            final View v = views.get(i);
            if (resizeChildrenWhenParentResized)
                v.w = v.w > w ? w : v.w;

            cumulative += v.h;
            v.setLastRelX(relX + x);
            v.setLastRelY(viewTop - cumulative);

            if (cullView(v))
                v.draw(relX + x, viewTop - cumulative, Math.min(relX + w, parentRight), Math.min(relY + h, parentTop));
        }
    }

    public void setLayoutDirection(int layoutDirection) {
        this.layoutDirection = layoutDirection;
    }

    @Override
    public void setSize(int w, int h) {
        setWidth(w);
        setHeight(h);
    }


    @Override
    public void dispose() {
        for (View view : views) {
            view.dispose();
        }
    }
}
