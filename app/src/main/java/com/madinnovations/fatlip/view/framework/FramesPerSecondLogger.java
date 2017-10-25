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

package com.madinnovations.fatlip.view.framework;

import android.util.Log;

/**
 * Utility class to track frames per second
 */
public class FramesPerSecondLogger {
	private static final String TAG = "FramesPerSecondLogger";
	private static final int DEFAULT_LIST_SIZE = 10;
	private static final int DEFAULT_FREQUENCY = 10;
	private static final String lineSeparator = System.getProperty("line.separator");
	private long startTime = System.nanoTime();
	private int frames = 0;
	private double framesPerSecond = 0.0;
	private int listSize = DEFAULT_LIST_SIZE;
	private int tailIndex = 0;
	private double[] framesPerSecondList = new double[10];
	private int logFrequency = DEFAULT_FREQUENCY;
	private int frequencyCounter = 0;

	 /**
	 * Increments the frame counter and if 1 second or more has passed since the last FPS calculation then it calculates the
	 * current FPS.
	 *
	 * @return the current FPS
	 */
	public double logFrame() {
		frames++;
		long delta = System.nanoTime() - startTime;
		if(delta > 1000000000) {
			framesPerSecond = (frames/(delta/1000000000));
			framesPerSecondList[tailIndex++] = framesPerSecond;
			tailIndex = tailIndex % listSize;
			frequencyCounter++;
			if(frequencyCounter == logFrequency) {
				Log.d(TAG, "logFrame: new fps = " + framesPerSecond);
				frequencyCounter = 0;
			}
			frames = 0;
			startTime = System.nanoTime();
		}

		return framesPerSecond;
	}

	public String printFPSList() {
		StringBuilder builder = new StringBuilder();
		for (int i = tailIndex; i < listSize; i++) {
			appendFPS(builder, framesPerSecondList[i]);
		}
		for (int i = 0; i < tailIndex; i++) {
			appendFPS(builder, framesPerSecondList[i]);
		}
		return builder.toString();
	}

	private void appendFPS(StringBuilder builder, double fps) {
		builder.append("\t")
				.append(fps)
				.append(lineSeparator);
	}

	// Getters and setters
	public double getFramesPerSecond() {
		return framesPerSecond;
	}
	public int getListSize() {
		return listSize;
	}
	public void setListSize(int listSize) {
		if(listSize > this.listSize) {
			double[] newList = new double[listSize];
			System.arraycopy(framesPerSecondList, tailIndex, newList, 0, this.listSize - tailIndex);
			if(tailIndex > 0) {
				System.arraycopy(framesPerSecondList, 0, newList, this.listSize - tailIndex, tailIndex);
			}
			tailIndex = this.listSize;
			this.listSize = listSize;
		}
	}
	public double[] getFramesPerSecondList() {
		return framesPerSecondList;
	}
	public int getLogFrequency() {
		return logFrequency;
	}
	public void setLogFrequency(int logFrequency) {
		this.logFrequency = logFrequency;
	}
}
