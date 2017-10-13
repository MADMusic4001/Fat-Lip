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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;

import com.madinnovations.fatlip.view.programs.ShaderProgram;

@SuppressWarnings({"SameParameterValue", "WeakerAccess", "unused"})
public class Vertices {
	private static final String TAG = "Vertices";
	private final static int POSITION_CNT_2D = 2;
	private final static int POSITION_CNT_3D = 3;
	private final static int COLOR_CNT = 4;
	private final static int TEXCOORD_CNT = 2;
	private final static int NORMAL_CNT = 3;
	private static final int MVP_MATRIX_INDEX_CNT = 1;
	private final static int INDEX_SIZE = Short.SIZE / 8;

	private final int         positionCnt;
	private final int         vertexStride;
	private final int         vertexSize;
	private final IntBuffer   vertices;
	private final ShortBuffer indices;
	private int               numVertices;
	private int               numIndices;
	private final int[]       tmpBuffer;
	private int               textureCoordinateHandle;
	private int               positionHandle;
	private int               mvpIndexHandle;

	/**
	 * Creates a new Vertice instance
	 *
	 * @param maxVertices  maximum vertices allowed in buffer
	 * @param maxIndices  maximum indices allowed in buffer
	 */
	public Vertices(ShaderProgram program, int maxVertices, int maxIndices)  {
		this.positionCnt = POSITION_CNT_2D;
		this.vertexStride = this.positionCnt + TEXCOORD_CNT + MVP_MATRIX_INDEX_CNT;
		this.vertexSize = this.vertexStride * 4;

		ByteBuffer buffer = ByteBuffer.allocateDirect( maxVertices * vertexSize );
		buffer.order( ByteOrder.nativeOrder());
		this.vertices = buffer.asIntBuffer();

		if ( maxIndices > 0 )  {
			buffer = ByteBuffer.allocateDirect( maxIndices * INDEX_SIZE );
			buffer.order( ByteOrder.nativeOrder());
			this.indices = buffer.asShortBuffer();
		}
		else {
			indices = null;
		}

		numVertices = 0;
		numIndices = 0;

		this.tmpBuffer = new int[maxVertices * vertexSize / 4];

		textureCoordinateHandle = glGetAttribLocation(program.getProgram(), AttribVariable.A_TexCoordinate.getName());
		mvpIndexHandle = glGetAttribLocation(program.getProgram(), AttribVariable.A_MVPMatrixIndex.getName());
		positionHandle = glGetAttribLocation(program.getProgram(), AttribVariable.A_Position.getName());
	}

	/**
	 * set the specified vertices in the vertex buffer
	 * 		NOTE: optimized to use integer buffer!
	 *
	 * @param vertices  array of vertices (floats) to set
	 * @param offset  offset to first vertex in array
	 * @param length  number of floats in the vertex array (total)
	 * 					for easy setting use: vtx_cnt * (this.vertexSize / 4)
	 */
	public void setVertices(float[] vertices, int offset, int length)  {
		this.vertices.clear();
		int last = offset + length;
		for ( int i = offset, j = 0; i < last; i++, j++ ) {
			tmpBuffer[j] = Float.floatToRawIntBits(vertices[i]);
		}
		this.vertices.put( tmpBuffer, 0, length);
		this.vertices.flip();
		this.numVertices = length / this.vertexStride;
	}

	/**
	 * set the specified indices in the index buffer
	 *
	 * @param indices  array of indices (shorts) to set
	 * @param offset  offset to first index in array
	 * @param length  number of indices in array (from offset)
	 */
	public void setIndices(short[] indices, int offset, int length)  {
		this.indices.clear();
		this.indices.put( indices, offset, length);
		this.indices.flip();
		this.numIndices = length;
	}

	/**
	 * perform all required binding/state changes before rendering batches.
	 */
	public void bind()  {
		vertices.position(0);
		glVertexAttribPointer(positionHandle, positionCnt, GL_FLOAT, false, vertexSize, vertices);
		glEnableVertexAttribArray(positionHandle);

		vertices.position(positionCnt);
		glVertexAttribPointer(textureCoordinateHandle, TEXCOORD_CNT, GL_FLOAT, false, vertexSize, vertices);
		glEnableVertexAttribArray(textureCoordinateHandle);

		vertices.position(positionCnt + TEXCOORD_CNT);
		glVertexAttribPointer(mvpIndexHandle, MVP_MATRIX_INDEX_CNT, GL_FLOAT, false, vertexSize, vertices);
		glEnableVertexAttribArray(mvpIndexHandle);
	}

	/**
	 * Draw the currently bound vertices in the vertex/index buffers
	 * 		USAGE: can only be called after calling bind() for this buffer.
	 *
	 * @param primitiveType  the type of primitive to draw
	 * @param offset  the offset in the vertex/index buffer to start at
	 * @param numVertices  the number of vertices (indices) to draw
	 */
	public void draw(int primitiveType, int offset, int numVertices)  {
		if (indices != null) {
			indices.position(offset);
			glDrawElements(primitiveType, numVertices, GL_UNSIGNED_SHORT, indices);
		}
		else  {
			glDrawArrays(primitiveType, offset, numVertices);
		}
	}

	/**
	 * clear binding states when done rendering batches.
	 * USAGE: call once before calling draw() multiple times for this buffer.
	 */
	public void unbind()  {
		glDisableVertexAttribArray(textureCoordinateHandle);
	}
}
