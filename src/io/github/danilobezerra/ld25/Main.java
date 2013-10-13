package io.github.danilobezerra.ld25;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		Texture.setEnforcePotImages(false);
		cfg.title = "LD25 - THE HEROES' MENACE";
		cfg.useGL20 = false;
		cfg.width = 640;
		cfg.height = 480;
		
		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		new LwjglApplication(new Ludum(), cfg);
	}
}
