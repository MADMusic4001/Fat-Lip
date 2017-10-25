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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.madinnovations.fatlip.Constants;
import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.controller.rxhandlers.OpponentRxHandler;
import com.madinnovations.fatlip.controller.rxhandlers.SceneryRxHandler;
import com.madinnovations.fatlip.controller.rxhandlers.WeaponRxHandler;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.model.Opponent;
import com.madinnovations.fatlip.model.Scenery;
import com.madinnovations.fatlip.model.Weapon;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.di.components.ScreenComponent;
import com.madinnovations.fatlip.view.di.modules.ScreenModule;
import com.madinnovations.fatlip.view.framework.Screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * UI for selecting opponent, object and scenery for the game
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SetupScreen extends Screen {
	private static final String TAG = "SetupScreen";
	@Inject
	protected OpponentRxHandler opponentRxHandler;
	@Inject
	protected SceneryRxHandler  sceneryRxHandler;
	@Inject
	protected WeaponRxHandler   weaponRxHandler;
	private   ConstraintLayout  setupScreenLayout;
	private   LinearLayout      opponentsLayout;
	private   LinearLayout      weaponsLayout;
	private   LinearLayout      sceneryLayout;
	private   Button            backButton;
	private   Button            startButton;
	private   ImageView         selectedOpponent;

	/**
	 * Creates a new SetupScreen instance
	 *
	 * @param game  the {@link Game} instance
	 */
	@SuppressLint("InflateParams")
	@Inject
	public SetupScreen(final Game game) {
		super(game);
		ScreenComponent screenComponent = ((FatLipApp)((GLGame)game).getApplication()).getApplicationComponent()
				.newScreenComponent(new ScreenModule(this));
		screenComponent.injectInto(this);

		((GLGame)game).runOnUiThread(() -> {
			LinearLayout parentLayout = ((GLGame)game).getParentLayout();
			setupScreenLayout = (ConstraintLayout)((GLGame)game).getLayoutInflater().inflate(R.layout.setup_screen,
																							null);
			parentLayout.addView(setupScreenLayout);
			opponentsLayout = ((GLGame)game).findViewById(R.id.opponents_layout);
			initOpponentsLayout();
			weaponsLayout = ((GLGame)game).findViewById(R.id.weapons_layout);
			initWeaponsLayout();
			sceneryLayout = ((GLGame)game).findViewById(R.id.scenery_layout);
			initSceneryLayout();
			backButton = ((GLGame)game).findViewById(R.id.back_button);
			initBackButton();
			startButton = ((GLGame)game).findViewById(R.id.start_button);
			initStartButton();
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
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	private void initOpponentsLayout() {
		opponentRxHandler.loadOpponents()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(result -> {
					boolean first = true;
					for(Opponent opponent : result) {
						InputStream is;
						try {
							if(opponent.isCustom()) {
								is = new FileInputStream(new File(Constants.getOpponentsOutputDir((GLGame)game),
																  opponent.getImageFileName()));
							}
							else {
								is = game.getFileIO().readAsset("opponents/" + opponent.getImageFileName());
							}
						}
						catch (IOException e) {
							throw new RuntimeException(e);
						}
						Bitmap bitmap = BitmapFactory.decodeStream(is);
						final ImageView imageView = new ImageView((GLGame) game);
						imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 128, 128, true));
						imageView.setPadding(5, 5, 5, 5);
						if(first) {
							imageView.setSelected(true);
							imageView.setBackgroundColor(Color.RED);
							first = false;
						}
						imageView.setOnClickListener(v -> {
							for(int i = 0; i < opponentsLayout.getChildCount(); i++) {
								View childView = opponentsLayout.getChildAt(i);
								if(childView != imageView) {
									childView.setSelected(false);
									childView.setBackgroundColor(Color.TRANSPARENT);
								}
								else {
									if (imageView.isSelected()) {
										imageView.setBackgroundColor(Color.TRANSPARENT);
										imageView.setSelected(false);
									}
									else {
										imageView.setBackgroundColor(Color.RED);
										imageView.setSelected(true);
									}
								}
							}
						});
						opponentsLayout.addView(imageView);
					}
		});
	}

	private void initWeaponsLayout() {
		weaponRxHandler.loadWeapons()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(result -> {
					boolean first = true;
					Log.d(TAG, "initWeaponsLayout: weapons count = " + result.size());
					for(Weapon weapon : result) {
						InputStream is;
						try {
							is = game.getFileIO().readAsset("weapons/" + weapon.getImageFileName());
						}
						catch (IOException e) {
							throw new RuntimeException(e);
						}
						Bitmap bitmap = BitmapFactory.decodeStream(is);
						final ImageView imageView = new ImageView((GLGame) game);
						imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 128, 128, true));
						imageView.setPadding(5, 5, 5, 5);
						if(first) {
							imageView.setSelected(true);
							imageView.setBackgroundColor(Color.RED);
							first = false;
						}
						imageView.setOnClickListener(v -> {
							for(int i = 0; i < weaponsLayout.getChildCount(); i++) {
								View childView = weaponsLayout.getChildAt(i);
								if(childView != imageView) {
									childView.setSelected(false);
									childView.setBackgroundColor(Color.TRANSPARENT);
								}
								else {
									if (imageView.isSelected()) {
										imageView.setBackgroundColor(Color.TRANSPARENT);
										imageView.setSelected(false);
									}
									else {
										imageView.setBackgroundColor(Color.RED);
										imageView.setSelected(true);
									}
								}
							}
						});
						weaponsLayout.addView(imageView);
					}
				});
	}

	private void initSceneryLayout() {
		sceneryRxHandler.loadScenery()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(result -> {
					boolean first = true;
					for(Scenery scenery : result) {
						InputStream is;
						try {
							if(scenery.isCustom()) {
								is = new FileInputStream(new File(Constants.getSceneryOutputDir((GLGame)game),
																  scenery.getImageFileName()));
							}
							else {
								is = game.getFileIO().readAsset("scenery/" + scenery.getImageFileName());
							}
						}
						catch (IOException e) {
							throw new RuntimeException(e);
						}
						Bitmap bitmap = BitmapFactory.decodeStream(is);
						final ImageView imageView = new ImageView((GLGame) game);
						imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 128, 128, true));
						imageView.setPadding(5, 5, 5, 5);
						if(first) {
							imageView.setSelected(true);
							imageView.setBackgroundColor(Color.RED);
							first = false;
						}
						imageView.setOnClickListener(v -> {
							for(int i = 0; i < sceneryLayout.getChildCount(); i++) {
								View childView = sceneryLayout.getChildAt(i);
								if(childView != imageView) {
									childView.setSelected(false);
									childView.setBackgroundColor(Color.TRANSPARENT);
								}
								else {
									if (imageView.isSelected()) {
										imageView.setBackgroundColor(Color.TRANSPARENT);
										imageView.setSelected(false);
									}
									else {
										imageView.setBackgroundColor(Color.RED);
										imageView.setSelected(true);
									}
								}
							}
						});
						sceneryLayout.addView(imageView);
					}
				});
	}

	private void initBackButton() {
		backButton.setOnClickListener(view -> ((GLGame)game).runOnUiThread(() -> {
			((GLGame)game).getParentLayout().removeView(setupScreenLayout);
			((GLGame)game).getGlView().setVisibility(View.VISIBLE);
			game.setScreen(new HomeScreen(game), true);
			if(Settings.soundEnabled) {
				Assets.click.play(1);
			}
		}));
	}

	private void initStartButton() {
		startButton.setOnClickListener(view -> ((GLGame)game).runOnUiThread(() -> {
			((GLGame)game).getParentLayout().removeView(setupScreenLayout);
			((GLGame)game).getGlView().setVisibility(View.VISIBLE);
			game.setScreen(new GameScreen(game), true);
			if(Settings.soundEnabled) {
				Assets.click.play(1);
			}
		}));
	}

	@Override
	public void showScreen() {
		((GLGame)game).getParentLayout().addView(setupScreenLayout);
	}

	@Override
	public void hideScreen() {
		((GLGame)game).getParentLayout().removeView(setupScreenLayout);
	}
}
