package io.github.danilobezerra.ld25;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Play implements Screen {
	Ludum game;
	SpriteBatch batch = null;
	ShapeRenderer renderer = null;
	Camera camera = null;

	Texture charset;
	Texture background = null;
	Texture border = null;
	Texture fireballSprite = null;

	Animation princessAnimation = null;
	Animation villainAnimation = null;
	Animation heroAnimation = null;

	Circle dangerZone;
	Rectangle villain;
	Rectangle princess;
	Array<Array<Rectangle>> fireballs;
	Rectangle fireball;
	Vector3 fire;

	Array<Array<Rectangle>> heroes;

	Array<Vector3> positions;
	Vector2 spriteSize;
	float presentPosition;
	long swarmTime;
	float frameTime;
	
	Sound beginGame = null;
	Sound shotFireball = null;
	Sound hit = null;
	
	long heroesKilled = 0;
	
	BitmapFont text = null;

	public Play(Ludum game) {
		this.game = game;
		batch = game.getBatch();
		renderer = game.getRenderer();
		camera = game.getCamera();
		
		text = new BitmapFont(Gdx.files.internal("fonts/texts.fnt"), 
				Gdx.files.internal("images/texts.png"), false);

		background = new Texture(Gdx.files.internal("images/Background.png"));
		border = new Texture(Gdx.files.internal("images/Border.png"));
		charset = new Texture(Gdx.files.internal("images/Charset.png"));
		fireballSprite = new Texture(Gdx.files.internal("images/fireball.png"));
		
		beginGame = Gdx.audio.newSound(Gdx.files.internal("sounds/Start.wav"));
		shotFireball = Gdx.audio.newSound(Gdx.files.internal("sounds/Shot.wav"));
		hit = Gdx.audio.newSound(Gdx.files.internal("sounds/Hit.wav"));
		
		princessAnimation = sheetFactory(0);
		villainAnimation = sheetFactory(1);
		heroAnimation = sheetFactory(2);

		spriteSize = new Vector2(33, 35);
		dangerZone = new Circle(320, 240, 50);
		presentPosition = 0.0f;

		positions = createPositions();
		princess = new Rectangle(positions.get(0).x, positions.get(0).y, spriteSize.x, spriteSize.y);
		villain = new Rectangle(positions.get(1).x, positions.get(1).y, spriteSize.x, spriteSize.y);
		fireball = new Rectangle();

		fire = null;

		heroes = new Array<Array<Rectangle>>();
		for(int i = 0; i < 8; i++) {
			heroes.add(new Array<Rectangle>());
		}
		
		beginGame.play();
	}

	@Override
	public void render(float delta) {
		frameTime += delta * 1.5f;
		
		batch.begin();
			batch.draw(background, 0, 0);
			
			
			batch.draw(princessAnimation.getKeyFrame(frameTime), princess.x, princess.y, princess.width, princess.height);
			batch.draw(villainAnimation.getKeyFrame(frameTime), villain.x, villain.y, villain.width, villain.height);
	
			for (Array<Rectangle> legion : heroes) {
				for (Rectangle hero : legion) {
					batch.draw(heroAnimation.getKeyFrame(frameTime), hero.x, hero.y, hero.width, hero.height);
				}
			}
	
			if (fire != null) {
				batch.draw(fireballSprite, fire.x, fire.y, 10, 10);
			}
			
			batch.draw(border, 0, 0);
		batch.end();

		renderer.begin(ShapeType.Circle);
			renderer.setColor(0.75f, 0.70f, 0.62f, 0.18f);
			renderer.circle(dangerZone.x, dangerZone.y, dangerZone.radius);
		renderer.end();
		
		renderer.begin(ShapeType.FilledRectangle);
			renderer.setColor(Color.BLACK);
			renderer.filledRect(430, 437, 210, 30);
			renderer.filledRect(-1, 10, 315, 30);
		renderer.end();
		
		renderer.begin(ShapeType.Rectangle);
			renderer.setColor(Color.WHITE);
			renderer.rect(430, 437, 210, 30);
			renderer.rect(-1, 10, 315, 30);
		renderer.end();
		
		batch.begin();
			text.setColor(Color.WHITE);
			text.draw(batch, "Heroes killed: " + heroesKilled, 440, 470);
			text.draw(batch, "Don't let them rescue her!!", 30, 43);
		batch.end();
		
		readInput();
		spawnHeroes();
		move(delta);
		
		System.out.println(heroesKilled);
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

	private Array<Vector3> createPositions() {
		Array<Vector3> e = new Array<Vector3>();
		int r = 250;

		/**
		 * Princess position
		 */
		e.add(new Vector3(303, 224, 0));

		/**
		 * Villain positions
		 */
		e.add(new Vector3(303, 294, 1));
		e.add(new Vector3(353, 273, 2));
		e.add(new Vector3(374, 224, 3));
		e.add(new Vector3(353, 173, 4));
		e.add(new Vector3(303, 154, 5));
		e.add(new Vector3(253, 173, 6));
		e.add(new Vector3(231, 224, 7));
		e.add(new Vector3(253, 273, 8));

		/**
		 * Heroes initial position
		 */
		e.add(new Vector3(e.get(1).x   , e.get(1).y+r , e.get(0).z));
		e.add(new Vector3(e.get(2).x+r , e.get(2).y+r , e.get(0).z));
		e.add(new Vector3(e.get(3).x+r , e.get(3).y	  , e.get(0).z));
		e.add(new Vector3(e.get(4).x+r , e.get(4).y-r , e.get(0).z));
		e.add(new Vector3(e.get(5).x   , e.get(5).y-r , e.get(0).z));
		e.add(new Vector3(e.get(6).x-r , e.get(6).y-r , e.get(0).z));
		e.add(new Vector3(e.get(7).x-r , e.get(7).y   , e.get(0).z));
		e.add(new Vector3(e.get(8).x-r , e.get(8).y+r , e.get(0).z));

		return e;
	}

	private void spawnFireball(float x, float y, float z) {
		if (fire == null) {
			fire = new Vector3(x, y, z);
			shotFireball.play();
		}		
	}


	private void spawnHeroes() {
		int heroPosition = MathUtils.random(9, 16);

		if (TimeUtils.nanoTime() - swarmTime > 1000000000) {
			heroes.get(heroPosition - 9).add(new Rectangle(
					positions.get(heroPosition).x, 
					positions.get(heroPosition).y,
					spriteSize.x, spriteSize.y));	
			swarmTime = TimeUtils.nanoTime();
		}		
	}

	private void move(float delta) {
		int heroMovement = 50;

		if (fire != null) {		
			switch(MathUtils.ceilPositive(fire.z - 1)) {
			case 0:
				fire.y += (heroMovement * 2) * delta;
				break;
			case 1:
				fire.x += (heroMovement * 2) * delta;
				fire.y += (heroMovement * 2) * delta;
				break;
			case 2:
				fire.x += (heroMovement * 2) * delta;
				break;
			case 3:
				fire.x += (heroMovement * 2) * delta;
				fire.y -= (heroMovement * 2) * delta;
				break;
			case 4:
				fire.y -= (heroMovement * 2) * delta;
				break;
			case 5:
				fire.x -= (heroMovement * 2) * delta;
				fire.y -= (heroMovement * 2) * delta;
				break;
			case 6:
				fire.x -= (heroMovement * 2) * delta;
				break;
			case 7:
				fire.x -= (heroMovement * 2) * delta;
				fire.y += (heroMovement * 2) * delta;
				break;
			}

			if (fire.x <= 0 || fire.x >= game.getWidth() ||
					fire.y <= 0 || fire.y >= game.getHeight()) {
				fire = null;
			}
		}



		Iterator<Rectangle> northHeroes = heroes.get(0).iterator();	
		while(northHeroes.hasNext()) {
			Rectangle hero = northHeroes.next();
			hero.y -= heroMovement * delta;

			if (fire != null && hero.overlaps(new Rectangle(fire.x, fire.y, 10, 10))) {
				fire = null;
				northHeroes.remove();
				hit.play();
				heroesKilled++;
			}

			if (hero.overlaps(villain)) {
				northHeroes.remove();
				game.setScreen(new GameOver(game, 0));
			} else if (Intersector.overlapCircleRectangle(dangerZone, hero)) {
				northHeroes.remove();
				game.setScreen(new GameOver(game, 1));
			}
		}

		Iterator<Rectangle> northeastHeroes = heroes.get(1).iterator();	
		while(northeastHeroes.hasNext()) {
			Rectangle hero = northeastHeroes.next();
			hero.x -= heroMovement * delta;
			hero.y -= heroMovement * delta;

			if (fire != null && hero.overlaps(new Rectangle(fire.x, fire.y, 10, 10))) {
				fire = null;
				northeastHeroes.remove();
				hit.play();
				heroesKilled++;
			}

			if (hero.overlaps(villain)) {
				northeastHeroes.remove();
				game.setScreen(new GameOver(game, 0));
			} else if (Intersector.overlapCircleRectangle(dangerZone, hero)) {
				northeastHeroes.remove();
				game.setScreen(new GameOver(game, 1));
			}
		}

		Iterator<Rectangle> eastHeroes = heroes.get(2).iterator();	
		while(eastHeroes.hasNext()) {
			Rectangle hero = eastHeroes.next();
			hero.x -= heroMovement * delta;

			if (fire != null && hero.overlaps(new Rectangle(fire.x, fire.y, 10, 10))) {
				fire = null;
				eastHeroes.remove();
				hit.play();
				heroesKilled++;
			}

			if (hero.overlaps(villain)) {
				eastHeroes.remove();
				game.setScreen(new GameOver(game, 0));
			} else if (Intersector.overlapCircleRectangle(dangerZone, hero)) {
				eastHeroes.remove();
				game.setScreen(new GameOver(game, 1));
			}
		}

		Iterator<Rectangle> southeastHeroes = heroes.get(3).iterator();	
		while(southeastHeroes.hasNext()) {
			Rectangle hero = southeastHeroes.next();
			hero.x -= heroMovement * delta;
			hero.y += heroMovement * delta;

			if (fire != null && hero.overlaps(new Rectangle(fire.x, fire.y, 10, 10))) {
				fire = null;
				southeastHeroes.remove();
				hit.play();
				heroesKilled++;
			}

			if (hero.overlaps(villain)) {
				southeastHeroes.remove();
				game.setScreen(new GameOver(game, 0));
			} else if (Intersector.overlapCircleRectangle(dangerZone, hero)) {
				southeastHeroes.remove();
				game.setScreen(new GameOver(game, 1));
			}
		}

		Iterator<Rectangle> southHeroes = heroes.get(4).iterator();	
		while(southHeroes.hasNext()) {
			Rectangle hero = southHeroes.next();
			hero.y += heroMovement * delta;

			if (fire != null && hero.overlaps(new Rectangle(fire.x, fire.y, 10, 10))) {
				fire = null;
				southHeroes.remove();
				hit.play();
				heroesKilled++;
			}

			if (hero.overlaps(villain)) {
				southHeroes.remove();
				game.setScreen(new GameOver(game, 0));
			} else if (Intersector.overlapCircleRectangle(dangerZone, hero)) {
				southHeroes.remove();
				game.setScreen(new GameOver(game, 1));
			}
		}

		Iterator<Rectangle> southwestHeroes = heroes.get(5).iterator();	
		while(southwestHeroes.hasNext()) {
			Rectangle hero = southwestHeroes.next();
			hero.x += heroMovement * delta;
			hero.y += heroMovement * delta;

			if (fire != null && hero.overlaps(new Rectangle(fire.x, fire.y, 10, 10))) {
				fire = null;
				southwestHeroes.remove();
				hit.play();
				heroesKilled++;
			}

			if (hero.overlaps(villain)) {
				southwestHeroes.remove();
				game.setScreen(new GameOver(game, 0));
			} else if (Intersector.overlapCircleRectangle(dangerZone, hero)) {
				southwestHeroes.remove();
				game.setScreen(new GameOver(game, 1));
			}
		}

		Iterator<Rectangle> westHeroes = heroes.get(6).iterator();	
		while(westHeroes.hasNext()) {
			Rectangle hero = westHeroes.next();
			hero.x += heroMovement * delta;

			if (fire != null && hero.overlaps(new Rectangle(fire.x, fire.y, 10, 10))) {
				fire = null;
				westHeroes.remove();
				hit.play();
				heroesKilled++;
			}

			if (hero.overlaps(villain)) {
				westHeroes.remove();
				game.setScreen(new GameOver(game, 0));
			} else if (Intersector.overlapCircleRectangle(dangerZone, hero)) {
				westHeroes.remove();
				game.setScreen(new GameOver(game, 1));
			}
		}

		Iterator<Rectangle> northwestHeroes = heroes.get(7).iterator();	
		while(northwestHeroes.hasNext()) {
			Rectangle hero = northwestHeroes.next();
			hero.x += heroMovement * delta;
			hero.y -= heroMovement * delta;

			if (fire != null && hero.overlaps(new Rectangle(fire.x, fire.y, 10, 10))) {
				fire = null;
				northwestHeroes.remove();
				hit.play();
				heroesKilled++;
			}

			if (hero.overlaps(villain)) {
				northwestHeroes.remove();
				game.setScreen(new GameOver(game, 0));
			} else if (Intersector.overlapCircleRectangle(dangerZone, hero)) {
				northwestHeroes.remove();
				game.setScreen(new GameOver(game, 1));
			}
		}
	}

	private Animation sheetFactory(int character) {
		TextureRegion[][] regions = TextureRegion.split(charset, 33, 35);
		Array<TextureRegion> spriteSheet = new Array<TextureRegion>();
		
		switch(character) {
		case 0:
			spriteSheet.add(regions[4][0]);
			spriteSheet.add(regions[4][1]);
			spriteSheet.add(regions[4][2]);
			break;
		case 1:
			spriteSheet.add(regions[2][0]);
			spriteSheet.add(regions[2][1]);
			spriteSheet.add(regions[2][2]);
//			spriteSheet.add(regions[3][0]);
//			spriteSheet.add(regions[3][1]);
//			spriteSheet.add(regions[3][2]);
			break;
		case 2:
			spriteSheet.add(regions[0][0]);
			spriteSheet.add(regions[0][1]);
			spriteSheet.add(regions[0][2]);
//			spriteSheet.add(regions[1][0]);
//			spriteSheet.add(regions[1][1]);
//			spriteSheet.add(regions[1][2]);
			break;
		default:
			return null;
		}

		return new Animation(0.15f, spriteSheet, Animation.LOOP_PINGPONG);
	}


	private void readInput() {
		/**
		 *  TODO: Touch screen after LudumDare
		 *  
			 if (Gdx.input.isTouched()) {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				ld.getCamera().unproject(touchPos);
				System.out.println(touchPos.x + ", " + touchPos.y);
			}
		 *
		 */

		if ((Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
				&& (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))) {
			villain.x = positions.get(2).x;
			villain.y = positions.get(2).y;
			presentPosition = (int) positions.get(2).z;
		} else if  ((Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
				&& (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))){
			villain.x = positions.get(4).x;
			villain.y = positions.get(4).y;
			presentPosition = (int) positions.get(4).z;
		} else if  ((Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
				&& (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))){
			villain.x = positions.get(6).x;
			villain.y = positions.get(6).y;
			presentPosition = (int) positions.get(6).z;
		} else if  ((Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
				&& (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))){
			villain.x = positions.get(8).x;
			villain.y = positions.get(8).y;
			presentPosition = (int) positions.get(8).z;
		} else {
			if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
				villain.x = positions.get(1).x;
				villain.y = positions.get(1).y;
				presentPosition = (int) positions.get(1).z;
			}

			if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
				villain.x = positions.get(3).x;
				villain.y = positions.get(3).y;
				presentPosition = (int) positions.get(3).z;
			}

			if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
				villain.x = positions.get(5).x;
				villain.y = positions.get(5).y;
				presentPosition = (int) positions.get(5).z;
			}

			if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
				villain.x = positions.get(7).x;
				villain.y = positions.get(7).y;
				presentPosition = (int) positions.get(7).z;
			}
		}

		if (Gdx.input.isKeyPressed(Keys.ENTER) || Gdx.input.isKeyPressed(Keys.SPACE)) {
			if (presentPosition != 0)
				spawnFireball(villain.x + (spriteSize.x / 2), 
						villain.y + (spriteSize.y / 2), 
						presentPosition);
		}

		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.setScreen(new MainMenu(game));
		}
	}
}