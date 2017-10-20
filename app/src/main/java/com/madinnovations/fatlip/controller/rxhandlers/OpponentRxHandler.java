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
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.madinnovations.fatlip.model.Opponent;
import com.madinnovations.fatlip.model.serializers.OpponentSerializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

/**
 * ReactiveX handler that reads and writes opponents.
 */
public class OpponentRxHandler {
	private static final String TAG = "OpponentRxHandler";
	private AssetManager assetManager;
	private OpponentSerializer opponentSerializer;

	/**
	 * Creates a new OpponentRxHandler
	 *
	 * @param assetManager the {@link AssetManager} instance
	 */
	@Inject
	public OpponentRxHandler(AssetManager assetManager, OpponentSerializer opponentSerializer) {
		this.assetManager = assetManager;
		this.opponentSerializer = opponentSerializer;
	}

	public Flowable<List<Opponent>> loadOpponents() {
		return Flowable.fromCallable(() -> {
			String[] opponentFileNames = assetManager.list("opponents");
			List<Opponent> opponents = new ArrayList<>();
			for(String opponentFileName : opponentFileNames) {
				Log.d(TAG, "call: opponentFileName = " + opponentFileName);
				if(opponentFileName.endsWith("json")) {
					InputStream is = assetManager.open("opponents/" + opponentFileName);
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
}
