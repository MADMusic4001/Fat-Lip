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

package com.madinnovations.fatlip.model.framework.impl;

import android.graphics.Point;

import com.madinnovations.fatlip.model.framework.Rect;
import com.madinnovations.fatlip.model.framework.Size;

/**
 * TextureAtlas Frame object
 */
public class Frame {
	private String  filename;
	private Rect    frame;
	private boolean rotated;
	private boolean trimmed;
	private Rect    spriteSourceSize;
	private Size    sourceSize;
	private Point   pivot;

	// Getters and setters
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Rect getFrame() {
		return frame;
	}
	public void setFrame(Rect frame) {
		this.frame = frame;
	}
	public boolean isRotated() {
		return rotated;
	}
	public void setRotated(boolean rotated) {
		this.rotated = rotated;
	}
	public boolean isTrimmed() {
		return trimmed;
	}
	public void setTrimmed(boolean trimmed) {
		this.trimmed = trimmed;
	}
	public Rect getSpriteSourceSize() {
		return spriteSourceSize;
	}
	public void setSpriteSourceSize(Rect spriteSourceSize) {
		this.spriteSourceSize = spriteSourceSize;
	}
	public Size getSourceSize() {
		return sourceSize;
	}
	public void setSourceSize(Size sourceSize) {
		this.sourceSize = sourceSize;
	}
	public Point getPivot() {
		return pivot;
	}
	public void setPivot(Point pivot) {
		this.pivot = pivot;
	}
}
