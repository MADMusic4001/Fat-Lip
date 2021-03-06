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

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.Toast;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.rxhandlers.OpponentRxHandler;
import com.madinnovations.fatlip.controller.rxhandlers.SceneryRxHandler;
import com.madinnovations.fatlip.controller.rxhandlers.WeaponRxHandler;
import com.madinnovations.fatlip.model.SetupInfo;
import com.madinnovations.fatlip.model.Splash;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.di.components.ScreenComponent;
import com.madinnovations.fatlip.view.di.modules.ScreenModule;
import com.madinnovations.fatlip.view.framework.FramesPerSecondLogger;
import com.madinnovations.fatlip.view.framework.GLOpponent;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.programs.SplashShaderProgram;
import com.madinnovations.fatlip.view.programs.TextureShaderProgram;
import com.madinnovations.fatlip.view.utils.TextureHelper;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;

/**
 * Renders the game screen
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class GameScreen extends Screen {
	private static final String                TAG       = "GameScreen";
	@Inject
	protected OpponentRxHandler   opponentRxHandler;
	@Inject
	protected SceneryRxHandler    sceneryRxHandler;
	@Inject
	protected WeaponRxHandler     weaponRxHandler;
	private GLSurfaceView         glView;
	private FramesPerSecondLogger fpsLogger = new FramesPerSecondLogger();
	private SetupInfo             setupInfo;
	private Bitmap                opponentBitmap = null;
	private Bitmap                sceneryBitmap  = null;
	private Bitmap                weaponBitmap   = null;
	private GLOpponent            glOpponent     = null;
	private Splash                splash         = null;
	private int                   opponentTextureId;
	private int                   splashTextureId;
	private int                   weaponTextureId;
	private SplashShaderProgram   splashProgram = null;
	private TextureShaderProgram  textureShaderProgram = null;
	private final float[]         viewMatrix = new float[16];
	private final float[]         viewProjectionMatrix = new float[16];
	private final float[]         modelViewProjectionMatrix = new float[16];

	/**
	 * Creates a new GameScreen instance
	 *
	 * @param game  a Game instance.
	 */
	public GameScreen(Game game) {
		super(game);
		ScreenComponent screenComponent = ((FatLipApp)((GLGame)game).getApplication()).getApplicationComponent()
				.newScreenComponent(new ScreenModule(this));
		screenComponent.injectInto(this);
	}

	@Override
	public void onCreate(int width, int height) {
		Log.d(TAG, "onCreate: ");
		glClearColor(0.5f, 0.5f, 0.5f, 0.0f);

		glViewport(0, 0, width, height);

		float[] projectionMatrix = new float[16];
		perspectiveM(projectionMatrix, 0,45, (float)width/(float)height, 1f, 10f);
		setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f,
						  0f);
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		float[] modelMatrix = new float[16];
		setIdentityM(modelMatrix, 0);
		multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
	}

	@Override
	public void update(float deltaTime) {
		if(splash == null && sceneryBitmap != null) {
			initScenery();
		}
		if(glOpponent == null && opponentBitmap != null) {
			initOpponent();
		}
	}

	@Override
	public void present(float deltaTime) {
		fpsLogger.logFrame();
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		if(splashProgram != null) {
			splashProgram.useProgram();
			splashProgram.setUniforms(splashTextureId);
			splash.bindData(splashProgram);
			splash.draw();
			splash.unbindData(splashProgram);
		}

		if(textureShaderProgram != null) {
			textureShaderProgram.useProgram();
			textureShaderProgram.setUniforms(modelViewProjectionMatrix, opponentTextureId);
			glOpponent.bindData(textureShaderProgram);
			glOpponent.draw();
			glOpponent.unbindData(textureShaderProgram);
		}
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

	@Override
	public void showScreen() {
		if(glView == null) {
			((GLGame) game).runOnUiThread(() -> {
				((GLGame)game).getParentLayout().removeAllViews();
				glView = new GLSurfaceView(((GLGame) game));
				((GLGame) game).getParentLayout().addView(glView);
				glView.setEGLContextClientVersion(2);
				glView.setRenderer(((GLGame) game));
				glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
				initScenery();
			});
		}
	}

	@Override
	public void hideScreen() {
		((GLGame)game).runOnUiThread(() -> {
			((GLGame)game).getParentLayout().removeView(glView);
			glView = null;
		});
	}

	private void initOpponent() {
		if(opponentBitmap != null) {
			opponentTextureId = TextureHelper.loadTexture(opponentBitmap);
			if(textureShaderProgram == null) {
				textureShaderProgram = new TextureShaderProgram((GLGame)game);
				glOpponent = new GLOpponent(setupInfo.getOpponent(), opponentBitmap);
			}
		}
	}

	private void initScenery() {
		if(sceneryBitmap != null) {
			splash = new Splash();
			splashTextureId = TextureHelper.loadTexture(sceneryBitmap);
			splashProgram = new SplashShaderProgram((GLGame) game);
		}
	}

	// Getters and setters
	public SetupInfo getSetupInfo() {
		return setupInfo;
	}
	public void setSetupInfo(SetupInfo setupInfo) {
		this.setupInfo = setupInfo;
		sceneryRxHandler.readSceneryBitmap(setupInfo.getScenery())
				.observeOn(Schedulers.io())
				.subscribeOn(AndroidSchedulers.mainThread())
				.subscribe(new SingleObserver<Bitmap>() {
					@Override
					public void onSubscribe(Disposable d) {}
					@Override
					public void onSuccess(Bitmap bitmap) {
						sceneryBitmap = bitmap;
					}
					@Override
					public void onError(Throwable e) {
						Log.e(TAG, "onError: Exception caught loading scenery bitmap", e);
						Toast.makeText((GLGame)GameScreen.this.game, R.string.scenery_read_fail, Toast.LENGTH_LONG).show();
					}
				});
		opponentRxHandler.readOpponentBitmap(setupInfo.getOpponent())
				.subscribe(new SingleObserver<Bitmap>() {
					@Override
					public void onSubscribe(Disposable d) {}
					@Override
					public void onSuccess(Bitmap bitmap) {
						opponentBitmap = bitmap;
					}
					@Override
					public void onError(Throwable e) {
						Log.e(TAG, "onError: Exception caught reading opponent bitmap", e);
						Toast.makeText((GLGame)GameScreen.this.game, R.string.opponent_read_fail, Toast.LENGTH_LONG).show();
					}
				});
		weaponRxHandler.readWeaponBitmap(setupInfo.getWeapon())
				.subscribe(new SingleObserver<Bitmap>() {
					@Override
					public void onSubscribe(Disposable d) {}
					@Override
					public void onSuccess(Bitmap bitmap) {
						weaponBitmap = bitmap;
					}
					@Override
					public void onError(Throwable e) {
						Log.e(TAG, "onError: Exception caught reading weapon bitmap", e);
						Toast.makeText((GLGame)GameScreen.this.game, R.string.weapon_read_fail, Toast.LENGTH_LONG).show();
					}
				});
	}
}
