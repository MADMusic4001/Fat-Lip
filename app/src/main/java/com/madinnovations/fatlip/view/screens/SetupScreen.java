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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.controller.rxhandlers.OpponentRxHandler;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.model.Opponent;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.di.components.ScreenComponent;
import com.madinnovations.fatlip.view.di.modules.ScreenModule;
import com.madinnovations.fatlip.view.framework.Screen;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * UI for selecting opponent, object and scenery for the game
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SetupScreen extends Screen {
	private static final String TAG = "SetupScreen";
	@Inject
	protected OpponentRxHandler opponentRxHandler;
	private ConstraintLayout setupScreenLayout;
	private LinearLayout opponentsLayout;
	private Spinner throwingObjectSpinner;
	private Spinner scenerySpinner;
	private Button backButton;
	private Button startButton;
	private ImageView selectedOpponent;

	/**
	 * Creates a new SetupScreen instance
	 *
	 * @param game  the {@link Game} instance
	 */
	@Inject
	public SetupScreen(final Game game) {
		super(game);
		ScreenComponent screenComponent = ((FatLipApp)((GLGame)game).getApplication()).getApplicationComponent()
				.newScreenComponent(new ScreenModule(this));
		screenComponent.injectInto(this);

		((GLGame)game).runOnUiThread(new Runnable() {
			@SuppressLint("InflateParams")
			@Override
			public void run() {
				LinearLayout parentLayout = ((GLGame)game).getParentLayout();
				setupScreenLayout = (ConstraintLayout)((GLGame)game).getLayoutInflater().inflate(R.layout.setup_screen,
																								null);
				parentLayout.addView(setupScreenLayout);
				opponentsLayout = ((GLGame)game).findViewById(R.id.opponents_layout);
				initOpponentsLayout();
				throwingObjectSpinner = ((GLGame)game).findViewById(R.id.throwing_objects_spinner);
				scenerySpinner = ((GLGame)game).findViewById(R.id.scenery_spinner);
				backButton = ((GLGame)game).findViewById(R.id.back_button);
				initBackButton();
				startButton = ((GLGame)game).findViewById(R.id.start_button);
				initStartButton();
				((GLGame)game).getGlView().setVisibility(View.GONE);
			}
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
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	private void initOpponentsLayout() {
		final List<List<Opponent>> opponentsWrapper = new ArrayList<>();
		opponentRxHandler.loadOpponents().subscribe(opponentsWrapper::add);
		List<Opponent> opponents = opponentsWrapper.get(0);
		for(Opponent opponent : opponents) {
			Bitmap bitmap = BitmapFactory.decodeFile(opponent.getImageFileName());
			final ImageView imageView = new ImageView((GLGame)game);
			imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 128, 128, true));
			imageView.setPadding(5, 5, 5, 5);
			imageView.setSelected(true);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(imageView.isSelected()) {
						imageView.setBackgroundColor(Color.WHITE);
						imageView.setSelected(false);
					}
					else {
						imageView.setBackgroundColor(Color.RED);
						imageView.setSelected(true);
					}
				}
			});
			opponentsLayout.addView(imageView);
		}
	}

	private void initBackButton() {
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((GLGame)game).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((GLGame)game).getParentLayout().removeView(setupScreenLayout);
						((GLGame)game).getGlView().setVisibility(View.VISIBLE);
						game.setScreen(new HomeScreen(game), true);
						if(Settings.soundEnabled) {
							Assets.click.play(1);
						}
					}
				});
			}
		});
	}

	private void initStartButton() {
		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((GLGame)game).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((GLGame)game).getParentLayout().removeView(setupScreenLayout);
						((GLGame)game).getGlView().setVisibility(View.VISIBLE);
						game.setScreen(new GameScreen(game), true);
						if(Settings.soundEnabled) {
							Assets.click.play(1);
						}
					}
				});
			}
		});
	}
}
