package io.github.danilobezerra.ld25;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;

public class GameOver implements Screen {
	private Ludum game;
	private Texture background = null;
	
	public GameOver(Ludum game, int result) {
		this.game = game;
		
		switch(result) {
		case 0:
			background = new Texture(Gdx.files.internal("images/YouLose2.png"));
			break;
		case 1:
			background = new Texture(Gdx.files.internal("images/YouLose1.png"));
			break;
		}
	}
	
	@Override
	public void render(float delta) {
		game.getBatch().begin();
			game.getBatch().draw(background, 0, 0);
		game.getBatch().end();
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.setScreen(new MainMenu(game));
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
