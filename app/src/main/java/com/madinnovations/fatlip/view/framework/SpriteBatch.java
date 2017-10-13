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

import android.opengl.Matrix;
import android.util.Log;

import com.madinnovations.fatlip.model.GLText;
import com.madinnovations.fatlip.view.programs.ShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

@SuppressWarnings("unused")
public class SpriteBatch {
	private static final String TAG = "SpriteBatch";
	private final static int VERTEX_SIZE = 5;
	private final static int VERTICES_PER_SPRITE = 4;
	private final static int INDICES_PER_SPRITE = 6;

	private Vertices vertices;
	private float[]  vertexBuffer;
	private int      bufferIndex;
	private int      maxSprites;
	private int      numSprites;
	private float[]  vpMatrix;
	private float[]  mvpMatrices = new float[GLText.CHAR_BATCH_SIZE*16];
	private int      mvpMatricesHandle;
	private float[]  mvpMatrix = new float[16];

	/**
	 * Creates a new SpriteBatch instance
	 *
	 * @param maxSprites  the maximum allowed sprites per batch
	 * @param program  the shader program to use when drawing
	 */
	@SuppressWarnings("PointlessArithmeticExpression")
	public SpriteBatch(int maxSprites, ShaderProgram program)  {
		this.vertexBuffer = new float[maxSprites * VERTICES_PER_SPRITE * VERTEX_SIZE];
		this.vertices = new Vertices(program, maxSprites * VERTICES_PER_SPRITE,
									 maxSprites * INDICES_PER_SPRITE);
		this.bufferIndex = 0;
		this.maxSprites = maxSprites;
		this.numSprites = 0;

		short[] indices = new short[maxSprites * INDICES_PER_SPRITE];
		int len = indices.length;
		short j = 0;
		for ( int i = 0; i < len; i+= INDICES_PER_SPRITE, j += VERTICES_PER_SPRITE )  {
			indices[i + 0] = (short)( j + 0 );
			indices[i + 1] = (short)( j + 1 );
			indices[i + 2] = (short)( j + 2 );
			indices[i + 3] = (short)( j + 2 );
			indices[i + 4] = (short)( j + 3 );
			indices[i + 5] = (short)( j + 0 );
		}
		vertices.setIndices( indices, 0, len );
        mvpMatricesHandle = glGetUniformLocation(program.getProgram(), "u_MVPMatrix");
	}

	public void beginBatch(float[] vpMatrix)  {
		numSprites = 0;
		bufferIndex = 0;
		this.vpMatrix = vpMatrix;
	}

	/**
	 * signal the end of a batch. render the batched sprites
	 */
	public void endBatch()  {
		if ( numSprites > 0 )  {
			glUniformMatrix4fv(mvpMatricesHandle, numSprites, false, mvpMatrices, 0);
			glEnableVertexAttribArray(mvpMatricesHandle);

			vertices.setVertices( vertexBuffer, 0, bufferIndex);
			vertices.bind();
			vertices.draw(GL_TRIANGLES, 0, numSprites * INDICES_PER_SPRITE );
			vertices.unbind();
		}
	}

	/**
	 * Batch specified sprite to batch. adds vertices for sprite to vertex buffer
	 * 		NOTE: MUST be called after beginBatch(), and before endBatch()!
	 * 		NOTE: if the batch overflows, this will render the current batch, restart it, and then batch this sprite.
	 * @param x  the x position of the sprite (center)
	 * @param y  the y position of the sprite (center)
	 * @param width  the width of the sprite
	 * @param height  the height of the sprite
	 * @param region  the texture region to use for sprite
	 * @param modelMatrix  the model matrix to assign to the sprite
	 */
	public void drawSprite(float x, float y, float width, float height, TextureRegion region, float[] modelMatrix)  {
		if ( numSprites == maxSprites )  {
			endBatch();
			numSprites = 0;
			bufferIndex = 0;
		}

		float halfWidth = width / 2.0f;
		float halfHeight = height / 2.0f;
		float x1 = x - halfWidth;
		float y1 = y - halfHeight;
		float x2 = x + halfWidth;
		float y2 = y + halfHeight;

		vertexBuffer[bufferIndex++] = x1;
		vertexBuffer[bufferIndex++] = y1;
		vertexBuffer[bufferIndex++] = region.u1;
		vertexBuffer[bufferIndex++] = region.v2;
		vertexBuffer[bufferIndex++] = numSprites;

		vertexBuffer[bufferIndex++] = x2;
		vertexBuffer[bufferIndex++] = y1;
		vertexBuffer[bufferIndex++] = region.u2;
		vertexBuffer[bufferIndex++] = region.v2;
		vertexBuffer[bufferIndex++] = numSprites;

		vertexBuffer[bufferIndex++] = x2;
		vertexBuffer[bufferIndex++] = y2;
		vertexBuffer[bufferIndex++] = region.u2;
		vertexBuffer[bufferIndex++] = region.v1;
		vertexBuffer[bufferIndex++] = numSprites;

		vertexBuffer[bufferIndex++] = x1;
		vertexBuffer[bufferIndex++] = y2;
		vertexBuffer[bufferIndex++] = region.u1;
		vertexBuffer[bufferIndex++] = region.v1;
		vertexBuffer[bufferIndex++] = numSprites;

		Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);

		System.arraycopy(mvpMatrix, 0, mvpMatrices, numSprites * 16, 16);

		numSprites++;
	}
}
