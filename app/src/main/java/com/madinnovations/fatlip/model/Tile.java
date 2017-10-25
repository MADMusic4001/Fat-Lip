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

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.madinnovations.fatlip.view.framework.VertexArray;
import com.madinnovations.fatlip.view.programs.TileShaderProgram;
import com.madinnovations.fatlip.view.utils.TextureHelper;

import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glUniform1i;
import static com.madinnovations.fatlip.Constants.BYTES_PER_FLOAT;

/**
 * A square object that displays an image/texture that the player can select when setting up a new game.
 */
@SuppressWarnings("unused")
public class Tile {
	private static final String TAG = "Tile";
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT
			+ TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private static final float[] VERTEX_DATA = {
			// Order of coordinates: X, Y, S, T

			// Triangles
			39.0f, -24.0f, 0.0f, 1.0f, // bottom right
			73.0f,  24.0f, 1.0f, 0.0f, // top left
			39.0f,  24.0f, 0.0f, 0.0f, // top right

			39.0f, -24.0f, 0.0f, 1.0f, // bottom right
			73.0f, -24.0f, 1.0f, 1.0f, // bottom left
			73.0f,  24.0f, 1.0f, 0.0f, // top left

			39.0f,  24.0f, 0.0f, 0.0f, // coordinates for selection box lines
			73.0f,  24.0f, 0.0f, 0.0f,
			73.0f, -24.0f, 0.0f, 0.0f,
			39.0f, -24.0f, 0.0f, 0.0f};

	private final VertexArray vertexArray;
	private boolean selected = false;
	private int textureId;

	/**
	 * Creates a new Tile instance
	 */
	public Tile(Context context, @DrawableRes int drawableId) {
		vertexArray = new VertexArray(VERTEX_DATA);
		textureId = TextureHelper.loadTexture(context, drawableId);
	}

	/**
	 * Binds the vertex and texture coordinate data to the shader program attributes;
	 *
	 * @param tileProgram  the OpenGL texture shader program
	 */
	public void bindData(TileShaderProgram tileProgram) {
		vertexArray.setVertexAttribPointer(0, tileProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT,
										   STRIDE);

		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, tileProgram.getTextureCoordinateAttributeLocation(),
										   TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
	}

	/**
	 * Renders the Splash image
	 */
	public void draw(TileShaderProgram program) {
		glDrawArrays(GL_TRIANGLES, 0, 6);
		int errorCode = glGetError();
		if(selected) {
			glUniform1i(program.getDrawingSelectedLocation(), 1);
			glDrawArrays(GL_LINE_LOOP, 0, 4);
			glUniform1i(program.getDrawingSelectedLocation(), 0);
		}
	}

	/**
	 * Unbinds the vertex data from OpenGL
	 *
	 * @param tileProgram  the shader program
	 */
	public void unbindData(TileShaderProgram tileProgram) {
		glDisableVertexAttribArray(tileProgram.getPositionAttributeLocation());
		glDisableVertexAttribArray(tileProgram.getTextureCoordinateAttributeLocation());
	}

	// Getters and setters
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public int getTextureId() {
		return textureId;
	}
}
