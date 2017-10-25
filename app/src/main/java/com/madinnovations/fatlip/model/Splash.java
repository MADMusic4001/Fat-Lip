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

import com.madinnovations.fatlip.view.framework.VertexArray;
import com.madinnovations.fatlip.view.programs.SplashShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static com.madinnovations.fatlip.Constants.BYTES_PER_FLOAT;

/**
 * A background image that covers the entire screen.
 */
public class Splash {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT
			+ TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

	private static final float[] VERTEX_DATA = {
			// Order of coordinates: X, Y, S, T

			// Triangles
			 -1.0f, -1.0f,  0.0f, 1.0f, // bottom right
			  1.0f,  1.0f,  1.0f, 0.0f, // top left
			 -1.0f,  1.0f,  0.0f, 0.0f,  // top right

			 -1.0f, -1.0f,  0.0f, 1.0f, // bottom right
		      1.0f, -1.0f,  1.0f, 1.0f, // bottom left
			  1.0f,  1.0f,  1.0f, 0.0f }; // top left

	private final VertexArray vertexArray;

	/**
	 * Creates a new Splash instance.
	 */
	public Splash() {
		vertexArray = new VertexArray(VERTEX_DATA);
	}

	/**
	 * Binds the vertex and texture coordinate data to the shader program attributes;
	 *
	 * @param splashProgram  the OpenGL texture shader program
	 */
	public void bindData(SplashShaderProgram splashProgram) {
		vertexArray.setVertexAttribPointer(0, splashProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT,
										   STRIDE);

		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, splashProgram.getTextureCoordinateAttributeLocation(),
										   TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
	}

	/**
	 * Renders the Splash image
	 */
	public void draw() {
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}

	public void unbindData(SplashShaderProgram splashProgram) {
		glDisableVertexAttribArray(splashProgram.getPositionAttributeLocation());
		glDisableVertexAttribArray(splashProgram.getTextureCoordinateAttributeLocation());
	}
}
