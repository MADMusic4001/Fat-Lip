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

// This is a OpenGL ES 1.0 dynamic font rendering system. It loads actual font
// files, generates a font map (texture) from them, and allows rendering of
// text strings.
//
// NOTE: the rendering portions of this class uses a sprite batcher in order
// provide decent speed rendering. Also, rendering assumes a BOTTOM-LEFT
// origin, and the (x,y) positions are relative to that, as well as the
// bottom-left of the string to render.

package com.madinnovations.fatlip.model;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.Matrix;
import android.util.Log;

import com.madinnovations.fatlip.view.framework.SpriteBatch;
import com.madinnovations.fatlip.view.framework.TextureRegion;
import com.madinnovations.fatlip.view.programs.TextShaderProgram;
import com.madinnovations.fatlip.view.utils.TextureHelper;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUseProgram;

@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public class GLText {
	private static final String TAG = "GLTEXT";

	public final static int CHAR_START = 32;
	public final static int CHAR_END = 126;
	public final static int CHAR_CNT = ( ( ( CHAR_END - CHAR_START ) + 1 ) + 1 );

	public final static int CHAR_NONE = 32;
	public final static int CHAR_UNKNOWN = ( CHAR_CNT - 1 );

	public final static int FONT_SIZE_MIN = 6;
	public final static int FONT_SIZE_MAX = 180;

	public final static int CHAR_BATCH_SIZE = 24;

	private AssetManager      assets;
	private SpriteBatch       batch;
	private int               fontPadX, fontPadY;
	private float             fontHeight;
	private float             fontAscent;
	private float             fontDescent;
	private int               textureId;
	private int               textureSize;
	private TextureRegion     textureRgn;
	private float             charWidthMax;
	private float             charHeight;
	private final float[]     charWidths;
	private TextureRegion[]   charRgn;
	private int               cellWidth, cellHeight;
	private int               rowCnt, colCnt;
	private float             scaleX, scaleY;
	private float             spaceX;
	private TextShaderProgram program;
	private int               colorHandle;
	private int               textureUniformHandle;

	/**
	 * Creates a new GLText instance with the given shader program and asset manager
	 *
	 * @param program  the Shader program to use to draw text
	 * @param assets  the AssetManager to use to load the font
	 */
	public GLText(TextShaderProgram program, AssetManager assets) {
		this.assets = assets;

		batch = new SpriteBatch(CHAR_BATCH_SIZE, program );

		charWidths = new float[CHAR_CNT];
		charRgn = new TextureRegion[CHAR_CNT];

		fontPadX = 0;
		fontPadY = 0;
		fontHeight = 0.0f;
		fontAscent = 0.0f;
		fontDescent = 0.0f;
		textureId = -1;
		textureSize = 0;
		charWidthMax = 0;
		charHeight = 0;
		cellWidth = 0;
		cellHeight = 0;
		rowCnt = 0;
		colCnt = 0;
		scaleX = 1.0f;
		scaleY = 1.0f;
		spaceX = 0.0f;

		this.program = program;
		colorHandle = glGetUniformLocation(this.program.getProgram(), "u_Color");
        textureUniformHandle = glGetUniformLocation(this.program.getProgram(), "u_Texture");
	}

	/**
	 * Creates a new GLText instance with the given asset manager and a default shader program.
	 *
	 * @param assets  the AssetManager to use to load the font
	 */
	public GLText(AssetManager assets) {
		this(null, assets);
	}

	/**
	 * Loads the specified font file, creates a texture for the defined character range, and sets up all required values used to render with
	 * it.
	 *
	 * @param file  the name of the font file
	 * @param size  the desired font size
	 * @param padX  the amount of padding to use before and after the rendered text
	 * @param padY  the amount of padding to use above and below the rendered text
	 * @return true if successful, otherwise false
	 */
	public boolean load(String file, int size, int padX, int padY) {
		fontPadX = padX;
		fontPadY = padY;

		Typeface tf = Typeface.createFromAsset( assets, file );
		Paint paint = new Paint();
		paint.setAntiAlias( true );
		paint.setTextSize( size );
		paint.setColor( 0xffffffff );
		paint.setTypeface( tf );

		Paint.FontMetrics fm = paint.getFontMetrics();
		fontHeight = (float)Math.ceil( Math.abs( fm.bottom ) + Math.abs( fm.top ) );
		fontAscent = (float)Math.ceil( Math.abs( fm.ascent ) );
		fontDescent = (float)Math.ceil( Math.abs( fm.descent ) );

		char[] s = new char[2];
		charWidthMax = charHeight = 0;
		float[] w = new float[2];
		int cnt = 0;
		for ( char c = CHAR_START; c <= CHAR_END; c++ )  {
			s[0] = c;
			paint.getTextWidths( s, 0, 1, w );
			charWidths[cnt] = w[0];
			if ( charWidths[cnt] > charWidthMax ) {
				charWidthMax = charWidths[cnt];
			}
			cnt++;
		}
		s[0] = CHAR_NONE;
		paint.getTextWidths( s, 0, 1, w );
		charWidths[cnt] = w[0];
		if ( charWidths[cnt] > charWidthMax ) {
			charWidthMax = charWidths[cnt];
		}

		charHeight = fontHeight;

		cellWidth = (int)charWidthMax + ( 2 * fontPadX );
		cellHeight = (int)charHeight + ( 2 * fontPadY );
		int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight;
		if ( maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX ) {
			return false;
		}

		if ( maxSize <= 24 ) {
			textureSize = 256;
		}
		else if ( maxSize <= 40 ) {
			textureSize = 512;
		}
		else if ( maxSize <= 80 ) {
			textureSize = 1024;
		}
		else {
			textureSize = 2048;
		}

		Bitmap bitmap = Bitmap.createBitmap( textureSize, textureSize, Bitmap.Config.ALPHA_8 );
		Canvas canvas = new Canvas( bitmap );
		bitmap.eraseColor( 0x00000000 );

		colCnt = textureSize / cellWidth;
		rowCnt = (int)Math.ceil( (float)CHAR_CNT / (float)colCnt );

		float x = fontPadX;
		float y = ( cellHeight - 1 ) - fontDescent - fontPadY;
		for ( char c = CHAR_START; c <= CHAR_END; c++ )  {
			s[0] = c;
			canvas.drawText( s, 0, 1, x, y, paint );
			x += cellWidth;
			if ( ( x + cellWidth - fontPadX ) > textureSize )  {
				x = fontPadX;
				y += cellHeight;
			}
		}
		s[0] = CHAR_NONE;
		canvas.drawText( s, 0, 1, x, y, paint );

		textureId = TextureHelper.loadTexture(bitmap);

		x = 0;
		y = 0;
		for ( int c = 0; c < CHAR_CNT; c++ )  {
			charRgn[c] = new TextureRegion( textureSize, textureSize, x, y, cellWidth-1, cellHeight-1 );
			x += cellWidth;
			if ( x + cellWidth > textureSize )  {
				x = 0;
				y += cellHeight;
			}
		}

		textureRgn = new TextureRegion( textureSize, textureSize, 0, 0, textureSize, textureSize );

		return true;                                    // Return Success
	}

	/**
	 * Call this method before all draw() calls using a text instance
	 * NOTE: color is set on a per-batch basis, and fonts should be 8-bit alpha only!!!
	 *
	 * @param vpMatrix  View and projection matrix to use
	 */
	public void begin(float[] vpMatrix)  {
		begin( 1.0f, 1.0f, 1.0f, 1.0f, vpMatrix );
	}

	/**
	 * Call this method before all draw() calls using a text instance
	 * NOTE: color is set on a per-batch basis, and fonts should be 8-bit alpha only!!!
	 *
	 * @param alpha  alpha value for font
	 * @param vpMatrix  View and projection matrix to use
	 */
	public void begin(float alpha, float[] vpMatrix)  {
		begin( 1.0f, 1.0f, 1.0f, alpha, vpMatrix );
	}

	/**
	 * Call this method before all draw() calls using a text instance
	 * NOTE: color is set on a per-batch basis, and fonts should be 8-bit alpha only!!!
	 *
	 * @param red  red value for font
	 * @param green  green value for font
	 * @param blue  blue value for font
	 * @param alpha  alpha value for font
	 * @param vpMatrix  View and projection matrix to use
	 */
	public void begin(float red, float green, float blue, float alpha, float[] vpMatrix)  {
		initDraw(red, green, blue, alpha);
		batch.beginBatch(vpMatrix);
	}

	private void initDraw(float red, float green, float blue, float alpha) {
		glUseProgram(program.getProgram());
		
		float[] color = {red, green, blue, alpha};
		glUniform4fv(colorHandle, 1, color , 0);
		glEnableVertexAttribArray(colorHandle);

		glActiveTexture(GL_TEXTURE0);

		glBindTexture(GL_TEXTURE_2D, textureId);

		glUniform1i(textureUniformHandle, 0);
	}

	/**
	 * Ends the rendering batch
	 */
	public void end()  {
		batch.endBatch();
		glDisableVertexAttribArray(colorHandle);
	}

	/**
	 * Draw text at the specified x,y position
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 * @param z  the z position to draw text at (bottom left of text; including descent)
	 * @param angleDegX  the angle to rotate the text around the x axis
	 * @param angleDegY  the angle to rotate the text around the y axis
	 * @param angleDegZ  the angle to rotate the text around the z axis
	 */
	public void draw(String text, float x, float y, float z, float angleDegX, float angleDegY, float angleDegZ)  {
		float chrHeight = cellHeight * scaleY;
		float chrWidth = cellWidth * scaleX;
		int len = text.length();
		x += ( chrWidth / 2.0f ) - ( fontPadX * scaleX );
		y += ( chrHeight / 2.0f ) - ( fontPadY * scaleY );

		float[] modelMatrix = new float[16];
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, x, y, z);
		Matrix.rotateM(modelMatrix, 0, angleDegZ, 0, 0, 1);
		Matrix.rotateM(modelMatrix, 0, angleDegX, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, angleDegY, 0, 1, 0);
		
		float letterX, letterY; 
		letterX = letterY = 0;
		
		for (int i = 0; i < len; i++)  {
			int c = (int)text.charAt(i) - CHAR_START;
			if (c < 0 || c >= CHAR_CNT) {
				c = CHAR_UNKNOWN;
			}
			batch.drawSprite(letterX, letterY, chrWidth, chrHeight, charRgn[c], modelMatrix);
			letterX += (charWidths[c] + spaceX ) * scaleX;
		}
	}

	/**
	 * Draw text at the specified x,y position
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 * @param z  the z position to draw text at (bottom left of text; including descent)
	 * @param angleDegZ  the angle to rotate the text around the z axis
	 */
	public void draw(String text, float x, float y, float z, float angleDegZ) {
		draw(text, x, y, z, 0, 0, angleDegZ);
	}

	/**
	 * Draw text at the specified x,y position
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 * @param angleDeg  the angle to rotate the text around the z axis
	 */
	public void draw(String text, float x, float y, float angleDeg) {
		draw(text, x, y, 0, angleDeg);
	}

	/**
	 * Draw text at the specified x,y position
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 */
	public void draw(String text, float x, float y) {
		draw(text, x, y, 0, 0);
	}

	/**
	 * Draw text CENTERED at the specified x,y position
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 * @param z  the z position to draw text at (bottom left of text; including descent)
	 * @param angleDegX  the angle to rotate the text around the x axis
	 * @param angleDegY  the angle to rotate the text around the y axis
	 * @param angleDegZ  the angle to rotate the text around the z axis
	 * @return the total width of the text that was drawn
	 */
	public float drawC(String text, float x, float y, float z, float angleDegX, float angleDegY, float angleDegZ)  {
		float len = getLength( text );
		draw( text, x - ( len / 2.0f ), y - ( getCharHeight() / 2.0f ), z, angleDegX, angleDegY, angleDegZ );
		return len;
	}

	/**
	 * Draw text CENTERED at the specified x,y position
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 * @param z  the z position to draw text at (bottom left of text; including descent)
	 * @param angleDegZ  the angle to rotate the text around the z axis
	 * @return the total width of the text that was drawn
	 */
	public float drawC(String text, float x, float y, float z, float angleDegZ) {
		return drawC(text, x, y, z, 0, 0, angleDegZ);
	}

	/**
	 * Draw text CENTERED at the specified x,y position
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 * @param angleDeg  the angle to rotate the text around the z axis
	 * @return the total width of the text that was drawn
	 */
	public float drawC(String text, float x, float y, float angleDeg) {
		return drawC(text, x, y, 0, angleDeg);
	}

	/**
	 * Draw text CENTERED at the specified x,y position
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 * @return the total width of the text that was drawn
	 */
	public float drawC(String text, float x, float y) {
		float len = getLength( text );
		return drawC(text, x - (len / 2.0f), y - ( getCharHeight() / 2.0f ), 0);
	}

	/**
	 * Draw text CENTERED at the specified x position only
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 * @return the total width of the text that was drawn
	 */
	public float drawCX(String text, float x, float y)  {
		float len = getLength( text );
		draw( text, x - ( len / 2.0f ), y );
		return len;
	}

	/**
	 * Draw text CENTERED at the specified y position only
	 *
	 * @param text  the text string to draw
	 * @param x  the x position to draw text at (bottom left of text; including descent)
	 * @param y  the y position to draw text at (bottom left of text; including descent)
	 */
	public void drawCY(String text, float x, float y)  {
		draw( text, x, y - ( getCharHeight() / 2.0f ) );
	}

	//--Set Scale--//
	// D: set the scaling to use for the font
	// A: scale - uniform scale for both x and y axis scaling
	//    sx, sy - separate x and y axis scaling factors
	// R: [none]

	/**
	 * Set the scaling to use for the font
	 *
	 * @param scale  uniform scale for both x and y axis scaling
	 */
	public void setScale(float scale)  {
		scaleX = scaleY = scale;
	}

	/**
	 * Return the length of the specified string if rendered using current settings
	 *
	 * @param text  the string to get length for
	 * @return the length of the specified string (pixels)
	 */
	public float getLength(String text) {
		float len = 0.0f;
		int strLen = text.length();
		for ( int i = 0; i < strLen; i++ ) {
			int c = (int)text.charAt( i ) - CHAR_START;
			len += ( charWidths[c] * scaleX );
		}
		len += ( strLen > 1 ? ( ( strLen - 1 ) * spaceX ) * scaleX : 0 );
		return len;
	}

	/**
	 * Returns the scaled max character width
	 *
	 * @return  the scaled max character width
	 */
	public float getCharWidthMax()  {
		return ( charWidthMax * scaleX );
	}

	/**
	 * Returns the scaled max character height
	 *
	 * @return the scaled max character height
	 */
	public float getCharHeight() {
		return ( charHeight * scaleY );
	}

	/**
	 * Set the scaling to use for the font
	 *
	 * @param sx  separate x axis scaling factor
	 * @param sy  separate y axis scaling factor
	 */
	public void setScale(float sx, float sy)  {
		scaleX = sx;
		scaleY = sy;
	}

	// Getters and setters
	public float getScaleX()  {
		return scaleX;
	}
	public float getScaleY()  {
		return scaleY;
	}
	public void setSpace(float space)  {
		spaceX = space;
	}
	public float getSpace()  {
		return spaceX;
	}

	/**
	 * Return the scaled width/height of a character, or max character width
	 *
	 * @param chr  the character to get width for
	 * @return the requested character size (scaled)
	 */
	public float getCharWidth(char chr)  {
		int c = chr - CHAR_START;
		return ( charWidths[c] * scaleX );
	}

	/**
	 * Return Font Ascent
	 *
	 * @return the font ascent
	 */
	public float getAscent() {
		return (fontAscent * scaleY);
	}

	/**
	 * Return Font Descent
	 *
	 * @return  the font descent
	 */
	public float getDescent()  {
		return ( fontDescent * scaleY );
	}

	/**
	 * Return Font Height (Actual)
	 *
	 * @return  the actual font height
	 */
	public float getHeight() {
		return (fontHeight * scaleY);
	}

	/**
	 * Draws the entire font texture (NOTE: for testing purposes only)
	 *
	 * @param width  the width of the area to draw to. this is used to draw the texture to the top-left corner.
	 * @param height  the height of the area to draw to. this is used to draw the texture to the top-left corner.
	 * @param vpMatrix  View and projection matrix to use
	 */
	public void drawTexture(int width, int height, float[] vpMatrix)  {
		initDraw(1.0f, 1.0f, 1.0f, 1.0f);

		batch.beginBatch(vpMatrix);
		float[] idMatrix = new float[16];
		Matrix.setIdentityM(idMatrix, 0);
		batch.drawSprite(width - (textureSize / 2), height - ( textureSize / 2 ),
				textureSize, textureSize, textureRgn, idMatrix);
		Log.d(TAG, "drawTexture: x = " + (width - (textureSize/2)));
		Log.d(TAG, "drawTexture: y = " + (height - (textureSize/2)));
		batch.endBatch();
	}
}
