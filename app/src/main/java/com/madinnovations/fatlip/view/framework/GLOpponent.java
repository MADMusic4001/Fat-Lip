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

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.madinnovations.fatlip.model.Opponent;
import com.madinnovations.fatlip.view.programs.TextureShaderProgram;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static com.madinnovations.fatlip.Constants.BYTES_PER_FLOAT;

/**
 * The OpenGL information needed to draw an Opponent on the screen
 */
@SuppressWarnings("SameParameterValue")
public class GLOpponent {
	private static final String TAG = "GLOpponent";
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT
			+ TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private final VertexArray vertexArray;
	private final ByteBuffer indexArray;
	private final int indexCount;
	private float scale = 1.0f;
	private final Opponent opponent;
	private final Bitmap opponentBitmap;
	private final static byte[] indexData = {
			0, 5, 2,
			0, 13, 5,
			0, 16, 13,
			0, 17, 16,
			0, 18, 17,
			0, 1, 18,
			1, 19, 18,
			1, 14, 19,
			1, 10, 14,
			1, 3, 10,
			2, 5, 4,
			2, 4, 7,
			2, 7, 3,
			3, 7, 8,
			3, 8, 11,
			3, 11, 10,
			4, 5, 6,
			4, 6, 7,
			5, 13, 6,
			6, 13, 12,
			6, 12, 15,
			6, 15, 9,
			6, 9, 7,
			7, 9, 8,
			8, 9, 10,
			8, 10, 11,
			9, 15, 14,
			9, 14, 10,
			12, 13, 15,
			13, 16, 20,
			13, 20, 14,
			13, 14, 15,
			14, 20, 19,
			16, 17, 20,
			17, 18, 20,
			18, 19, 20
	};
	private final static byte[] noNoseIndexData = {
			0, 5, 2,
			0, 12, 5,
			0, 13, 12,
			0, 1, 13,
			1, 14, 13,
			1, 15, 14,
			1, 10, 15,
			1, 3, 10,
			2, 5, 4,
			2, 4, 7,
			2, 7, 3,
			3, 7, 8,
			3, 8, 11,
			3, 11, 10,
			4, 5, 6,
			4, 6, 7,
			5, 12, 6,
			6, 12, 16,
			6, 16, 9,
			6, 9, 7,
			7, 9, 8,
			8, 9, 10,
			8, 10, 11,
			9, 16, 15,
			9, 15, 10,
			12, 13, 16,
			13, 14, 16,
			14, 15, 16
	};

	/**
	 * Creates a new GLOpponent instance.
	 */
	public GLOpponent(Opponent opponent, Bitmap opponentBitmap) {
		this.opponent = opponent;
		this.opponentBitmap = opponentBitmap;
		try {
			vertexArray = new VertexArray(generateVertexData());
			byte[] indexData;
			if(opponent.getNose() != null && opponent.getNose().width() > 0 &&
					opponent.getNose().height() > 0) {
				indexData = GLOpponent.indexData;
			}
			else {
				indexData = GLOpponent.noNoseIndexData;
			}
			indexCount = indexData.length;
			indexArray = ByteBuffer.allocateDirect(indexCount).put(indexData);
			indexArray.position(0);
		}
		catch (Exception e) {
			Log.e(TAG, "draw: Exception caught ", e);
			throw e;
		}
	}

	/**
	 * Binds the vertex and texture coordinate data to the shader program attributes;
	 *
	 * @param textureProgram  the OpenGL texture shader program
	 */
	public void bindData(TextureShaderProgram textureProgram) {
		vertexArray.setVertexAttribPointer(0, textureProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT,
										   STRIDE);

		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, textureProgram.getTextureCoordinateAttributeLocation(),
										   TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
	}

	/**
	 * Renders the Splash image
	 */
	public void draw() {
		glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_BYTE, indexArray);
	}

	public void unbindData(TextureShaderProgram textureProgram) {
		glDisableVertexAttribArray(textureProgram.getPositionAttributeLocation());
		glDisableVertexAttribArray(textureProgram.getTextureCoordinateAttributeLocation());
	}

	private float[] generateVertexData() {
		float[] vertexData;
		boolean hasNose = false;
		if(opponent.getNose() != null && opponent.getNose().width() > 0 &&
				opponent.getNose().height() > 0) {
			vertexData = new float[22*4];
			hasNose = true;
		}
		else {
			vertexData = new float[18*4];
		}

		float offsetToCenter = scale / 2.0f;

		vertexData[0] = -offsetToCenter;
		vertexData[1] = -offsetToCenter;
		vertexData[2] = 0f;
		vertexData[3] = 1f;

		vertexData[4] = offsetToCenter;
		vertexData[5] = -offsetToCenter;
		vertexData[6] = 1f;
		vertexData[7] = 1f;

		vertexData[8] = -offsetToCenter;
		vertexData[9] = offsetToCenter;
		vertexData[10] = 0f;
		vertexData[11] = 0f;

		vertexData[12] = offsetToCenter;
		vertexData[13] = offsetToCenter;
		vertexData[14] = 1f;
		vertexData[15] = 0f;

		scale = 0.4f;
		generateVerticesForRect(vertexData, 16, opponentBitmap.getWidth(), opponentBitmap.getHeight(),
								opponent.getLeftEye());
		generateVerticesForRect(vertexData, 32, opponentBitmap.getWidth(), opponentBitmap.getHeight(),
								opponent.getRightEye());
		if(hasNose) {
			generateVerticesForRect(vertexData, 48, opponentBitmap.getWidth(), opponentBitmap.getHeight(),
									opponent.getNose());
			generateVerticesForMouth(vertexData, 64, opponentBitmap.getWidth(), opponentBitmap.getHeight(),
									 opponent.getMouth());
		}
		else {
			generateVerticesForMouth(vertexData, 48, opponentBitmap.getWidth(), opponentBitmap.getHeight(),
									 opponent.getMouth());
		}

		return vertexData;
	}

	private void generateVerticesForRect(float[] vertexData, int destinationOffset, float width, float height, Rect sourceRect) {
		float offsetToCenter = scale / 2.0f;
		RectF textureRect = new RectF();
		RectF modelRect = new RectF();

		textureRect.left = ((float)sourceRect.left)/width;
		modelRect.left = textureRect.left * scale - offsetToCenter;
		textureRect.right = ((float)sourceRect.right)/width;
		modelRect.right = textureRect.right * scale - offsetToCenter;
		textureRect.top = ((float)sourceRect.top)/height;
		modelRect.top = (1 - textureRect.top) * scale - offsetToCenter;
		textureRect.bottom = ((float)sourceRect.bottom)/height;
		modelRect.bottom = (1 - textureRect.bottom) * scale - offsetToCenter;

		vertexData[destinationOffset] = modelRect.left;
		vertexData[destinationOffset+1] = modelRect.top;
		vertexData[destinationOffset+2] = textureRect.left;
		vertexData[destinationOffset+3] = textureRect.top;

		vertexData[destinationOffset+4] = modelRect.left;
		vertexData[destinationOffset+5] = modelRect.bottom;
		vertexData[destinationOffset+6] = textureRect.left;
		vertexData[destinationOffset+7] = textureRect.bottom;

		vertexData[destinationOffset+8] = modelRect.right;
		vertexData[destinationOffset+9] = modelRect.bottom;
		vertexData[destinationOffset+10] = textureRect.right;
		vertexData[destinationOffset+11] = textureRect.bottom;

		vertexData[destinationOffset+12] = modelRect.right;
		vertexData[destinationOffset+13] = modelRect.top;
		vertexData[destinationOffset+14] = textureRect.right;
		vertexData[destinationOffset+15] = textureRect.top;
	}

	private void generateVerticesForMouth(float[] vertexData, int destinationOffset, float width, float height,
										  Rect sourceRect) {
		float offsetToCenter = scale / 2.0f;
		RectF textureRect = new RectF();
		RectF modelRect = new RectF();
		PointF midLeftTexturePoint = new PointF();
		PointF midLeftModelPoint = new PointF();
		PointF midTexturePoint = new PointF();
		PointF midModelPoint = new PointF();
		PointF midRightTexturePoint = new PointF();
		PointF midRightModelPoint = new PointF();

		textureRect.left = ((float)sourceRect.left)/width;
		modelRect.left = textureRect.left * scale - offsetToCenter;
		textureRect.right = ((float)sourceRect.right)/width;
		modelRect.right = textureRect.right * scale - offsetToCenter;
		textureRect.bottom = ((float)sourceRect.bottom)/height;
		modelRect.bottom = (1 - textureRect.bottom) * scale - offsetToCenter;
		textureRect.top = ((float)sourceRect.top)/height;
		modelRect.top = (1 - textureRect.top) * scale - offsetToCenter;
		int third = (sourceRect.right - sourceRect.left) / 3;
		midLeftTexturePoint.x = (sourceRect.left + third)/width;
		midLeftModelPoint.x = midLeftTexturePoint.x * scale - offsetToCenter;
		midLeftTexturePoint.y = textureRect.bottom;
		midLeftModelPoint.y = modelRect.bottom;
		midTexturePoint.x = (sourceRect.left + (sourceRect.right - sourceRect.left))/ (2.0f * width);
		midModelPoint.x = midTexturePoint.x * scale - offsetToCenter;
		midTexturePoint.y = sourceRect.top/height - ((sourceRect.top - sourceRect.bottom) / (height * 3.0f));
		midModelPoint.y = (1 - midTexturePoint.y) * scale - offsetToCenter;
		midRightTexturePoint.x = (sourceRect.right - third)/width;
		midRightModelPoint.x = midRightTexturePoint.x * scale - offsetToCenter;
		midRightTexturePoint.y = textureRect.bottom;
		midRightModelPoint.y = modelRect.bottom;

		vertexData[destinationOffset] = modelRect.left;
		vertexData[destinationOffset+1] = modelRect.top;
		vertexData[destinationOffset+2] = textureRect.left;
		vertexData[destinationOffset+3] = textureRect.top;

		vertexData[destinationOffset+4] = midLeftModelPoint.x;
		vertexData[destinationOffset+5] = midLeftModelPoint.y;
		vertexData[destinationOffset+6] = midLeftTexturePoint.x;
		vertexData[destinationOffset+7] = midLeftTexturePoint.y;

		vertexData[destinationOffset+8] = midRightModelPoint.x;
		vertexData[destinationOffset+9] = midRightModelPoint.y;
		vertexData[destinationOffset+10] = midRightTexturePoint.x;
		vertexData[destinationOffset+11] = midRightTexturePoint.y;

		vertexData[destinationOffset+12] = modelRect.right;
		vertexData[destinationOffset+13] = modelRect.top;
		vertexData[destinationOffset+14] = textureRect.right;
		vertexData[destinationOffset+15] = textureRect.top;

		Log.d(TAG, "generateVerticesForMouth: midTexturePoint = " + midTexturePoint);
		Log.d(TAG, "generateVerticesForMouth: modelRect = " + modelRect);
		Log.d(TAG, "generateVerticesForMouth: midLeftModelPoint = " + midLeftModelPoint);
		Log.d(TAG, "generateVerticesForMouth: midModelPoint = " + midModelPoint);
		Log.d(TAG, "generateVerticesForMouth: midRightModelPoint = " + midRightModelPoint);
		vertexData[destinationOffset+16] = midModelPoint.x;
		vertexData[destinationOffset+17] = midModelPoint.y;
		vertexData[destinationOffset+18] = midTexturePoint.x;
		vertexData[destinationOffset+19] = midTexturePoint.y;
	}
}
