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
import android.view.View;
import android.widget.Toast;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.rxhandlers.SceneryRxHandler;
import com.madinnovations.fatlip.model.SetupInfo;
import com.madinnovations.fatlip.model.Splash;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.di.components.ScreenComponent;
import com.madinnovations.fatlip.view.di.modules.ScreenModule;
import com.madinnovations.fatlip.view.framework.FramesPerSecondLogger;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.programs.SplashShaderProgram;
import com.madinnovations.fatlip.view.utils.TextureHelper;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Renders the game screen
 */
@SuppressWarnings("WeakerAccess")
public class GameScreen extends Screen {
	private static final String                TAG       = "GameScreen";
	@Inject
	protected SceneryRxHandler    sceneryRxHandler;
	private GLSurfaceView         glView;
	private FramesPerSecondLogger fpsLogger = new FramesPerSecondLogger();
	private SetupInfo             setupInfo;
	private Bitmap                sceneryBitmap = null;
	private Splash                splash = null;
	private int                   splashTextureId;
	private SplashShaderProgram   splashProgram = null;

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
	}

	@Override
	public void update(float deltaTime) {
		if(splash == null && sceneryBitmap != null) {
			initScenery();
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

	private void initScenery() {
		if(sceneryBitmap != null) {
			splash = new Splash();
			splashTextureId = TextureHelper.loadTexture(sceneryBitmap);
			Log.d(TAG, "onSuccess: splashTextureId = " + splashTextureId);
			splashProgram = new SplashShaderProgram((GLGame) game);
			Log.d(TAG, "onSuccess: splashProgramId = " + splashProgram.getProgram());
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
	}
}
