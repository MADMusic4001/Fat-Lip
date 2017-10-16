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

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Input;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.model.GLText;
import com.madinnovations.fatlip.model.Tile;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.programs.TextShaderProgram;
import com.madinnovations.fatlip.view.programs.TileShaderProgram;
import com.madinnovations.fatlip.view.utils.Geometry;

import java.util.List;

import static android.opengl.GLES20.glViewport;
import static com.madinnovations.fatlip.view.utils.Geometry.getWorldFromScreen;
import static com.madinnovations.fatlip.view.utils.Geometry.inBounds;

/**
 * UI for selecting opponent, object and scenery for the game
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SetupScreen extends Screen {
	private static final String TAG = "SetupScreen";
	private final float[] mtrxProjectionAndView = new float[16];
	private final float[] mtrxProjection = new float[16];
	private final float[] mtrxView = new float[16];
	private final float[] mtrxModel = new float[16];
	private final float[] mtrxModelViewProjection = new float[16];
	private GLText glText;
	private PointF selectOpponentLocation = new PointF();
	private PointF selectWeaponLocation = new PointF();
	private PointF selectSceneryLocation = new PointF();
	private RectF startButtonLocation = new RectF();
	private RectF backButtonLocation = new RectF();
	private int screenWidth;
	private int screenHeight;
	private TileShaderProgram tileShaderProgram;
	private Tile tile;

	/**
	 * Creates a new SetupScreen instance
	 *
	 * @param game  the {@link Game} instance
	 */
	public SetupScreen(Game game) {
		super(game);
	}

	@Override
	public void onCreate(int width, int height) {
		screenWidth = width;
		screenHeight = height;

		glViewport(0, 0, width, height);

		glText = new GLText(new TextShaderProgram((GLGame)game), ((GLGame)game).getAssets());
		glText.load("Roboto-Regular.ttf", height/36, 2, 2);
		float textHeight = glText.getHeight();

		selectOpponentLocation.x = (float)(width * 0.2);
		selectOpponentLocation.y = (float)(height * 0.8) - textHeight;

		selectWeaponLocation.x = selectOpponentLocation.x;
		selectWeaponLocation.y = (float)(height * 0.6) - textHeight;

		selectSceneryLocation.x = selectOpponentLocation.x;
		selectSceneryLocation.y = (float)(height * 0.4) - textHeight;

		float textWidth = glText.getLength(((GLGame)game).getResources().getString(R.string.start));
		startButtonLocation.left = width / 2 - textWidth / 2;
		startButtonLocation.right = startButtonLocation.left + textWidth;
		startButtonLocation.top = (float)(height * 0.2);
		startButtonLocation.bottom = startButtonLocation.top - textHeight;

		textWidth = glText.getLength(((GLGame)game).getResources().getString(R.string.back));
		backButtonLocation.left = width / 2 - textWidth / 2;
		backButtonLocation.right = backButtonLocation.left + textWidth;
		backButtonLocation.top = (float)(height * 0.1);
		backButtonLocation.bottom = backButtonLocation.top - textHeight;

		final float ratio = (float)width / height;
		if(width > height) {
			Matrix.frustumM(mtrxProjection, 0, -ratio, ratio, -1, 1, 1, 10);
		}
		else {
			Matrix.frustumM(mtrxProjection, 0, -1, 1, -1/ratio, 1/ratio, 1, 10);
		}
		Matrix.orthoM(mtrxView, 0, 0, 640, 0, 960, 0.1f, 10f);
		Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);

		mtrxModelViewProjection[0] = 0.0f;
		mtrxModelViewProjection[1] = 0.0f;
		mtrxModelViewProjection[2] = 0.0f;
		mtrxModelViewProjection[3] = 0.0f;
		mtrxModelViewProjection[4] = 0.0f;
		mtrxModelViewProjection[5] = 0.0f;
		mtrxModelViewProjection[6] = 0.0f;
		mtrxModelViewProjection[7] = 0.0f;
		mtrxModelViewProjection[8] = 0.0f;
		mtrxModelViewProjection[9] = 0.0f;
		mtrxModelViewProjection[10] = 0.25f;
		mtrxModelViewProjection[11] = 0.20f;
		mtrxModelViewProjection[12] = -0.05f;
		mtrxModelViewProjection[13] = -0.55f;
		mtrxModelViewProjection[14] = -0.98f;
		mtrxModelViewProjection[15] = 1.02f;

		Log.d(TAG, "onCreate: mtrxModelViewProjection = " + Geometry.printMatrix(mtrxModelViewProjection, 2));
		// enable texture + alpha blending
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		tileShaderProgram = new TileShaderProgram((GLGame)game);
		tile = new Tile((GLGame)game, R.drawable.butters);
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
				if(inBounds(worldCoords[0], worldCoords[1], startButtonLocation)) {
					game.setScreen(new GameScreen(game), true);
					if(Settings.soundEnabled) {
						Assets.click.play(1);
					}
					return;
				}
				if(inBounds(worldCoords[0], worldCoords[1], backButtonLocation)) {
					game.setScreen(new HomeScreen(game), true);
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

		glText.begin(1.0f, 1.0f, 1.0f, 1.0f, mtrxProjectionAndView);
		glText.draw(((GLGame)game).getResources().getString(R.string.select_opponent),
					selectOpponentLocation.x, selectOpponentLocation.y, 0.0f, 0.0f, 0.0f, 0.0f);
		glText.draw(((GLGame)game).getResources().getString(R.string.select_weapon),
					selectWeaponLocation.x, selectWeaponLocation.y, 0.0f, 0.0f,0.0f, 0.0f);
		glText.draw(((GLGame)game).getResources().getString(R.string.select_scenery),
					selectSceneryLocation.x, selectSceneryLocation.y, 0.0f, 0.0f,0.0f, 0.0f);
		glText.draw(((GLGame)game).getResources().getString(R.string.start),
					startButtonLocation.left, startButtonLocation.bottom, 0.0f, 0.0f,0.0f, 0.0f);
		glText.draw(((GLGame)game).getResources().getString(R.string.back),
					backButtonLocation.left, backButtonLocation.bottom, 0.0f, 0.0f,0.0f, 0.0f);
		glText.end();

		tile.setSelected(true);
		tileShaderProgram.useProgram();
		tileShaderProgram.setUniforms(mtrxModelViewProjection, tile.getTextureId(), Color.RED);
		tile.bindData(tileShaderProgram);
		tile.draw(tileShaderProgram);
		tile.unbindData(tileShaderProgram);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}
}
