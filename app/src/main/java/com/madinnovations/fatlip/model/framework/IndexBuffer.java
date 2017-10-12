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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;
import static com.madinnovations.fatlip.Constants.BYTES_PER_SHORT;

public class IndexBuffer {
    private final int bufferId;

    public IndexBuffer(short[] indexData) {
        // Allocate a buffer.
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new index buffer object.");
        }
        
        bufferId = buffers[0];
        
        // Bind to the buffer. 
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
        
        // Transfer data to native memory.
        ShortBuffer indexArray = ByteBuffer
            .allocateDirect(indexData.length * BYTES_PER_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indexData);
        indexArray.position(0);
                               
        // Transfer data from native memory to the GPU buffer.        
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexArray.capacity() * BYTES_PER_SHORT,
            indexArray, GL_STATIC_DRAW);                             
         
        // IMPORTANT: Unbind from the buffer when we're done with it.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        // We let the native buffer go out of scope, but it won't be released
        // until the next time the garbage collector is run.
    }
    
    public int getBufferId() {
        return bufferId;
    }
}
