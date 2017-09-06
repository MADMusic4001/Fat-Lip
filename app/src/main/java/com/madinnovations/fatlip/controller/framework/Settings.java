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

package com.madinnovations.fatlip.controller.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.inject.Inject;

/**
 * Maintains global settings
 */
public class Settings {
	public static boolean soundEnabled = true;
	public static int[] highScores = new int[] {100, 80, 50, 30, 10};

	/**
	 * Loads the settings file from disk. Defaults will be used if any errors occur.
	 *
	 * @param files  a {@link FileIO} instance
	 */
	public static void load(FileIO files) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(files.readFile(".fatlip")));
			soundEnabled = Boolean.parseBoolean(in.readLine());
			for(int i = 0; i < 5; i++) {
				highScores[i] = Integer.parseInt(in.readLine());
			}
		}
		catch (IOException | NumberFormatException ignored) {}
		finally {
			try {
				if(in != null) {
					in.close();
				}
			}
			catch (IOException ignored) {}
		}
	}

	/**
	 * Saves the game settings to a file.
	 *
	 * @param files  a {@link FileIO} instance.
	 */
	public static void save(FileIO files) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(files.writeFile(".fatlip")));
			out.write(Boolean.toString(soundEnabled));
			for(int score : highScores) {
				out.write(Integer.toString(score));
			}
		}
		catch (IOException ignored) {}
		finally {
			try {
				if(out != null) {
					out.close();
				}
			}
			catch (IOException ignored) {}
		}
	}

	/**
	 * Add a score to the high scores list if it is higher than one or more existing high scores.
	 *
	 * @param score  the score to potentially add to the list
	 */
	public static void addScore(int score) {
		for(int i = 0; i < 5; i++) {
			if(highScores[i] < score) {
				for(int j = 4; j > i; j--) {
					highScores[j] = highScores[j -1];
				}
				highScores[i] = score;
				break;
			}
		}
	}
}
