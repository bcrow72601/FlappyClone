package com.bcrowsoftworks.flappy.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bcrowsoftworks.flappy.FlappyClone;
import com.bcrowsoftworks.flappy.sprites.Bird;
import com.bcrowsoftworks.flappy.sprites.Tube;

public class PlayState extends State {

	private static final int TUBE_SPACING = 125;
	private static final int TUBE_COUNT = 4;
	private static final int GROUND_Y_OFFSET = -50;
	private static final int BIRD_STARTING_LOCATION = 50;

	private Bird bird;
	private Texture bg;
	private Texture ground;
	private Texture gameOver;
	private Texture playButton;
	private Vector2 groundPos1, groundPos2, scorePosition;
	private boolean gameRunning = true;
	private int points = 0;
	private BitmapFont font = new BitmapFont();
	private Sound pointSound;

	private Array<Tube> tubes;

	protected PlayState(GameStateManager gsm) {
		super(gsm);
		bird = new Bird(BIRD_STARTING_LOCATION, 300);
		cam.setToOrtho(false, FlappyClone.WIDTH / 2, FlappyClone.HEIGHT / 2);
		bg = new Texture("bg.png");
		tubes = new Array<Tube>(TUBE_COUNT);
		ground = new Texture("ground.png");
		gameOver = new Texture("gameover.png");
		playButton = new Texture("playbtn.png");
		groundPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, GROUND_Y_OFFSET);
		groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + ground.getWidth(), GROUND_Y_OFFSET);
		scorePosition = new Vector2(14, cam.viewportHeight - 4);
		pointSound = Gdx.audio.newSound(Gdx.files.internal("point.wav"));
		font.setUseIntegerPositions(false);

		for (int i = 1; i <= TUBE_COUNT; i++) {
			tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));
		}
	}

	@Override
	protected void handleInput() {
		if (Gdx.input.justTouched() && gameRunning) {
			bird.jump();
		} else if (Gdx.input.justTouched() && !gameRunning) {
			gsm.set(new PlayState(gsm));
		}

	}

	@Override
	public void update(float deltaTime) {
		if(gameRunning) {
			handleInput();
			updateGround();
			bird.update(deltaTime);
			this.updateScoreLocation(deltaTime);
			cam.position.x = bird.getPosition().x + 80;

			for (Tube tube : tubes) {
				if (cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()) {
					tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
				}
				if(tube.collides(bird.getBounds())) {
					gameRunning = false;
					break;
				}
			}

			if (bird.getPosition().y <= ground.getHeight() + GROUND_Y_OFFSET) {
				gameRunning = false;
			}
			if (bird.getPosition().x / (TUBE_SPACING + Tube.TUBE_WIDTH) > points + 1) {
				points++;
				pointSound.play(0.5f);
			}
			cam.update();
		} else {
			handleInput();
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();
		sb.draw(bg, cam.position.x - (cam.viewportWidth / 2), 0);
		sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y);
		for (Tube tube : tubes) {
			sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
			sb.draw(tube.getBottomTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
		}

		sb.draw(ground, groundPos1.x, groundPos1.y);
		sb.draw(ground, groundPos2.x, groundPos2.y);
		if (!gameRunning) {
			if (points == 0) {
				font.draw(sb, "Damn you suck!", cam.position.x - 56, cam.viewportHeight - (cam.viewportHeight/4));
			}
			String pointsString;
			if (points > 1 || points == 0) {
				pointsString = "points";
			} else {
				pointsString = "point";
			}
			font.draw(sb, "You Scored " + String.valueOf(points) + " " + pointsString +"!", cam.position.x - 70, cam.viewportHeight - (cam.viewportHeight/3));
			sb.draw(gameOver, cam.position.x - (gameOver.getWidth()/2), cam.viewportHeight / 2);
			sb.draw(playButton, cam.position.x - (playButton.getWidth()/2), cam.viewportHeight / 3);
		}

		font.draw(sb, String.valueOf(points), scorePosition.x, scorePosition.y);
		sb.end();
	}

	private void updateGround() {
		if(cam.position.x - (cam.viewportWidth /2 ) > groundPos1.x + ground.getWidth()) {
			groundPos1.add(ground.getWidth() *2, 0);
		}
		if(cam.position.x - (cam.viewportWidth /2 ) > groundPos2.x + ground.getWidth()) {
			groundPos2.add(ground.getWidth() *2, 0);
		}
	}

	private void updateScoreLocation(float deltaTime) {
		scorePosition.add(100 * deltaTime, 0);
	}

	@Override
	public void dispose() {
		bg.dispose();
		bird.dispose();
		playButton.dispose();
		gameOver.dispose();
		ground.dispose();
		for(Tube tube: tubes) {
			tube.dispose();
		}
	}

}
