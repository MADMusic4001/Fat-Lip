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
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.controller.rxhandlers.SceneryRxHandler;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.model.Scenery;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.activities.FatLipGame;
import com.madinnovations.fatlip.view.activities.FileSelectorDialogFragment;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.di.components.ScreenComponent;
import com.madinnovations.fatlip.view.di.modules.ScreenModule;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.utils.TextureHelper;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Renders the screen allowing the player to import a custom opponent
 */
@SuppressWarnings("WeakerAccess")
public class ImportSceneryScreen extends Screen implements FileSelectorDialogFragment.FileSelectorDialogListener {
	private static final String TAG = "ImportSceneryScreen";
	private static final String FILE_SELECTOR_FILTER = "fs_extension_filter";
	private static final String BITMAP_FILE_EXTENSION = ".png";
	@Inject
	protected SceneryRxHandler sceneryRxHandler;
	private   ConstraintLayout importSceneryScreenLayout;
	private   EditText         nameEditText;
	private   Button           browseButton;
	private   Button           saveButton;
	private   Button           backButton;
	private   ImageView        sceneryView;
	private   Scenery          scenery;
	private   String           sourcePath;

	/**
	 * Creates a new ImportOpponentScreen instance with the given Game
	 *
	 * @param game  a Game instance that the screen can use to access resources
	 */
	@SuppressLint("InflateParams")
	public ImportSceneryScreen(Game game) {
		super(game);
		ScreenComponent screenComponent = ((FatLipApp)((GLGame)game).getApplication()).getApplicationComponent()
				.newScreenComponent(new ScreenModule(this));
		screenComponent.injectInto(this);

		((GLGame)game).runOnUiThread(() -> {
			LinearLayout parentLayout = ((GLGame)game).getParentLayout();
			importSceneryScreenLayout = (ConstraintLayout)((GLGame)game).getLayoutInflater().inflate(
					R.layout.import_scenery_screen,null);
			parentLayout.addView(importSceneryScreenLayout);
			nameEditText = ((GLGame)game).findViewById(R.id.name_edit);
			initNameEdit();
			browseButton = ((GLGame)game).findViewById(R.id.select_file_button);
			initBrowseButton();
			sceneryView = ((GLGame)game).findViewById(R.id.scenery_view);
			saveButton = ((GLGame)game).findViewById(R.id.save_button);
			initSaveButton();
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
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void onFileSelected(String fileName) {
		if(fileName != null) {
			loadImage(fileName);
			String name = fileName;
			if(scenery != null && (scenery.getName() == null || scenery.getName().length() == 0)) {
				String separator = System.getProperty("file.separator");
				if (fileName.lastIndexOf(separator) != -1) {
					name = fileName.substring(fileName.lastIndexOf(separator) + 1);
				}
				if(name.lastIndexOf(".") != -1) {
					name = name.substring(0, name.lastIndexOf("."));
				}
				scenery.setName(name);
			}
			nameEditText.setText(name);
		}
	}

	private void loadImage(String fileName) {
		if(!fileName.endsWith(ImportSceneryScreen.BITMAP_FILE_EXTENSION)) {
			Toast.makeText((GLGame)game, R.string.not_png, Toast.LENGTH_LONG).show();
		}
		else {
			Bitmap bitmap = BitmapFactory.decodeFile(fileName);
			if(bitmap.getWidth() < 64 || bitmap.getHeight() < 64) {
				AlertDialog alertDialog = new AlertDialog.Builder((FatLipGame)game)
						.setTitle(R.string.import_failure)
						.setMessage(R.string.scenery_min_size_error)
						.setPositiveButton(R.string.ok, (dialog, which) -> {})
						.create();
				alertDialog.show();
				return;
			}

			if(bitmap.getWidth() > TextureHelper.getMaxTextureSize() || bitmap.getHeight() > TextureHelper.getMaxTextureSize()) {
				AlertDialog alertDialog = new AlertDialog.Builder((FatLipGame)game)
						.setTitle(R.string.import_failure)
						.setMessage(((GLGame)game).getString(R.string.scenery_max_size_error, TextureHelper.getMaxTextureSize()))
						.setPositiveButton(R.string.ok, (dialog, which) -> {})
						.create();
				alertDialog.show();
				return;
			}

			if((bitmap.getWidth() & (bitmap.getWidth() - 1)) != 0 || (bitmap.getHeight() & (bitmap.getHeight() - 1)) != 0) {
				AlertDialog alertDialog = new AlertDialog.Builder((FatLipGame)game)
						.setTitle(R.string.import_failure)
						.setMessage(R.string.scenery_pow2_size_error)
						.setPositiveButton(R.string.ok, (dialog, which) -> {})
						.create();
				alertDialog.show();
				return;
			}

			scenery = new Scenery();
			sourcePath = fileName;
			String separator = System.getProperty("file.separator");
			if (fileName.lastIndexOf(separator) == -1) {
				scenery.setImageFileName(fileName);
			}
			else {
				scenery.setImageFileName(fileName.substring(fileName.lastIndexOf(separator) + 1));
			}
			sceneryView.setImageBitmap(bitmap);
			saveButton.setEnabled(true);
		}
	}

	private void initNameEdit() {
		nameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable editable) {
				if(scenery != null && editable.length() > 0 && scenery != null) {
					scenery.setName(editable.toString());
				}
			}
		});
	}

	private void initBrowseButton() {
		browseButton.setOnClickListener(view -> {
			FileSelectorDialogFragment dialogFragment;
			dialogFragment = new FileSelectorDialogFragment();
			dialogFragment.setListener(ImportSceneryScreen.this);
			Bundle bundle = new Bundle();
			bundle.putString(FILE_SELECTOR_FILTER, BITMAP_FILE_EXTENSION);
			dialogFragment.setArguments(bundle);
			dialogFragment.show(((GLGame)game).getFragmentManager(), "FileSelectorDialog");
		});

	}

	private void initSaveButton() {
		saveButton.setEnabled(false);
		saveButton.setOnClickListener(v -> sceneryRxHandler.saveScenery(scenery, sourcePath)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(() -> Toast.makeText((GLGame)game, R.string.save_success, Toast.LENGTH_LONG).show(),
						   throwable -> {
					Toast.makeText((GLGame)game, R.string.save_error, Toast.LENGTH_LONG).show();
					Log.e(TAG, "An exception occurred saving the opponent", throwable);
				}));
	}

	private void initBackButton() {
		backButton.setOnClickListener(v -> (
				(GLGame)game).runOnUiThread(() -> {
					((GLGame)game).getParentLayout().removeView(importSceneryScreenLayout);
					((GLGame)game).getGlView().setVisibility(View.VISIBLE);
					game.setScreen(new HomeScreen(game), true);
					if(Settings.soundEnabled) {
						Assets.click.play(1);
					}
				})
		);
	}
}
