package tbs.doblon.io.views;

import java.util.ArrayList;


/**
 * Created by linde on 09-Dec-15.
 */
public abstract class ViewPager extends ViewGroup {
    protected static final ArrayList<View> pages = new ArrayList<View>(4);
    public int currentPage;
    protected float pageScrollOffSetX, pageScrollOffSetY;
    private PageScrollListener pageScrollListener;
    //Todo ensure all pages are the same size as the parent
    //Todo make adapter for both the pages and the titles* optional

    @Override
    public boolean longClick(int xPos, int yPos) {
        //Todo notify on pageListener
        if (pageScrollListener != null) {

        }
        return super.longClick(xPos, yPos);
    }

    @Override
    public boolean click( int xPos, int yPos) {
        //Todo notify on pageListener
        if (pageScrollListener != null) {

        }
        return super.click( xPos, yPos);
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        //Todo
    }

    public abstract ArrayList getTitles();

    public View getPage(int position) {
        if (position < 0 || position >= pages.size())
            return null;

        return pages.get(position);
    }

    //Todo views are the title, pager tab strip and pages


    public void setPageScrollListener(PageScrollListener pageScrollListener) {
        this.pageScrollListener = pageScrollListener;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void draw(float relX, float relY, float parentRight, float parentTop) {
        //Todo draw title and tab strip
        drawBackground(relX, relY);

        final View behind = getPage(currentPage - 1);
        final View current = getPage(currentPage);
        final View ahead = getPage(currentPage + 1);

        final float behindLeft = relX + x + 3;

        //Todo calculate offset and draw the views that a re behind and ahead based on that value
        if (behind != null)
            behind.draw(behindLeft, relY + y, parentRight, parentTop);

        if (current != null)
            current.draw(behindLeft + w, relY + y, parentRight, parentTop);

        if (ahead != null)
            ahead.draw(behindLeft + w + w, relY + y, parentRight, parentTop);

    }

    public interface PageScrollListener {
        void onPageScrolled(int currentPage, float pageScrollOffSet);

        void onPageSelected(int selectedPage);
    }
}
