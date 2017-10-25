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
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.madinnovations.fatlip.Constants.BYTES_PER_FLOAT;

public class VertexBuffer {
    private final int bufferId;
    
    public VertexBuffer(float[] vertexData) {
        // Allocate a buffer.
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new vertex buffer object.");
        }
        
        bufferId = buffers[0];
        
        // Bind to the buffer. 
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        
        // Transfer data to native memory.        
        FloatBuffer vertexArray = ByteBuffer
            .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData);
        vertexArray.position(0);
        
        // Transfer data from native memory to the GPU buffer.              
        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * BYTES_PER_FLOAT,
            vertexArray, GL_STATIC_DRAW);                      
         
        // IMPORTANT: Unbind from the buffer when we're done with it.
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        // We let vertexArray go out of scope, but it won't be released
        // until the next time the garbage collector is run.
    }
        
    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
        int componentCount, int stride) {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
                        
        // This call is slightly different than the glVertexAttribPointer we've
        // used in the past: the last parameter is set to dataOffset, to tell OpenGL
        // to begin reading data at this position of the currently bound buffer.          
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, 
            false, stride, dataOffset);
        glEnableVertexAttribArray(attributeLocation);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);        
    }
}
