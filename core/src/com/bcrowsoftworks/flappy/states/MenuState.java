package com.bcrowsoftworks.flappy.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bcrowsoftworks.flappy.FlappyClone;

public class MenuState extends State {

	private Texture background;
	private Texture playButton;

	@Override
	public void handleInput() {
		if(Gdx.input.justTouched()) {
			gsm.set(new PlayState(gsm));
		}
	}

	@Override
	public void update(float deltaTime) {
		this.handleInput();
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.begin();
		sb.draw(background, 0, 0, FlappyClone.WIDTH, FlappyClone.HEIGHT);
		sb.draw(playButton, (FlappyClone.WIDTH/2) - (playButton.getWidth()/2), (FlappyClone.HEIGHT/2));
		sb.end();
	}

	public MenuState(GameStateManager gsm) {
		super(gsm);
		background = new Texture("bg.png");
		playButton = new Texture("playbtn.png");
	}

	@Override
	public void dispose() {
		background.dispose();
		playButton.dispose();
	}

}
