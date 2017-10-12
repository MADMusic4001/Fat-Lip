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

import android.util.Log;

import com.madinnovations.fatlip.view.utils.LoggerConfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_INVALID_ENUM;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.madinnovations.fatlip.Constants.BYTES_PER_FLOAT;

public class VertexArray {
	private static final String TAG = "VertexArray";
	private final FloatBuffer floatBuffer;

	/**
	 * Creates a new VertexArray instance.
	 *
	 * @param vertexData  the vertex data to use to populate the array
	 */
	public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData);
        floatBuffer.position(0);
    }

	/**
	 * Sets the shader attribute pointer to the given offset and configures it with the give number of components per vertex
	 * and the given stride between vertices.
	 *
	 * @param dataOffset  the offset into the array to set the pointer
	 * @param attributeLocation  the attribute location id
	 * @param componentCount  the number of components per vertex
	 * @param stride  the number of bytes between vertices
	 */
    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride) {
    	if(attributeLocation < 0) {
    		throw new RuntimeException("attributeLocation out of range: " + attributeLocation);
		}
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
		glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }

    /**
     * Updates the float buffer with the specified vertex data, assuming that
     * the vertex data and the float buffer are the same size.
     */
    public void updateBuffer(float[] vertexData, int start, int count) {
       floatBuffer.position(start);
       floatBuffer.put(vertexData, start, count);
       floatBuffer.position(0);
    }
}
