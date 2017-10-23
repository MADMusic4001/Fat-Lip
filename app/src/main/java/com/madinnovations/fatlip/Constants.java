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
package com.madinnovations.fatlip;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

@SuppressWarnings("unused")
public class Constants {
	private static final String TAG = "Constants";
	public static final int BYTES_PER_SHORT = 2;
    public static final int BYTES_PER_FLOAT = 4;
    public static final String HIGH_SCORES_PREFS_NAME = "High_Scores";
    public static final String HIGH_SCORE_1 = "highScore1";
    public static final String HIGH_SCORE_2 = "highScore2";
    public static final String HIGH_SCORE_3 = "highScore3";
    public static final String HIGH_SCORE_4 = "highScore4";
    public static final String HIGH_SCORE_5 = "highScore5";

	/**
	 * Gets the starting import directory to use. ExternalFiles/Documents.
	 *
	 * @return the File representing the directory or n
	 */
	public static File getImportDir(Context context) {
		File dir;
		dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
		if(dir == null || !dir.exists()) {
			Log.e("RMU", "Directory not created");
			throw new RuntimeException("Error creating output directory");
		}
		return dir;
	}

	/**
	 * Gets the opponents output directory to use. {InternalFiles}/opponents.
	 *
	 * @return the File representing the directory or n
	 */
	public static File getOpponentsOutputDir(Context context) {
		File dir;
		dir = new File(context.getFilesDir(), "opponents");
		if(!dir.exists()) {
			if(!dir.mkdir()) {
				Log.e(TAG, "Opponents directory not created");
				throw new RuntimeException("Error creating output directory");
			}
		}
		return dir;
	}

	/**
	 * Gets the output directory to use for custom scenery. {InternalFiles}/scenery.
	 *
	 * @return the File representing the directory or n
	 */
	public static File getSceneryOutputDir(Context context) {
		File dir;
		dir = new File(context.getFilesDir(), "scenery");
		if(!dir.exists()) {
			if(!dir.mkdir()) {
				Log.e(TAG, "Scenery directory not created");
				throw new RuntimeException("Error creating scenery output directory");
			}
		}
		return dir;
	}
}
