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

package com.madinnovations.fatlip.view.screens;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.android.texample2.GLText;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.view.activities.FatLipGame;
import com.madinnovations.fatlip.view.framework.GLGraphicTools;
import com.madinnovations.fatlip.view.framework.Screen;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.inject.Inject;

/**
 * Renders the home screen
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class HomeScreen extends Screen {
	@Inject
	protected AssetManager assetManager;
	private static final String TAG = "HomeScreen";
	private static short indices[];
	private FloatBuffer  vertexBuffer;
	private ShortBuffer  drawListBuffer;
	private FloatBuffer  uvBuffer;
	// Our matrices
	private final float[] mtrxProjection = new float[16];
	private final float[] mtrxView = new float[16];
	private final float[] mtrxProjectionAndView = new float[16];
	private int    screenWidth;
	private int    screenHeight;
	private GLText glText;

	@Inject
	public HomeScreen(FatLipGame game) {
		super(game);
	}

	@Override
	public void onCreate(int width, int height, boolean contextLost) {
		Log.d(TAG, "onCreate: assetManager = " + assetManager);
		// We need to know the current width and height.
		screenWidth = width;
		screenHeight = height;

		// Create the triangles
		SetupTriangle();
		// Create the image information
		SetupImage();

		// Set the clear color to black
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);

		// Create the shaders, solid color
		int vertexShader = GLGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
													 GLGraphicTools.vs_SolidColor);
		int fragmentShader = GLGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
													   GLGraphicTools.fs_SolidColor);

		GLGraphicTools.sp_SolidColor = GLES20.glCreateProgram();
		GLES20.glAttachShader(GLGraphicTools.sp_SolidColor, vertexShader);
		GLES20.glAttachShader(GLGraphicTools.sp_SolidColor, fragmentShader);
		GLES20.glLinkProgram(GLGraphicTools.sp_SolidColor);

		// Create the shaders, images
		vertexShader = GLGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
												 GLGraphicTools.vs_Image);
		fragmentShader = GLGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
												   GLGraphicTools.fs_Image);

		GLGraphicTools.sp_Image = GLES20.glCreateProgram();
		GLES20.glAttachShader(GLGraphicTools.sp_Image, vertexShader);
		GLES20.glAttachShader(GLGraphicTools.sp_Image, fragmentShader);
		GLES20.glLinkProgram(GLGraphicTools.sp_Image);

		// Set our shader programm
		GLES20.glUseProgram(GLGraphicTools.sp_Image);

		// Redo the Viewport, making it fullscreen.
		GLES20.glViewport(0, 0, screenWidth, screenHeight);

		// Clear our matrices
		for(int i=0;i<16;i++)
		{
			mtrxProjection[i] = 0.0f;
			mtrxView[i] = 0.0f;
			mtrxProjectionAndView[i] = 0.0f;
		}

		// Setup our screen width and height for normal sprite translation.
		Matrix.orthoM(mtrxProjection, 0, 0f, screenWidth, 0.0f, screenHeight, 0, 50);

		// Set the camera position (View matrix)
		Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f,
						  0.0f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);

		glText = new GLText(assetManager);
		glText.load("Roboto-Regular.ttf", 72, 2, 2 );  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels))

		// enable texture + alpha blending
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void update(float deltaTime) {
	}

	@Override
	public void present(float deltaTime) {
		// clear Screen and Depth Buffer,
		// we have set the clear color as black.
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// get handle to vertex shader's vPosition member
		int mPositionHandle = GLES20.glGetAttribLocation(GLGraphicTools.sp_Image, "vPosition");

		// Enable generic vertex attribute array
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

		// Get handle to texture coordinates location
		int mTexCoordLoc = GLES20.glGetAttribLocation(GLGraphicTools.sp_Image, "a_texCoord" );

		// Enable generic vertex attribute array
		GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

		// Prepare the texturecoordinates
		GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

		// Get handle to shape's transformation matrix
		int mtrxhandle = GLES20.glGetUniformLocation(GLGraphicTools.sp_Image, "uMVPMatrix");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mtrxProjectionAndView, 0);

		// Get handle to textures locations
		int mSamplerLoc = GLES20.glGetUniformLocation (GLGraphicTools.sp_Image, "s_texture" );

		// Set the sampler texture unit to 0, where we have saved the texture.
		GLES20.glUniform1i ( mSamplerLoc, 0);

		// Draw the triangle
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
							  GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mTexCoordLoc);

		glText.begin(1.0f, 0.0f, 0.0f, 1.0f, mtrxProjectionAndView);
		glText.drawC("Test text", 800.0f, 1024.0f, 0.0f, 0.0f, 0.0f, 0.0f);
		glText.end();
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {
		GLES20.glClearColor(0f, 1f, 0f, 1f);
	}

	@Override
	public void dispose() {}

	private void SetupTriangle() {
		// We have create the vertices of our view.
		float[] vertices = new float[]{
				0.0f, (float) screenHeight, 0.0f,
				0.0f, 0.0f, 0.0f,
				(float) screenWidth, 0.0f, 0.0f,
				(float) screenWidth, (float) screenHeight, 0.0f};

		indices = new short[] {0, 1, 2, 0, 2, 3};

		// The vertex buffer.
		ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(indices);
		drawListBuffer.position(0);
	}

	private void SetupImage() {
		// Create our UV coordinates.
		float[] uvs = new float[]{
				0.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f
		};

		// The texture buffer
		ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
		bb.order(ByteOrder.nativeOrder());
		uvBuffer = bb.asFloatBuffer();
		uvBuffer.put(uvs);
		uvBuffer.position(0);

		// Generate Textures, if more needed, alter these numbers.
		int[] texturenames = new int[1];
		GLES20.glGenTextures(1, texturenames, 0);

		// Temporary create a bitmap
		Bitmap bmp = Assets.homeScreenBitmap;

		// Bind texture to texturename
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);

		// Set filtering
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
							   GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
							   GLES20.GL_LINEAR);

		// Set wrapping mode
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
							   GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
							   GLES20.GL_CLAMP_TO_EDGE);

		// Load the bitmap into the bound texture.
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		// We are done using the bitmap so we should recycle it.
		bmp.recycle();
	}
}
