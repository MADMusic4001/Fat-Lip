/*
 *
 *   Copyright (C) 2017 MadInnovations
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *   <p/>
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   <p/>
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.madinnovations.fatlip.view.screens;

import android.opengl.GLES20;
import android.view.View;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Input;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.model.Splash;
import com.madinnovations.fatlip.view.framework.FramesPerSecondLogger;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.programs.SplashShaderProgram;
import com.madinnovations.fatlip.view.utils.TextureHelper;

import java.util.List;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Renders a splash screen while loading the game assets
 */
@SuppressWarnings("unused")
public class LoadingScreen extends Screen {
	private static final String TAG = "LoadingScreen";
	private final Splash splash;
	private FramesPerSecondLogger fpsLogger = new FramesPerSecondLogger();
	private SplashShaderProgram splashProgram;
	private int textureId;
	private boolean loaded = false;

	/**
	 * Creates a new LoadingScreen instance
	 *
	 * @param game  a {@link Game} instance
	 */
	public LoadingScreen(final Game game) {
		super(game);
		splash = new Splash();
	}

	@Override
	public void onCreate(int width, int height) {
		glClearColor(0.5f, 0.5f, 0.5f, 0.0f);

		glViewport(0, 0, width, height);
		float ratio = (float)width / height;

		textureId = TextureHelper.loadTexture((GLGame)game, R.drawable.fatlip);
		splashProgram = new SplashShaderProgram((GLGame)game);

		int[] value = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, value, 0);
		TextureHelper.setMaxTextureSize(value[0]);
	}

	@Override
	public void update(float deltaTime) {
		synchronized (this) {
			if (!loaded) {
				Assets.click = game.getAudio().newSound("click.ogg");
				Settings.load(game.getFileIO());
				loaded = true;
			}
		}
		List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		int len = touchEvents.size();
		for(int i = 0; i < len; i++) {
			Input.TouchEvent event = touchEvents.get(i);
			if(event.type == Input.TouchEvent.TOUCH_UP) {
				game.setScreen(new HomeScreen(game), false);
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		fpsLogger.logFrame();
		// clear Screen and Depth Buffer,
		// we have set the clear color as black.
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		splashProgram.useProgram();
		splashProgram.setUniforms(textureId);
		splash.bindData(splashProgram);
		splash.draw();
		splash.unbindData(splashProgram);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}

	@Override
	public void showScreen() {
		((GLGame)game).getGlView().setVisibility(View.VISIBLE);
	}

	@Override
	public void hideScreen() {
		((GLGame)game).getGlView().setVisibility(View.GONE);
	}
}
