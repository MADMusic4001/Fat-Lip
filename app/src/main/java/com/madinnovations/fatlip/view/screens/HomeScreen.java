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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
 *
 */
@SuppressWarnings("WeakerAccess")
public class HomeScreen extends Screen {
	private ConstraintLayout homeScreenLayout;
	private Button playButton;
	private Button helpButton;
	private ListView highScoresListview;

	public HomeScreen(final Game game) {
		super(game);
		((GLGame)game).runOnUiThread(new Runnable() {
			@SuppressLint("InflateParams")
			@Override
			public void run() {
				LinearLayout parentLayout = ((GLGame)game).getParentLayout();
				homeScreenLayout = (ConstraintLayout)((GLGame)game).getLayoutInflater().inflate(R.layout.home_screen,
						null);
				parentLayout.addView(homeScreenLayout);
				playButton = ((GLGame)game).findViewById(R.id.play_button);
				initPlayButton();
				helpButton = ((GLGame)game).findViewById(R.id.help_button);
				initHelpButton();
				highScoresListview = ((GLGame)game).findViewById(R.id.high_scores_listview);
				initHighScoresList();
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

	private void initPlayButton() {
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((GLGame)game).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((GLGame)game).getParentLayout().removeView(homeScreenLayout);
						((GLGame)game).getGlView().setVisibility(View.VISIBLE);
						game.setScreen(new SetupScreen(game), true);
						if(Settings.soundEnabled) {
							Assets.click.play(1);
						}
					}
				});
			}
		});
	}

	private void initHelpButton() {
		helpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((GLGame)game).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((GLGame)game).getParentLayout().removeView(homeScreenLayout);
						((GLGame)game).getGlView().setVisibility(View.VISIBLE);
						game.setScreen(new HelpScreen(game), true);
						if(Settings.soundEnabled) {
							Assets.click.play(1);
						}
					}
				});
			}
		});
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
		highScoresListview.setAdapter(highScoresArrayAdapter);
	}
}
