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

import com.madinnovations.fatlip.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * OpenGL shader program for rendering text
 */
public class TextShaderProgram extends ShaderProgram {
	private final int uMVPMatrixLocation;
	private final int uTextureUnitLocation;
	private final int aPositionLocation;
	private final int aColorLocation;
	private final int aTextureCoordinatesLocation;

	/**
	 * Creates a new TextShaderProgram instance.
	 *
	 * @param context  an Android context to use for loading shader program resources
	 */
	public TextShaderProgram(Context context) {
		super(context, R.raw.text_vertex_shader, R.raw.text_fragment_shader);

		uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
		uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aColorLocation = glGetAttribLocation(program, A_COLOR);
		aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
	}

	/**
	 * Sets the uniform data.
	 *
	 * @param mvpMatrix  the model view projection matrix
	 * @param textureUnitId  the id of the texture unit with the text font
	 */
	public void setUniforms(float[] mvpMatrix, int textureUnitId) {
		glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureUnitId);
		glUniform1i(uTextureUnitLocation, 0);
	}

	// Getters
	public int getPositionLocation() {
		return aPositionLocation;
	}
	public int getColorLocation() {
		return aColorLocation;
	}
	public int getTextureCoordinatesLocation() {
		return aTextureCoordinatesLocation;
	}
}
