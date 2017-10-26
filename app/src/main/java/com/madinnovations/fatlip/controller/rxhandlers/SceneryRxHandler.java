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
import com.madinnovations.fatlip.model.Scenery;
import com.madinnovations.fatlip.model.serializers.ScenerySerializer;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.activities.GLGame;

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
 * ReactiveX handler that reads and writes scenery.
 */
@SuppressWarnings({"unused", "WeakerAccess", "ResultOfMethodCallIgnored"})
public class SceneryRxHandler {
	private static final String TAG = "SceneryRxHandler";
	private FatLipApp         fatLipApp;
	private AssetManager      assetManager;
	private ScenerySerializer scenerySerializer;

	/**
	 * Creates a new SceneryRxHandler
	 *
	 * @param assetManager the {@link AssetManager} instance
	 */
	@Inject
	public SceneryRxHandler(FatLipApp fatLipApp, AssetManager assetManager, ScenerySerializer scenerySerializer) {
		this.fatLipApp = fatLipApp;
		this.assetManager = assetManager;
		this.scenerySerializer = scenerySerializer;
	}

	public Flowable<List<Scenery>> loadScenery() {
		return Flowable.fromCallable(() -> {
			String[] sceneryFileNames = assetManager.list("scenery");
			List<Scenery> sceneryList = new ArrayList<>();
			for(String sceneryFileName : sceneryFileNames) {
				if(sceneryFileName.endsWith("json")) {
					InputStream is = assetManager.open("scenery/" + sceneryFileName);
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(Scenery.class, scenerySerializer);
					gsonBuilder.setLenient();
					final Gson gson = gsonBuilder.create();

					JsonReader jsonReader = gson.newJsonReader(bufferedReader);
					Scenery scenery = gson.fromJson(jsonReader, Scenery.class);
					if(fatLipApp.getUser().getUnlockedScenery().contains(scenery.getName())) {
						sceneryList.add(scenery);
					}
					is.close();
				}
			}

			File sceneryDir = Constants.getSceneryOutputDir(fatLipApp);
			sceneryFileNames = sceneryDir.list();
			for(String sceneryFileName : sceneryFileNames) {
				if(sceneryFileName.endsWith("json")) {
					InputStream is = new FileInputStream(new File(sceneryDir, sceneryFileName));
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(Scenery.class, scenerySerializer);
					gsonBuilder.setLenient();
					final Gson gson = gsonBuilder.create();

					JsonReader jsonReader = gson.newJsonReader(bufferedReader);
					Scenery scenery = gson.fromJson(jsonReader, Scenery.class);
					sceneryList.add(scenery);
					is.close();
				}
			}

			return sceneryList;
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Completable saveScenery(Scenery scenery, String sourceImageFilePath) {
		return Completable.fromCallable(() -> {
			File dir = Constants.getSceneryOutputDir(fatLipApp);
			String jsonFileName = scenery.getImageFileName().replace("png", "json");
			File jsonFile = new File(dir, jsonFileName);
			if(!jsonFile.exists()) {
				jsonFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
			final GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Scenery.class, scenerySerializer);
			gsonBuilder.setLenient();
			final Gson gson = gsonBuilder.create();
			JsonWriter jsonWriter = gson.newJsonWriter(writer);
			jsonWriter.jsonValue(gson.toJson(scenery));
			jsonWriter.flush();
			jsonWriter.close();

			File sourceFile = new File(sourceImageFilePath);
			File destinationFile = new File(dir, scenery.getImageFileName());
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
	 * Creates a Single<Bitmap> reactive observable to read a scenery bitmap from disk.
	 *
	 * @param scenery  the Scenery whose Bitmap is to be loaded
	 * @return a Single<Bitmap> reactive observable
	 */
	public Single<Bitmap> readSceneryBitmap(Scenery scenery) {
		return Single.fromCallable(new Callable<Bitmap>() {
			@Override
			public Bitmap call() throws Exception {
				InputStream stream = null;
				try {
					if (scenery.isCustom()) {
						try {
							stream = new FileInputStream(new File(Constants.getSceneryOutputDir(fatLipApp),
																  scenery.getImageFileName()));
						}
						catch (FileNotFoundException e) {
							Log.e(TAG, "Exception caught opening scenery file", e);
							throw new RuntimeException(e);
						}
					}
					else {
						try {
							stream = assetManager.open("scenery/" + scenery.getImageFileName());
						}
						catch (IOException e) {
							Log.e(TAG, "Exception caught opening scenery file", e);
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
