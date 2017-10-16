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

import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Input;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.model.GLText;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.programs.TextShaderProgram;

import java.util.List;

import static android.opengl.GLES20.glViewport;
import static com.madinnovations.fatlip.view.utils.Geometry.getWorldFromScreen;
import static com.madinnovations.fatlip.view.utils.Geometry.inBounds;

/**
 * Renders the home screen
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class HomeScreen extends Screen {
	private static final String TAG = "HomeScreen";
	// Our matrices
	private final float[] mtrxProjectionAndView = new float[16];
	private final float[] mtrxProjection = new float[16];
	private final float[] mtrxView = new float[16];
	private GLText glText;
	private RectF playRect = new RectF();
	private RectF helpRect = new RectF();
	private int screenWidth;
	private int screenHeight;

	/**
	 * Creates a new HomeScreen instance.
	 *
	 * @param game  a {@link Game} instance
	 */
	public HomeScreen(Game game) {
		super(game);
	}

	@Override
	public void onCreate(int width, int height) {
		screenWidth = width;
		screenHeight = height;

		glViewport(0, 0, width, height);

		glText = new GLText(new TextShaderProgram((GLGame)game), ((GLGame)game).getAssets());
		glText.load("Roboto-Regular.ttf", 72, 2, 2);
		float textHeight = glText.getHeight();

		float textWidth = glText.getLength(((GLGame)game).getResources().getString(R.string.play));
		playRect.left = width/2 - textWidth/2;
		playRect.right = playRect.left + textWidth;
		playRect.top = height/2 + textHeight*2;
		playRect.bottom = playRect.top - textHeight;

		textWidth = glText.getLength(((GLGame)game).getResources().getString(R.string.help));
		helpRect.left = width/2 - textWidth/2;
		helpRect.right = helpRect.left + textWidth;
		helpRect.top = height/2;
		helpRect.bottom = helpRect.top - textHeight;

		final float ratio = (float)width / height;
		if(width > height) {
			Matrix.frustumM(mtrxProjection, 0, -ratio, ratio, -1, 1, 1, 10);
		}
		else {
			Matrix.frustumM(mtrxProjection, 0, -1, 1, -1/ratio, 1/ratio, 1, 10);
		}
		Matrix.orthoM(mtrxView, 0, 0, width, 0, height, 0.1f, 100f);
		Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);

		// enable texture + alpha blending
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void update(float deltaTime) {
		List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		int len = touchEvents.size();
		for(int i = 0; i < len; i++) {
			Input.TouchEvent event = touchEvents.get(i);
			if(event.type == Input.TouchEvent.TOUCH_UP) {
				float[] worldCoords = getWorldFromScreen(screenWidth, screenHeight, event.x, event.y, mtrxProjection,
																  mtrxView);
				if(inBounds(worldCoords[0], worldCoords[1], playRect)) {
					game.setScreen(new SetupScreen(game), true);
					if(Settings.soundEnabled) {
						Assets.click.play(1);
					}
					return;
				}
				if(inBounds(worldCoords[0], worldCoords[1], helpRect)) {
					game.setScreen(new HelpScreen(game), true);
					if(Settings.soundEnabled) {
						Assets.click.play(1);
					}
					return;
				}
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		GLES20.glClearColor(1.0f, 0.5f, 0.5f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// enable texture + alpha blending
		glText.begin(1.0f, 1.0f, 1.0f, 1.0f, mtrxProjectionAndView);
		glText.draw(((GLGame)game).getResources().getString(R.string.play),
					playRect.left, playRect.bottom, 0.0f, 0.0f, 0.0f, 0.0f);
		glText.draw(((GLGame)game).getResources().getString(R.string.help),
					helpRect.left, helpRect.bottom, 0.0f, 0.0f,0.0f, 0.0f);
		glText.end();
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {
		GLES20.glClearColor(0f, 1f, 0f, 1f);
	}

	@Override
	public void dispose() {}
}
