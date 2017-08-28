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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.madinnovations.fatlip.controller.framework.FileIO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to load and manage a Texture
 */
public class Texture {
	private FileIO fileIO;
	private String fileName;
	private int textureId;
	private int minFilter;
	private int magFilter;
	private int width;
	private int height;

	public Texture(String fileName) {
		this.fileName = fileName;
	}

	private void load() {
		int[] textureIds = new int[1];
		GLES20.glGenTextures(1, textureIds, 0);
		textureId = textureIds[0];

		InputStream in = null;
		try {
			in = fileIO.readAsset(fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			width = bitmap.getWidth();
			height = bitmap.getHeight();
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			setFilters(GLES20.GL_NEAREST, GLES20.GL_NEAREST);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		}
		catch (IOException e) {
			throw new RuntimeException("Couldn't load texture '" + fileName + "'", e);
		}
		finally {
			if(in != null) {
				try {in.close();}catch (IOException ignored) {}
			}
		}
	}

	public void reload() {
		load();
		bind();
		setFilters(minFilter, magFilter);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}

	private void setFilters(int minFilter, int magFilter) {
		this.minFilter = minFilter;
		this.magFilter = magFilter;
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, magFilter);
	}

	private void bind() {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
	}

	public void dispose() {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		int[] textureIds = {textureId};
		GLES20.glDeleteTextures(1, textureIds, 0);
	}
}
