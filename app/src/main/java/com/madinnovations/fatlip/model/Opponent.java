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

import android.graphics.Rect;

/**
 * Representation of an opponent that the player attempts to hit
 */
public class Opponent {
	private String name;
	private Rect   leftEye;
	private Rect   rightEye;
	private Rect   nose;
	private Rect   mouth;
	private String imageFileName;
	private boolean custom = false;

	// Getters and setters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Rect getLeftEye() {
		return leftEye;
	}
	public void setLeftEye(Rect leftEye) {
		this.leftEye = leftEye;
	}
	public Rect getRightEye() {
		return rightEye;
	}
	public void setRightEye(Rect rightEye) {
		this.rightEye = rightEye;
	}
	public Rect getNose() {
		return nose;
	}
	public void setNose(Rect nose) {
		this.nose = nose;
	}
	public Rect getMouth() {
		return mouth;
	}
	public void setMouth(Rect mouth) {
		this.mouth = mouth;
	}
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	public boolean isCustom() {
		return custom;
	}
	public void setCustom(boolean custom) {
		this.custom = custom;
	}
}
