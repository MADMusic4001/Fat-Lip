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

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.framework.Screen;

/**
 * Renders the help screen
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class HelpScreen extends Screen {
	private static final String TAG = "HelpScreen";
	private ConstraintLayout helpScreenLayout;
	private Button backButton;

	/**
	 * Creates a new HelpScreen instance
	 *
	 * @param game  a (@link Game} instance
	 */
	@SuppressLint("InflateParams")
	public HelpScreen(final Game game) {
		super(game);
		((GLGame)game).runOnUiThread(() -> {
			LinearLayout parentLayout = ((GLGame)game).getParentLayout();
			helpScreenLayout = (ConstraintLayout)((GLGame)game).getLayoutInflater().inflate(R.layout.help_screen,
																							null);
			parentLayout.addView(helpScreenLayout);
			backButton = ((GLGame)game).findViewById(R.id.back_button);
			initBackButton();
			((GLGame)game).getGlView().setVisibility(View.GONE);
		});
	}

	@Override
	public void onCreate(int width, int height) {
	}

	@Override
	public void update(float deltaTime) {
	}

	@Override
	public void present(float deltaTime) {
	}

	@Override
	public void pause() { }

	@Override
	public void resume() {
		GLES20.glClearColor(0f, 1f, 0f, 1f);
	}

	@Override
	public void dispose() { }

	private void initBackButton() {
		backButton.setOnClickListener(view -> ((GLGame)game).runOnUiThread(() -> {
			((GLGame)game).getParentLayout().removeView(helpScreenLayout);
			((GLGame)game).getGlView().setVisibility(View.VISIBLE);
			game.setScreen(new HomeScreen(game), true);
			if(Settings.soundEnabled) {
				Assets.click.play(1);
			}
		}));
	}

	@Override
	public void showScreen() {
		((GLGame)game).getParentLayout().addView(helpScreenLayout);
	}

	@Override
	public void hideScreen() {
		((GLGame)game).getParentLayout().removeView(helpScreenLayout);
	}
}
