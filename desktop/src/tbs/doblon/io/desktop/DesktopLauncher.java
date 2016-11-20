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
			final int w = Math.round(screenDimension.width * 0.75f);
			config.width = w;
			config.height = (w * 9) / 16;
		} else {
			config.width = 960;
			config.height = 540;
		}

		config.resizable = false;
		new LwjglApplication(new Game(), config);
	}
}
