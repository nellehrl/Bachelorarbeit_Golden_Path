package com.mygdx.dijkstra;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.dijkstra.DijkstraAlgorithm;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1200, 720);
		config.useVsync(true);
		config.setForegroundFPS(60);
		config.setTitle("dijkstra-algorithm");
		new Lwjgl3Application(new DijkstraAlgorithm(), config);
	}
}
