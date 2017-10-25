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

import com.madinnovations.fatlip.view.framework.VertexArray;
import com.madinnovations.fatlip.view.programs.SplashShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static com.madinnovations.fatlip.Constants.BYTES_PER_FLOAT;

/**
 * Holds data for rendering text with OpenGL
 */
public class TextObject {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT
			+ TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private String      text;
	private float       x;
	private float       y;
	private float[]     color;
	private float[]     vertexData;
	private VertexArray vertexArray;

	/**
	 * Creates a new TextObject instance
	 */
	public TextObject() {
		text = "default";
		x = 0f;
		y = 0f;
		color = new float[] {1f, 1f, 1f, 1f};
	}

	/**
	 * Creates a new TextObject instance with the given text to be drawn at the given location.
	 *
	 * @param text  the text to render
	 * @param x  the x value of the position to render the text
	 * @param y  the y value of the position to render the text
	 * @param centered  true if the text should be centered at the given coordinates
	 */
	public TextObject(String text, float x, float y, boolean centered) {
		this.text = text;
		this.x = x;
		this.y = y;
		color = new float[] {1f, 1f, 1f, 1f};
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
