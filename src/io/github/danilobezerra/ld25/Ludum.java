package io.github.danilobezerra.ld25;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Ludum extends Game {	
	private OrthographicCamera camera = null;
	private SpriteBatch batch = null;
	private ShapeRenderer renderer = null;
	private float width;
	private float height;
	
	@Override
	public void create() {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
			
		setScreen(new MainMenu(this));
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		renderer.setProjectionMatrix(camera.combined);
		
//		batch.begin();
			
			if (getScreen() != null)
				getScreen().render(Gdx.graphics.getDeltaTime());
		
//		batch.end();
			
		camera.update();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		
		if (getScreen() != null) 
			getScreen().dispose();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public void setBatch(SpriteBatch batch) {
		this.batch = batch;
	}

	public ShapeRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(ShapeRenderer renderer) {
		this.renderer = renderer;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}	
}
