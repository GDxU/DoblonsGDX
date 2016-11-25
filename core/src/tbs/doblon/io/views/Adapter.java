package tbs.doblon.io.views;

/**
 * Created by linde on 04-Dec-15.
 */
public abstract class Adapter {

    public abstract int getCount();

    public abstract Object getItem(int position);

    public abstract View getView(int position);

}
