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

package com.madinnovations.fatlip.model.framework;

/**
 * Holds data for rendering text with OpenGL
 */
public class TextObject {
	private String text;
	private float x;
	private float y;
	private float[] color;

	public TextObject() {
		text = "default";
		x = 0f;
		y=0f;
		color = new float[] {1f, 1f, 1f, 1f};
	}

	public TextObject(String text, float x, float y) {
		this.text = text;
		this.x = x;
		this.y = y;
		color = new float[] {1f, 1f, 1f, 1f};
	}

	// Getters and setters
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float[] getColor() {
		return color;
	}
	public void setColor(float[] color) {
		this.color = color;
	}
}
