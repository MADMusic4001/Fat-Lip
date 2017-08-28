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

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Class to manage 2D or 3D Vertices
 */
public class Vertices {
	private boolean     hasColor;
	private boolean     hasTexCoords;
	private boolean     is3D;
	private int         vertexSize;
	private FloatBuffer vertices;
	private int         indicesLength;
	private ShortBuffer indices;

	public Vertices(int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords, boolean is3D) {
		this.hasColor = hasColor;
		this.hasTexCoords = hasTexCoords;
		this.is3D = is3D;

		this.vertexSize = (((is3D?3:2) + (hasColor?4:0) + (hasTexCoords?2:0)) * 4);
		ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
		buffer.order(ByteOrder.nativeOrder());
		vertices = buffer.asFloatBuffer();

		if(maxIndices > 0) {
			buffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);
			buffer.order(ByteOrder.nativeOrder());
			indices = buffer.asShortBuffer();
		}
		else {
			indices = null;
		}
	}

	public void setVertices(float[] vertices, int offset, int length) {
		this.vertices.clear();
		this.vertices.put(vertices, offset, length);
		this.vertices.flip();
	}

	public void setIndices(short[] indices, int offset, int length) {
		this.indices.clear();
		this.indices.put(indices, offset, length);
		this.indices.flip();
		this.indicesLength = length;
	}

	public void draw(float[] mvpMatrix, int primitiveType, int offset, int numVertices) {
		int stride = 4;
		int texCoordsIndex = 2;
		if(is3D) {
			texCoordsIndex += 1;
			stride += 1;
		}
		if(hasColor) {
			texCoordsIndex += 4;
			stride += 4;
		}
		int positionHandle = GLES20.glGetAttribLocation(GLGraphicTools.sp_Image, "vPosition");
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, vertexSize, GLES20.GL_FLOAT, false, stride, vertices);
		int colorHandle = -1;
		if(hasColor) {
			colorHandle = GLES20.glGetAttribLocation(GLGraphicTools.sp_SolidColor, "vPosition");
			GLES20.glEnableVertexAttribArray(colorHandle);
			GLES20.glVertexAttribPointer(colorHandle, vertexSize, GLES20.GL_FLOAT, false, stride, vertices);
		}
		int texCoordLoc = -1;
		if(hasTexCoords) {
			texCoordLoc = GLES20.glGetAttribLocation(GLGraphicTools.sp_Image, "a_texCoord");
			GLES20.glEnableVertexAttribArray(texCoordLoc);
			GLES20.glVertexAttribPointer(texCoordLoc, vertexSize, GLES20.GL_FLOAT, false, stride, texCoordsIndex);
			int matrixHandle = GLES20.glGetUniformLocation(GLGraphicTools.sp_Image, "uMVPMatrix");
			GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);
			int samplerLoc = GLES20.glGetUniformLocation(GLGraphicTools.sp_Image, "s_texture");
			GLES20.glUniform1i(samplerLoc, 0);
		}

		GLES20.glDrawElements(primitiveType, indicesLength, GLES20.GL_UNSIGNED_SHORT, indices);

		GLES20.glDisableVertexAttribArray(positionHandle);
		if(colorHandle >= 0) {
			GLES20.glDisableVertexAttribArray(colorHandle);
		}
		if(texCoordLoc >= 0) {
			GLES20.glDisableVertexAttribArray(texCoordLoc);
		}
	}
}
