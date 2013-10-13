package io.github.danilobezerra.ld25;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class MainMenu implements Screen {
	Ludum game;
	Texture background = null;
	
	public MainMenu(Ludum game) {
		this.game = game;
		
		background = new Texture(Gdx.files.internal("images/TitleScreen.png"));
	}

	@Override
	public void render(float delta) {
		game.getBatch().begin();
			game.getBatch().draw(background, 0, 0);
		game.getBatch().end();
		
		if (Gdx.input.isKeyPressed(Keys.ENTER)) {
			game.setScreen(new Play(game));
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
