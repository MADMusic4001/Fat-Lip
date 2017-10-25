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

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Sprite image class
 */
public class Sprite {
	private float  angle;
	private float  scale;
	private RectF  base;
	private PointF translation;

	public Sprite() {
		// Initialise our initial size around the 0,0 point
		base = new RectF(-50f,50f, 50f, -50f);

		// Initial translation
		translation = new PointF(50f,50f);

		// We start with our initial size
		scale = 1f;

		// We start in our inital angle
		angle = 0f;
	}

	public void translate(float deltaX, float deltaY) {
		// Update our location.
		translation.x += deltaX;
		translation.y += deltaY;
	}

	public void scale(float deltaS) {
		scale += deltaS;
	}

	public void rotate(float deltaA) {
		angle += deltaA;
	}

	public float[] getTransformedVertices() {
		// Start with scaling
		float x1 = base.left * scale;
		float x2 = base.right * scale;
		float y1 = base.bottom * scale;
		float y2 = base.top * scale;

		// We now detach from our Rect because when rotating,
		// we need the separate points, so we do so in openGL order
		PointF one = new PointF(x1, y2);
		PointF two = new PointF(x1, y1);
		PointF three = new PointF(x2, y1);
		PointF four = new PointF(x2, y2);

		// We create the sin and cos function once,
		// so we do not have calculate them each time.
		float s = (float) Math.sin(angle);
		float c = (float) Math.cos(angle);

		// Then we rotate each point
		one.x = x1 * c - y2 * s;
		one.y = x1 * s + y2 * c;
		two.x = x1 * c - y1 * s;
		two.y = x1 * s + y1 * c;
		three.x = x2 * c - y1 * s;
		three.y = x2 * s + y1 * c;
		four.x = x2 * c - y2 * s;
		four.y = x2 * s + y2 * c;

		// Finally we translate the sprite to its correct position.
		one.x += translation.x;
		one.y += translation.y;
		two.x += translation.x;
		two.y += translation.y;
		three.x += translation.x;
		three.y += translation.y;
		four.x += translation.x;
		four.y += translation.y;

		// We now return our float array of vertices.
		return new float[]
				{
						one.x, one.y, 0.0f,
						two.x, two.y, 0.0f,
						three.x, three.y, 0.0f,
						four.x, four.y, 0.0f,
				};
	}

	// Getters and setters
	public float getAngle() {
		return angle;
	}
	public void setAngle(float angle) {
		this.angle = angle;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public RectF getBase() {
		return base;
	}
	public void setBase(RectF base) {
		this.base = base;
	}
	public PointF getTranslation() {
		return translation;
	}
	public void setTranslation(PointF translation) {
		this.translation = translation;
	}
}
