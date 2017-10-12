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

import com.madinnovations.fatlip.view.di.components.ActivityComponent;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.screens.LoadingScreen;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * The game Activity
 */
@SuppressWarnings("unused")
public class FatLipGame extends GLGame {
	private static final String TAG = "FatLipGame";
	private ActivityComponent activityComponent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Screen getStartScreen() {
		return new LoadingScreen(this);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		super.onSurfaceCreated(unused, config);
	}

	public ActivityComponent getActivityComponent() {
		return activityComponent;
	}
}
