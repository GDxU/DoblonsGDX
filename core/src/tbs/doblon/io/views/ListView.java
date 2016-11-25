package tbs.doblon.io.views;


/**
 * Created by Michael on 3/10/2015.
 */
public class ListView extends ScrollView {
    //Todo get methods from Scrollview
    private OnItemClickListener onItemClickListener;
    private Adapter adapter;

    public ListView() {
        super(true);
    }

    public ListView(Adapter adapter) {
        super(true);
        this.adapter = adapter;
    }

    public ListView(Adapter adapter, OnItemClickListener onItemClickListener) {
        super(true);
        this.onItemClickListener = onItemClickListener;
        this.adapter = adapter;
    }

    @Override
    public boolean drag(float startX, float startY, float dx, float dy) {
        rect.set(lastRelX + x, lastRelY + y, w, h);
        if (rect.contains(startX, startY)) {
            //Todo pan animator
            setScrollX(scrollX + (int) dx);
            setScrollY(scrollY + (int) dy);
            return true;
        }
        return false;
    }

    @Override
    public boolean click( int xPos, int yPos) {
        for (int i = 0; i < adapter.getCount(); i++) {
            final View v = adapter.getView(i);
            rect.set(x, y, w, h);
            if (rect.contains(v.x, v.y, v.w, v.h))
                if (v.click( xPos, yPos)) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(v, i);
                    return true;
                }
        }
        return false;
    }

    @Override
    public void draw(float relX, float relY, float parentRight, float parentTop) {
        tic = System.nanoTime();
        updatePanAnimator();
        drawBackground(relX, relY);
        if (adapter == null || adapter.getCount() < 1) {
            return;
        }

        int cumulative = 0;
        final float viewTop = relY + y + h;

        for (int i = 0; i < adapter.getCount(); i++) {
            final View v = adapter.getView(i);
            if (resizeChildrenWhenParentResized)
                v.w = v.w > w ? w : v.w;

            cumulative += v.h;
            v.setLastRelX(relX + x + scrollX);
            v.setLastRelY(viewTop - cumulative - scrollY);

            if (cullView(v))
                v.draw(relX + x + scrollX, viewTop - cumulative - scrollY, Math.min(relX + x + scrollX + w, parentRight), Math.min(viewTop - cumulative - scrollY + h, parentTop));

        }
        lastMeasuredHeight = cumulative;

        print("listView tic toc > " + (System.nanoTime() - tic));

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }


    @Override
    public void addView(View view) {
        print("listview can't add views");
    }

    @Override
    public void removeAllViews() {
        print("listview can't remove views");
    }

    @Override
    public void removeView(View view) {
        print("listview can't remove views");
    }

    @Override
    public void dispose() {
        for (View view : views) {
            view.dispose();
        }

        if (adapter != null)
            for (int i = 0; i < adapter.getCount(); i++) {
                adapter.getView(i).dispose();
            }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

}
