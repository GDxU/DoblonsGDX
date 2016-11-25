package tbs.doblon.io.views;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

import tbs.doblon.io.Utility;

/**
 * Created by linde on 09-Dec-15.
 */
public class TestAdapter extends Adapter {
    private static Button button;
    private static ArrayList<Background> backgrounds;
    int w, h, views;

    public TestAdapter(int views) {
        this.views = views;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        createSimpleView();
        backgrounds = new ArrayList<Background>(views);
        for (int i = 0; i < views; i++) {
            backgrounds.add(new Background(Utility.rand.nextInt(), Background.Type.COLOR));
        }
    }

    private void createSimpleView() {
        button = new Button();
        button.setSize(w, w / 10);
    }

    @Override
    public int getCount() {
        //This is where you would have an array list and call array.size()/length
        return views;
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public View getView(int position) {
        button.setText("position > " + position);
        button.setBackground(backgrounds.get(position));
        return button;
    }
}
