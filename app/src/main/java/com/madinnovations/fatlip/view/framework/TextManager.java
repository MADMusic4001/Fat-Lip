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

import com.madinnovations.fatlip.model.framework.TextObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

/**
 * Class to manage resources needed to draw text with OpenGL
 */
public class TextManager {
	private static final float TEXT_UV_BOX_WIDTH = 0.125f;
	private static final float TEXT_WIDTH = 32.0f;
	private static final float TEXT_SPACESIZE = 20.0f;

	private FloatBuffer vertexBuffer;
	private FloatBuffer textureBuffer;
	private FloatBuffer colorBuffer;
	private ShortBuffer drawListBuffer;

	private float[] vecs;
	private float[] uvs;
	private short[] indices;
	private float[] colors;

	private int index_vecs;
	private int index_indices;
	private int index_uvs;
	private int index_colors;

	private int textureNbr;

	private float uniformScale;

	public static int[] l_size = {36, 29, 30, 34, 25, 25, 34, 33, 11, 20, 31, 24, 48, 35, 39, 29,
		42, 31, 27, 31, 34, 35, 46, 35, 31, 27, 30, 26, 28, 26, 31, 28, 28, 28, 29, 29, 14, 24, 30, 18,
		26, 14, 14, 14, 25, 28, 31, 0, 0, 38, 39, 12, 36, 34, 0, 0, 0, 38, 0, 0, 0, 0, 0, 0};

	public Vector<TextObject> txtCollection;

	public TextManager() {
		txtCollection = new Vector<>();

		vecs = new float[3*10];
		colors = new float[4 * 10];
		uvs = new float[2 *10];
		indices = new short[10];

		textureNbr = 0;
	}

	public void addText(TextObject textObject) {
		txtCollection.add(textObject);
	}

	public void setTextureId(int val) {
		textureNbr = val;
	}

	public void AddCharRenderInformation(float[] vec, float[] cs, float[] uv, short[] indi) {
		// We need a base value because the object has indices related to
		// that object and not to this collection so basicly we need to
		// translate the indices to align with the vertexlocation in ou
		// vecs array of vectors.
		short base = (short) (index_vecs / 3);

		// We should add the vec, translating the indices to our saved vector
		for (float aVec : vec) {
			vecs[index_vecs] = aVec;
			index_vecs++;
		}

		// We should add the colors.
		for (float c : cs) {
			colors[index_colors] = c;
			index_colors++;
		}

		// We should add the uvs
		for (float anUv : uv) {
			uvs[index_uvs] = anUv;
			index_uvs++;
		}

		// We handle the indices
		for (short anIndi : indi) {
			indices[index_indices] = (short) (base + anIndi);
			index_indices++;
		}
	}

	public void PrepareDrawInfo() {
		// Reset the indices.
		index_vecs = 0;
		index_indices = 0;
		index_uvs = 0;
		index_colors = 0;

		// Get the total amount of characters
		int charCount = 0;
		for (TextObject txt : txtCollection) {
			if(txt!=null) {
				if(!(txt.getText() == null)) {
					charCount += txt.getText().length();
				}
			}
		}

		// Create the arrays we need with the correct size.
		vecs = null;
		colors = null;
		uvs = null;
		indices = null;

		vecs = new float[charCount * 12];
		colors = new float[charCount * 16];
		uvs = new float[charCount * 8];
		indices = new short[charCount * 6];

	}

	public void PrepareDraw() {
		// Setup all the arrays
		PrepareDrawInfo();

		// Using the iterator protects for problems with concurrency
		for (TextObject txt : txtCollection) {
			if (txt != null) {
				if (!(txt.getText() == null)) {
					convertTextToTriangleInfo(txt);
				}
			}
		}
	}

	public void Draw(float[] m) {
		// Set the correct shader for our grid object.
		GLES20.glUseProgram(GLGraphicTools.sp_Text);

		// The vertex buffer.
		ByteBuffer bb = ByteBuffer.allocateDirect(vecs.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vecs);
		vertexBuffer.position(0);

		// The vertex buffer.
		ByteBuffer bb3 = ByteBuffer.allocateDirect(colors.length * 4);
		bb3.order(ByteOrder.nativeOrder());
		colorBuffer = bb3.asFloatBuffer();
		colorBuffer.put(colors);
		colorBuffer.position(0);

		// The texture buffer
		ByteBuffer bb2 = ByteBuffer.allocateDirect(uvs.length * 4);
		bb2.order(ByteOrder.nativeOrder());
		textureBuffer = bb2.asFloatBuffer();
		textureBuffer.put(uvs);
		textureBuffer.position(0);

		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(indices);
		drawListBuffer.position(0);

		// get handle to vertex shader's vPosition member
		int mPositionHandle = GLES20.glGetAttribLocation(GLGraphicTools.sp_Text,
														 "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the background coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, 3,
									 GLES20.GL_FLOAT, false,
									 0, vertexBuffer);

		int mTexCoordLoc = GLES20.glGetAttribLocation(GLGraphicTools.sp_Text,
													  "a_texCoord" );

		// Prepare the texturecoordinates
		GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
									   false,
									   0, textureBuffer);

		GLES20.glEnableVertexAttribArray ( mPositionHandle );
		GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

		int mColorHandle = GLES20.glGetAttribLocation(GLGraphicTools.sp_Text,
													  "a_Color");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mColorHandle);

		// Prepare the background coordinate data
		GLES20.glVertexAttribPointer(mColorHandle, 4,
									 GLES20.GL_FLOAT, false,
									 0, colorBuffer);

		// get handle to shape's transformation matrix
		int mtrxhandle = GLES20.glGetUniformLocation(GLGraphicTools.sp_Text,
													 "uMVPMatrix");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

		int mSamplerLoc = GLES20.glGetUniformLocation (GLGraphicTools.sp_Text,
													   "s_texture" );

		// Set the sampler texture unit to our selected id
		GLES20.glUniform1i ( mSamplerLoc, textureNbr);

		// Draw the triangle
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
							  GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mTexCoordLoc);
		GLES20.glDisableVertexAttribArray(mColorHandle);
	}

	private int convertCharToIndex(int c_val) {
		int index = -1;

		// Retrieve the index
		if(c_val>64&&c_val<91) // A-Z
			index = c_val - 65;
		else if(c_val>96&&c_val<123) // a-z
			index = c_val - 97;
		else if(c_val>47&&c_val<58) // 0-9
			index = c_val - 48 + 26;
		else if(c_val==43) // +
			index = 38;
		else if(c_val==45) // -
			index = 39;
		else if(c_val==33) // !
			index = 36;
		else if(c_val==63) // ?
			index = 37;
		else if(c_val==61) // =
			index = 40;
		else if(c_val==58) // :
			index = 41;
		else if(c_val==46) // .
			index = 42;
		else if(c_val==44) // ,
			index = 43;
		else if(c_val==42) // *
			index = 44;
		else if(c_val==36) // $
			index = 45;

		return index;
	}

	private void convertTextToTriangleInfo(TextObject val) {
		// Get attributes from text object
		float x = val.getX();
		float y = val.getY();
		String text = val.getText();

		// Create
		for(int j=0; j<text.length(); j++) {
			// get ascii value
			char c = text.charAt(j);
			int c_val = (int)c;

			int index = convertCharToIndex(c_val);

			if(index==-1) {
				// unknown character, we will add a space for it to be save.
				x += ((TEXT_SPACESIZE) * uniformScale);
				continue;
			}

			// Calculate the uv parts
			int row = index / 8;
			int col = index % 8;

			float v = row * TEXT_UV_BOX_WIDTH;
			float v2 = v + TEXT_UV_BOX_WIDTH;
			float u = col * TEXT_UV_BOX_WIDTH;
			float u2 = u + TEXT_UV_BOX_WIDTH;

			// Creating the triangle information
			float[] vec = new float[12];
			float[] uv = new float[8];
			float[] colors = new float[16];

			vec[0] = x;
			vec[1] = y + (TEXT_WIDTH * uniformScale);
			vec[2] = 0.99f;
			vec[3] = x;
			vec[4] = y;
			vec[5] = 0.99f;
			vec[6] = x + (TEXT_WIDTH * uniformScale);
			vec[7] = y;
			vec[8] = 0.99f;
			vec[9] = x + (TEXT_WIDTH * uniformScale);
			vec[10] = y + (TEXT_WIDTH * uniformScale);
			vec[11] = 0.99f;

			colors = new float[]
					{val.getColor()[0], val.getColor()[1], val.getColor()[2], val.getColor()[3],
							val.getColor()[0], val.getColor()[1], val.getColor()[2], val.getColor()[3],
							val.getColor()[0], val.getColor()[1], val.getColor()[2], val.getColor()[3],
							val.getColor()[0], val.getColor()[1], val.getColor()[2], val.getColor()[3]
					};
			// 0.001f = texture bleeding hack/fix
			uv[0] = u+0.001f;
			uv[1] = v+0.001f;
			uv[2] = u+0.001f;
			uv[3] = v2-0.001f;
			uv[4] = u2-0.001f;
			uv[5] = v2-0.001f;
			uv[6] = u2-0.001f;
			uv[7] = v+0.001f;

			short[] inds = {0, 1, 2, 0, 2, 3};

			// Add our triangle information to our collection for 1 render call.
			AddCharRenderInformation(vec, colors, uv, inds);

			// Calculate the new position
			x += ((l_size[index]/2)  * uniformScale);
		}
	}

	public float getUniformScale() {
		return uniformScale;
	}

	public void setUniformScale(float uniformScale) {
		this.uniformScale = uniformScale;
	}
}
