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
import android.util.Log;

import com.madinnovations.fatlip.controller.framework.Input;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.model.GLText;
import com.madinnovations.fatlip.view.activities.FatLipGame;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.programs.TextShaderProgram;

import java.util.List;

import static android.opengl.GLES20.glViewport;

/**
 * Renders the home screen
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class HomeScreen extends Screen {
	private static final String TAG = "HomeScreen";
	// Our matrices
	private final float[] mtrxProjectionAndView = new float[16];
	private GLText glText;
	private RectF playRect = new RectF();
	private RectF helpRect = new RectF();
	private int screenWidth;
	private int screenHeight;

	/**
	 * Creates a new HomeScreen instance.
	 *
	 * @param game  a {@link FatLipGame} instance
	 */
	public HomeScreen(FatLipGame game) {
		super(game);
	}

	@Override
	public void onCreate(int width, int height) {
		glViewport(0, 0, width, height);

		glText = new GLText(new TextShaderProgram((FatLipGame)game), ((FatLipGame)game).getAssets());
		glText.load("Roboto-Regular.ttf", 72, 2, 2);
		float textWidth = glText.getLength("Play");
		float textHeight = glText.getHeight();
		playRect.left = width /2 - textWidth/2;
		playRect.right = playRect.left + textWidth;
		playRect.top = height /2 - textHeight*2;
		playRect.bottom = playRect.top - textHeight;
		textWidth = glText.getLength("Help");
		helpRect.left = width /2 - textWidth/2;
		helpRect.right = helpRect.left + textWidth;
		helpRect.top = height /2 + textHeight;
		helpRect.bottom = helpRect.top + textHeight;

		screenWidth = width;
		screenHeight = height;
		final float ratio = (float)width / height;
		final float[] mtrxProjection = new float[16];
		final float[] mtrxView = new float[16];
		if(width > height) {
			Matrix.frustumM(mtrxProjection, 0, -ratio, ratio, -1, 1, 1, 10);
		}
		else {
			Matrix.frustumM(mtrxProjection, 0, -1, 1, -1/ratio, 1/ratio, 1, 10);
		}
		final int min = Math.min(width, height);
		Matrix.orthoM(mtrxView, 0, -min/2, min/2, -min/2, min/2, 0.1f, 100f);
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
				Log.d(TAG, "update: x,y = " + event.x + "," + event.y);
				Log.d(TAG, "update: playRect = " + playRect);
				if(inBounds(event, playRect)) {
					game.setScreen(new GameScreen(game));
					if(Settings.soundEnabled) {
						Assets.click.play(1);
					}
					Log.d(TAG, "update: in PlayRect");
					return;
				}
				if(inBounds(event, helpRect)) {
					game.setScreen(new HelpScreen(game));
					if(Settings.soundEnabled) {
						Assets.click.play(1);
					}
					Log.d(TAG, "update: in HelpRect");
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
		glText.drawC("Play", 0, 0, 0.0f, 0.0f, 0.0f, 0.0f);
//		glText.draw("Play", playRect.left, playRect.top, 0.0f, 0.0f, 0.0f, 0.0f);
//		glText.draw("Help", helpRect.left, helpRect.top, 0.0f, 0.0f, 0.0f, 0.0f);
		glText.drawC("Help", 0, 144, 0.0f, 0.0f, 0.0f, 0.0f);
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

	private boolean
	inBounds(Input.TouchEvent event, RectF hitRect) {
		return event.x > hitRect.left && event.x < hitRect.right - 1 &&
				event.y > hitRect.top && event.y < hitRect.bottom - 1;
	}
}
