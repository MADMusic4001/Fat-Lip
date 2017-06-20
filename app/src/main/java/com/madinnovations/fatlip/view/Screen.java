package com.madinnovations.fatlip.view;

import com.madinnovations.fatlip.controller.Game;

/**
 * Base class for Screen implementations
 */
public abstract class Screen {
	protected final Game game;

	public Screen(Game game) {
		this.game = game;
	}

	public abstract void update(float deltaTime);
	public abstract void present(float deltaTime);
	public abstract void pause();
	public abstract void resume();
	public abstract void dispose();
}
