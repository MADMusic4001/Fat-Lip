package com.madinnovations.fatlip.view.framework;

import com.madinnovations.fatlip.model.Opponent;
import com.madinnovations.fatlip.view.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static com.madinnovations.fatlip.Constants.BYTES_PER_FLOAT;

/**
 * Class to draw an Opponent with OpenGL
 */
public class GLOpponent {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT
			+ TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private final Opponent opponent;
	private final VertexArray vertexArray;

	/**
	 * Creates a new GLOpponent instance.
	 */
	public GLOpponent(Opponent opponent, float[] vertexData) {
		vertexArray = new VertexArray(vertexData);
		this.opponent = opponent;
	}

	/**
	 * Binds the vertex and texture coordinate data to the shader program attributes;
	 *
	 * @param textureShaderProgram  the OpenGL texture shader program
	 */
	public void bindData(TextureShaderProgram textureShaderProgram) {
		vertexArray.setVertexAttribPointer(0, textureShaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT,
				STRIDE);

		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, textureShaderProgram.getTextureCoordinateAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
	}

	/**
	 * Renders the Splash image
	 */
	public void draw() {
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}

	public void unbindData(TextureShaderProgram textureShaderProgram) {
		glDisableVertexAttribArray(textureShaderProgram.getPositionAttributeLocation());
		glDisableVertexAttribArray(textureShaderProgram.getTextureCoordinateAttributeLocation());
	}
}
