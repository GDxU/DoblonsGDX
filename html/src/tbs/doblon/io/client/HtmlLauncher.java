package tbs.doblon.io.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import tbs.doblon.io.Game;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        final int h = (int) (com.google.gwt.user.client.Window.getClientHeight() * 0.92);
        return new GwtApplicationConfiguration((h * 9) / 16, h);
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new Game();
    }
}