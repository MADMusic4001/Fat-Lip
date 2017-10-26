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

package com.madinnovations.fatlip.controller.rxhandlers;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.madinnovations.fatlip.Constants;
import com.madinnovations.fatlip.model.Opponent;
import com.madinnovations.fatlip.model.Scenery;
import com.madinnovations.fatlip.model.serializers.OpponentSerializer;
import com.madinnovations.fatlip.view.FatLipApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * ReactiveX handler that reads and writes opponents.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class OpponentRxHandler {
	private static final String TAG = "OpponentRxHandler";
	private FatLipApp          fatLipApp;
	private AssetManager       assetManager;
	private OpponentSerializer opponentSerializer;

	/**
	 * Creates a new OpponentRxHandler
	 *
	 * @param assetManager the {@link AssetManager} instance
	 */
	@Inject
	public OpponentRxHandler(FatLipApp fatLipApp, AssetManager assetManager, OpponentSerializer opponentSerializer) {
		this.fatLipApp = fatLipApp;
		this.assetManager = assetManager;
		this.opponentSerializer = opponentSerializer;
	}

	public Flowable<List<Opponent>> loadOpponents() {
		return Flowable.fromCallable(() -> {
			String[] opponentFileNames = assetManager.list("opponents");
			List<Opponent> opponents = new ArrayList<>();
			for(String opponentFileName : opponentFileNames) {
				if(opponentFileName.endsWith("json")) {
					InputStream is = assetManager.open("opponents/" + opponentFileName);
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(Opponent.class, opponentSerializer);
					gsonBuilder.setLenient();
					final Gson gson = gsonBuilder.create();

					JsonReader jsonReader = gson.newJsonReader(bufferedReader);
					Opponent opponent = gson.fromJson(jsonReader, Opponent.class);
					if(fatLipApp.getUser().getUnlockedOpponents().contains(opponent.getName())) {
						opponents.add(opponent);
					}
					is.close();
				}
			}

			File opponentsDir = Constants.getOpponentsOutputDir(fatLipApp);
			opponentFileNames = opponentsDir.list();
			for(String opponentFileName : opponentFileNames) {
				if(opponentFileName.endsWith("json")) {
					InputStream is = new FileInputStream(new File(opponentsDir, opponentFileName));
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(Opponent.class, opponentSerializer);
					gsonBuilder.setLenient();
					final Gson gson = gsonBuilder.create();

					JsonReader jsonReader = gson.newJsonReader(bufferedReader);
					Opponent opponent = gson.fromJson(jsonReader, Opponent.class);
					opponents.add(opponent);
					is.close();
				}
			}
			return opponents;
		});
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public Completable saveOpponent(Opponent opponent, String sourceImageFilePath) {
		return Completable.fromCallable(() -> {
			File dir = Constants.getOpponentsOutputDir(fatLipApp);
			String jsonFileName = opponent.getImageFileName().replace("png", "json");
			File jsonFile = new File(dir, jsonFileName);
			if(!jsonFile.exists()) {
				jsonFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
			final GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Opponent.class, opponentSerializer);
			gsonBuilder.setLenient();
			final Gson gson = gsonBuilder.create();
			JsonWriter jsonWriter = gson.newJsonWriter(writer);
			jsonWriter.jsonValue(gson.toJson(opponent));
			jsonWriter.flush();
			jsonWriter.close();

			File sourceFile = new File(sourceImageFilePath);
			File destinationFile = new File(dir, opponent.getImageFileName());
			if(!destinationFile.exists()) {
				destinationFile.createNewFile();
			}
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				Files.copy(sourceFile.toPath(), destinationFile.toPath());
			}
			else {
				FileChannel inputChannel = null;
				FileChannel outputChannel = null;
				try {
					inputChannel = new FileInputStream(sourceFile).getChannel();
					outputChannel = new FileOutputStream(destinationFile).getChannel();
					outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
				}
				finally {
					if(inputChannel != null) {
						inputChannel.close();
					}
					if(outputChannel != null) {
						outputChannel.close();
					}
				}
			}
			return true;
		});
	}

	/**
	 * Creates a Single<Bitmap> reactive observable to read an opponent bitmap from disk.
	 *
	 * @param opponent the Opponent whose Bitmap is to be loaded
	 * @return a Single<Bitmap> reactive observable
	 */
	public Single<Bitmap> readOpponentBitmap(Opponent opponent) {
		return Single.fromCallable(new Callable<Bitmap>() {
			@Override
			public Bitmap call() throws Exception {
				InputStream stream = null;
				try {
					if (opponent.isCustom()) {
						try {
							stream = new FileInputStream(new File(Constants.getSceneryOutputDir(fatLipApp),
																		 opponent.getImageFileName()));
						}
						catch (FileNotFoundException e) {
							Log.e(TAG, "Exception caught opening opponent bitmap file", e);
							throw new RuntimeException(e);
						}
					}
					else {
						try {
							stream = assetManager.open("opponents/" + opponent.getImageFileName());
						}
						catch (IOException e) {
							Log.e(TAG, "Exception caught opening opponent bitmap file", e);
							throw new RuntimeException(e);
						}
					}
					return BitmapFactory.decodeStream(stream);
				}
				finally {
					if(stream != null) {
						stream.close();
					}
				}
			}
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}
}
