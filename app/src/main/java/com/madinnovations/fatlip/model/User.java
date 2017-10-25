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

package com.madinnovations.fatlip.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User data
 */
public class User {
	private int[] highScores = new int[5];
	private List<String> unlockedOpponents = new ArrayList<>();
	private List<String> unlockedWeapons = new ArrayList<>();
	private List<String> unlockedScenery = new ArrayList<>();

	public User() {
		highScores[0] = 10000;
		highScores[1] = 8000;
		highScores[2] = 6000;
		highScores[3] = 5000;
		highScores[4] = 2500;

		unlockedOpponents.add("Clinton");
		unlockedOpponents.add("Butters");
		unlockedWeapons.add("Rock");
		unlockedWeapons.add("Tomato");
		unlockedScenery.add("Desert");
	}

	// Getters
	public int[] getHighScores() {
		return highScores;
	}
	public List<String> getUnlockedOpponents() {
		return unlockedOpponents;
	}
	public List<String> getUnlockedWeapons() {
		return unlockedWeapons;
	}
	public List<String> getUnlockedScenery() {
		return unlockedScenery;
	}
}
