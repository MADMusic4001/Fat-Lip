/*
  Copyright (C) 2017 MadInnovations
  <p/>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p/>
  http://www.apache.org/licenses/LICENSE-2.0
  <p/>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.madinnovations.fatlip.view.activities;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.madinnovations.fatlip.controller.framework.Audio;
import com.madinnovations.fatlip.controller.framework.FileIO;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Input;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.FatLipSurfaceView;
import com.madinnovations.fatlip.view.GLGraphics;
import com.madinnovations.fatlip.view.HomeScreen;
import com.madinnovations.fatlip.view.Screen;
import com.madinnovations.fatlip.view.di.components.ActivityComponent;
import com.madinnovations.fatlip.view.di.modules.ActivityModule;
import com.madinnovations.fatlip.view.framework.Graphics;
import com.madinnovations.fatlip.view.utils.GLGraphicTools;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.ContentValues.TAG;

/**
 * The game Activity
 */
public class FatLipGame extends AppCompatActivity implements Game, Renderer {
	private ActivityComponent activityComponent;
	private enum GLGameState {
		Initialized,
		Running,
		Paused,
		Finished,
		Idle
	}
	private FatLipSurfaceView glView;
	private GLGraphics glGraphics;
	private Audio audio;
	private Input input;
	private FileIO fileIO;
	private Screen screen;
	private GLGameState state = GLGameState.Initialized;
	private final Object stateChanged = new Object();
	private long startTime = System.nanoTime();
	private boolean firstDraw;
	private boolean surfaceCreated;
	private int width;
	private int height;
	private long lastTime;
	private int fps;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activityComponent = ((FatLipApp) getApplication()).getApplicationComponent()
				.newActivityComponent(new ActivityModule(this));
		activityComponent.injectInto(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		firstDraw = true;
		surfaceCreated = false;
		width = -1;
		height = -1;
		lastTime = System.currentTimeMillis();
		fps = 0;

		glView = new FatLipSurfaceView(this);
		glView.setRenderer(this);
		setContentView(glView);
		glGraphics = new GLGraphics(glView);

	}

	@Override
	protected void onResume() {
		super.onResume();
		glView.onResume();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		Log.i(TAG, "onSurfaceCreated: Surface created.");
		surfaceCreated = true;
		width = -1;
		height = -1;
		// Text shader
		int vshadert = GLGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
												 GLGraphicTools.vs_Text);
		int fshadert = GLGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
												 GLGraphicTools.fs_Text);

		GLGraphicTools.sp_Text = GLES20.glCreateProgram();
		GLES20.glAttachShader(GLGraphicTools.sp_Text, vshadert);
		GLES20.glAttachShader(GLGraphicTools.sp_Text, fshadert);
		GLES20.glLinkProgram(GLGraphicTools.sp_Text);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		if (!surfaceCreated && width == this.width
				&& height == this.height) {
			Log.i(TAG, "onSurfaceChanged: Surface changed but already handled.");
			return;
		}
		// Android honeycomb has an option to keep the
		// context.
		String msg = "Surface changed width:" + width
				+ " height:" + height;
		if (surfaceCreated) {
			msg += " context lost.";
		} else {
			msg += ".";
		}
		Log.i(TAG, "onSurfaceChanged: " + msg);

		this.width = width;
		this.height = height;

		screen.onCreate(this.width, this.height, surfaceCreated);
		surfaceCreated = false;
	}

	@Override
	public void onDrawFrame(GL10 unused) {

	}

	@Override
	protected void onPause() {
		synchronized (stateChanged) {
			if(isFinishing()) {
				state = GLGameState.Finished;
			}
			else {
				state = GLGameState.Paused;
			}
			while (true) {
				try {
					stateChanged.wait();
					break;
				}
				catch (InterruptedException ignored) {}
			}
		}
		glView.onPause();
		super.onPause();
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	@Override
	public Screen getCurrentScreen() {
		if(screen == null) {
			return getStartScreen();
		}
		return screen;
	}

	@Override
	public Screen getStartScreen() {
		screen = new HomeScreen(this);
		return screen;
	}

	public GLGraphics getGLGraphics() {
		return glGraphics;
	}

	@Override
	public Input getInput() {
		return input;
	}

	@Override
	public FileIO getFileIO() {
		return fileIO;
	}

	@Override
	public Graphics getGraphics() {
		throw new IllegalStateException("We are using OpenGL!");
	}

	@Override
	public Audio getAudio() {
		return audio;
	}

	public ActivityComponent getActivityComponent() {
		return activityComponent;
	}
}
