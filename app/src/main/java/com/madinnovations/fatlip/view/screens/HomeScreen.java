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
import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.madinnovations.fatlip.Constants;
import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.view.activities.FatLipGame;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.framework.Screen;

/**
 * Renders the Home screen
 */
@SuppressWarnings("WeakerAccess")
public class HomeScreen extends Screen {
	private ConstraintLayout homeScreenLayout;
	private Button           playButton;
	private Button           helpButton;
	private Button           importOpponentButton;
	private Button           importSceneryButton;
	private ListView         highScoresListView;

	@SuppressLint("InflateParams")
	public HomeScreen(final Game game) {
		super(game);
		((GLGame)game).runOnUiThread(() -> homeScreenLayout = (ConstraintLayout)((GLGame)game).getLayoutInflater()
				.inflate(R.layout.home_screen, null));
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

	private void initPlayButton() {
		playButton.setOnClickListener(view -> ((GLGame)game).runOnUiThread(() -> {
			game.setScreen(new SetupScreen(game), true);
			if(Settings.soundEnabled) {
				Assets.click.play(1);
			}
		}));
	}

	private void initHelpButton() {
		helpButton.setOnClickListener(view -> ((GLGame)game).runOnUiThread(() -> {
			game.setScreen(new HelpScreen(game), true);
			if(Settings.soundEnabled) {
				Assets.click.play(1);
			}
		}));
	}

	private void initImportOpponentButton() {
		importOpponentButton.setOnClickListener(view -> ((GLGame)game).runOnUiThread(() -> {
			game.setScreen(new ImportOpponentScreen(game), true);
			if(Settings.soundEnabled) {
				Assets.click.play(1);
			}
		}));
	}

	private void initImportSceneryButton() {
		importSceneryButton.setOnClickListener(view -> ((GLGame)game).runOnUiThread(() -> {
			game.setScreen(new ImportSceneryScreen(game), true);
			if(Settings.soundEnabled) {
				Assets.click.play(1);
			}
		}));
	}

	private void initHighScoresList() {
		ArrayAdapter<Integer> highScoresArrayAdapter = new ArrayAdapter<>((FatLipGame)game,
																		  android.R.layout.simple_list_item_1);
		SharedPreferences highScoresPreferences = ((GLGame)game).getSharedPreferences(Constants.HIGH_SCORES_PREFS_NAME,
				Context.MODE_PRIVATE);
		int score = highScoresPreferences.getInt(Constants.HIGH_SCORE_1, 0);
		highScoresArrayAdapter.add(score);
		score = highScoresPreferences.getInt(Constants.HIGH_SCORE_2, 0);
		highScoresArrayAdapter.add(score);
		score = highScoresPreferences.getInt(Constants.HIGH_SCORE_3, 0);
		highScoresArrayAdapter.add(score);
		score = highScoresPreferences.getInt(Constants.HIGH_SCORE_4, 0);
		highScoresArrayAdapter.add(score);
		score = highScoresPreferences.getInt(Constants.HIGH_SCORE_5, 0);
		highScoresArrayAdapter.add(score);
		highScoresListView.setAdapter(highScoresArrayAdapter);
	}

	@Override
	public void showScreen() {
		((GLGame)game).runOnUiThread(() -> {
			((GLGame)game).getParentLayout().addView(homeScreenLayout);
			playButton = ((GLGame)game).findViewById(R.id.play_button);
			initPlayButton();
			helpButton = ((GLGame)game).findViewById(R.id.help_button);
			initHelpButton();
			importOpponentButton = ((GLGame)game).findViewById(R.id.import_opponent_button);
			initImportOpponentButton();
			importSceneryButton = ((GLGame)game).findViewById(R.id.import_scenery_button);
			initImportSceneryButton();
			highScoresListView = ((GLGame)game).findViewById(R.id.high_scores_listview);
			initHighScoresList();
		});
	}

	@Override
	public void hideScreen() {
		((GLGame)game).runOnUiThread(() -> ((GLGame)game).getParentLayout().removeView(homeScreenLayout));
	}
}
