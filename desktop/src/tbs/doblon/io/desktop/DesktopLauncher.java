package tbs.doblon.io.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.Dimension;
import java.awt.Toolkit;

import tbs.doblon.io.Game;

public class DesktopLauncher {
	public static void main(String[] arg) {
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenDimension != null) {
			final int h = Math.round(screenDimension.height * 0.75f);
			config.height = h;
			config.width = (h * 9) / 16;
		} else {
			config.height = 960;
			config.width = 540;
		}

		config.resizable = false;
		new LwjglApplication(new Game(), config);
	}
}
