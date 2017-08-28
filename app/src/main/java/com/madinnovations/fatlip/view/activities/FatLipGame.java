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

import com.madinnovations.fatlip.controller.framework.impl.GLGame;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.di.components.ActivityComponent;
import com.madinnovations.fatlip.view.di.modules.ActivityModule;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.screens.HomeScreen;

import javax.inject.Inject;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * The game Activity
 */
@SuppressWarnings("unused")
public class FatLipGame extends GLGame {
	private static final String TAG = "FatLipGame";
	private ActivityComponent activityComponent;
	private boolean firstTimeCreate = true;
	@Inject
	protected HomeScreen homeScreen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityComponent = ((FatLipApp) getApplication()).getApplicationComponent()
				.newActivityComponent(new ActivityModule(this));
		activityComponent.injectInto(this);
	}

	@Override
	public Screen getStartScreen() {
		return homeScreen;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		super.onSurfaceCreated(unused, config);
		if(firstTimeCreate) {
			Assets.load(this);
			firstTimeCreate = false;
		}
		else {
			Assets.reload();
		}
	}

	public ActivityComponent getActivityComponent() {
		return activityComponent;
	}
}
