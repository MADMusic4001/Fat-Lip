package com.madinnovations.fatlip.view;

import android.opengl.GLES20;

import com.madinnovations.fatlip.controller.Game;

/**
 * Renders the home screen
 */
public class HomeScreen extends Screen {

	public HomeScreen(Game game) {
		super(game);
	}

	@Override
	public void update(float deltaTime) {
	}

	@Override
	public void present(float deltaTime) {
		GLES20.glClearColor(0f, 1f, 0f, 1f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {
		GLES20.glClearColor(0f, 1f, 0f, 1f);
	}

	@Override
	public void dispose() {

	}
}
