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
import android.graphics.Color;
import android.util.Log;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.model.Tile;

import static android.opengl.GLES20.GL_NO_ERROR;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * OpenGl shader program for drawing a {@link Tile} object
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public class TileShaderProgram extends ShaderProgram {
	private static final String TAG = "TileShaderProgram";
	private static final String U_MVP_MATRIX = "u_MVPMatrix";
	private static final String U_DRAWING_SELECTED = "u_DrawingSelected";
	private static final String U_LINE_COLOR = "u_LineColor";
	private int uMVPMatrixLocation;
	private int uTextureUnitLocation;
	private int uDrawingSelectedLocation;
	private int uLineColorLocation;
	private int aPositionLocation;
	private int aTextureCoordinateLocation;

	/**
	 * Creates a new TileShaderProgram instance.
	 *
	 * @param context  an android context to use to access resources.
	 */
	public TileShaderProgram(Context context) {
		super(context, R.raw.tile_vertex_shader, R.raw.tile_fragment_shader);

		uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
		uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
		uDrawingSelectedLocation = glGetUniformLocation(program, U_DRAWING_SELECTED);
		uLineColorLocation = glGetUniformLocation(program, U_LINE_COLOR);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aTextureCoordinateLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
	}

	/**
	 * Sets the shader uniform values
	 *
	 * @param textureId  the id of the texture to use when rendering
	 */
	public void setUniforms(float[] mvpMatrix, int textureId, int lineColor) {
		glUniformMatrix4fv(uMVPMatrixLocation, 0, false, mvpMatrix, 0);
		int errorCode = glGetError();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureId);
		glUniform1i(uTextureUnitLocation, 0);
		glUniform1i(uDrawingSelectedLocation, 0);
		glUniform4f(uLineColorLocation, Color.red(lineColor), Color.green(lineColor), Color.blue(lineColor), Color.alpha(lineColor));
	}

	// Getters
	public int getDrawingSelectedLocation() {
		return uDrawingSelectedLocation;
	}
	public int getPositionAttributeLocation() {
		return aPositionLocation;
	}
	public int getTextureCoordinateAttributeLocation() {
		return aTextureCoordinateLocation;
	}
}
