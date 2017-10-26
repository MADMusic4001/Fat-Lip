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
import com.madinnovations.fatlip.model.Weapon;
import com.madinnovations.fatlip.model.serializers.WeaponSerializer;
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
 * ReactiveX handler that reads and writes weapon.
 */
@SuppressWarnings({"unused", "WeakerAccess", "ResultOfMethodCallIgnored"})
public class WeaponRxHandler {
	private static final String TAG = "WeaponRxHandler";
	private FatLipApp        fatLipApp;
	private AssetManager     assetManager;
	private WeaponSerializer weaponSerializer;

	/**
	 * Creates a new WeaponRxHandler
	 *
	 * @param assetManager the {@link AssetManager} instance
	 */
	@Inject
	public WeaponRxHandler(FatLipApp fatLipApp, AssetManager assetManager, WeaponSerializer weaponSerializer) {
		this.fatLipApp = fatLipApp;
		this.assetManager = assetManager;
		this.weaponSerializer = weaponSerializer;
	}

	public Flowable<List<Weapon>> loadWeapons() {
		return Flowable.fromCallable(() -> {
			String[] weaponFileNames = assetManager.list("weapons");
			List<Weapon> weaponList = new ArrayList<>();
			for(String weaponFileName : weaponFileNames) {
				if(weaponFileName.endsWith("json")) {
					InputStream is = assetManager.open("weapons/" + weaponFileName);
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(Weapon.class, weaponSerializer);
					gsonBuilder.setLenient();
					final Gson gson = gsonBuilder.create();

					JsonReader jsonReader = gson.newJsonReader(bufferedReader);
					Weapon weapon = gson.fromJson(jsonReader, Weapon.class);
					if(fatLipApp.getUser().getUnlockedWeapons().contains(weapon.getName())) {
						weaponList.add(weapon);
					}
					is.close();
				}
			}
			return weaponList;
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Completable saveWeapon(Weapon weapon, String sourceImageFilePath) {
		return Completable.fromCallable(() -> {
			File dir = Constants.getWeaponsOutputDir(fatLipApp);
			String jsonFileName = weapon.getImageFileName().replace("png", "json");
			File jsonFile = new File(dir, jsonFileName);
			if(!jsonFile.exists()) {
				jsonFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
			final GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Weapon.class, weaponSerializer);
			gsonBuilder.setLenient();
			final Gson gson = gsonBuilder.create();
			JsonWriter jsonWriter = gson.newJsonWriter(writer);
			jsonWriter.jsonValue(gson.toJson(weapon));
			jsonWriter.flush();
			jsonWriter.close();

			File sourceFile = new File(sourceImageFilePath);
			File destinationFile = new File(dir, weapon.getImageFileName());
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
	 * Creates a Single<Bitmap> reactive observable to read a weapon bitmap from disk.
	 *
	 * @param weapon the Weapon whose Bitmap is to be loaded
	 * @return a Single<Bitmap> reactive observable
	 */
	public Single<Bitmap> readWeaponBitmap(Weapon weapon) {
		return Single.fromCallable(new Callable<Bitmap>() {
			@Override
			public Bitmap call() throws Exception {
				InputStream stream = null;
				try {
					try {
						stream = assetManager.open("weapons/" + weapon.getImageFileName());
					}
					catch (IOException e) {
						Log.e(TAG, "Exception caught opening weapon bitmap file", e);
						throw new RuntimeException(e);
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
