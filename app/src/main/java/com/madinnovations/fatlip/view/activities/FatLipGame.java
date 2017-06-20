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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.madinnovations.fatlip.controller.Game;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.FatLipRenderer;
import com.madinnovations.fatlip.view.FatLipSurfaceView;
import com.madinnovations.fatlip.view.GLGraphics;
import com.madinnovations.fatlip.view.GLRenderer;
import com.madinnovations.fatlip.view.HomeScreen;
import com.madinnovations.fatlip.view.Screen;
import com.madinnovations.fatlip.view.di.components.ActivityComponent;
import com.madinnovations.fatlip.view.di.modules.ActivityModule;

/**
 * The game Activity
 */
public class FatLipGame extends AppCompatActivity implements Game {
	private ActivityComponent activityComponent;
	enum GLGameState {
		Initialized,
		Running,
		Paused,
		Finished,
		Idle
	}
	FatLipSurfaceView glView;
	GLGraphics glGraphics;
	Screen screen;
	GLRenderer renderer;
	GLGameState state = GLGameState.Initialized;
	Object stateChanged = new Object();
	long startTime = System.nanoTime();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activityComponent = ((FatLipApp) getApplication()).getApplicationComponent()
				.newActivityComponent(new ActivityModule(this));
		activityComponent.injectInto(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		glView = new FatLipSurfaceView(this);
		renderer = new FatLipRenderer(this);
		glView.setRenderer(renderer);
		setContentView(glView);
		glGraphics = new GLGraphics(glView);

	}

	@Override
	protected void onResume() {
		super.onResume();
		glView.onResume();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public ActivityComponent getActivityComponent() {
		return activityComponent;
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
}
