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

package com.madinnovations.fatlip.view.programs;

import android.content.Context;
import android.util.Log;

import com.madinnovations.fatlip.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;

/**
 * Shader program for rendering a splash screen
 */
@SuppressWarnings("unused")
public class SplashShaderProgram extends ShaderProgram {
	private static final String TAG = "SplashShaderProgram";
	private int uTextureUnitLocation;
	private int aPositionLocation;
	private int aTextureCoordinateLocation;

	public SplashShaderProgram(Context context) {
		super(context, R.raw.splash_vertex_shader, R.raw.splash_fragment_shader);

		uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aTextureCoordinateLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
	}

	/**
	 * Sets the shader uniform values
	 *
	 * @param textureId  the id of the texture to use when rendering
	 */
	public void setUniforms(int textureId) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureId);
		glUniform1i(uTextureUnitLocation, 0);
	}

	// Getters
	public int getPositionAttributeLocation() {
		return aPositionLocation;
	}
	public int getTextureCoordinateAttributeLocation() {
		return aTextureCoordinateLocation;
	}
}
