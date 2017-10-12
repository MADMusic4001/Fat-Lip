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
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.madinnovations.fatlip.view.framework.SpriteBatch;
import com.madinnovations.fatlip.view.framework.TextureRegion;
import com.madinnovations.fatlip.view.programs.TextShaderProgram;
import com.madinnovations.fatlip.view.utils.TextureHelper;

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

	//--Constructor--//
	// D: save program + asset manager, create arrays, and initialize the members

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
		colorHandle = GLES20.glGetUniformLocation(this.program.getProgram(), "u_Color");
        textureUniformHandle = GLES20.glGetUniformLocation(this.program.getProgram(), "u_Texture");
	}

	// Constructor using the default program (BatchTextProgram)

	/**
	 * Creates a new GLText instance with the given asset manager and a default shader program.
	 *
	 * @param assets  the AssetManager to use to load the font
	 */
	public GLText(AssetManager assets) {
		this(null, assets);
	}

	//--Load Font--//
	// description
	//    this will load the specified font file, create a texture for the defined
	//    character range, and setup all required values used to render with it.
	// arguments:
	//    file - Filename of the font (.ttf, .otf) to use. In 'Assets' folder.
	//    size - Requested pixel size of font (height)
	//    padX, padY - Extra padding per character (X+Y Axis); to prevent overlapping characters.

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

	//--Begin/End Text Drawing--//
	// D: call these methods before/after (respectively all draw() calls using a text instance
	//    NOTE: color is set on a per-batch basis, and fonts should be 8-bit alpha only!!!
	// A: red, green, blue - RGB values for font (default = 1.0)
	//    alpha - optional alpha value for font (default = 1.0)
	// 	  vpMatrix - View and projection matrix to use
	// R: [none]

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

	void initDraw(float red, float green, float blue, float alpha) {
		GLES20.glUseProgram(program.getProgram());
		
		float[] color = {red, green, blue, alpha};
		GLES20.glUniform4fv(colorHandle, 1, color , 0);
		GLES20.glEnableVertexAttribArray(colorHandle);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

		GLES20.glUniform1i(textureUniformHandle, 0);
	}

	public void end()  {
		batch.endBatch();
		GLES20.glDisableVertexAttribArray(colorHandle);
	}

	//--Draw Text--//
	// D: draw text at the specified x,y position
	// A: text - the string to draw
	//    x, y, z - the x, y, z position to draw text at (bottom left of text; including descent)
	//    angleDeg - angle to rotate the text
	// R: [none]
	public void draw(String text, float x, float y, float z, float angleDegX, float angleDegY, float angleDegZ)  {
		float chrHeight = cellHeight * scaleY;          // Calculate Scaled Character Height
		float chrWidth = cellWidth * scaleX;            // Calculate Scaled Character Width
		int len = text.length();                        // Get String Length
		x += ( chrWidth / 2.0f ) - ( fontPadX * scaleX );  // Adjust Start X
		y += ( chrHeight / 2.0f ) - ( fontPadY * scaleY );  // Adjust Start Y
		
		// create a model matrix based on x, y and angleDeg
		float[] modelMatrix = new float[16];
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, x, y, z);
		Matrix.rotateM(modelMatrix, 0, angleDegZ, 0, 0, 1);
		Matrix.rotateM(modelMatrix, 0, angleDegX, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, angleDegY, 0, 1, 0);
		
		float letterX, letterY; 
		letterX = letterY = 0;
		
		for (int i = 0; i < len; i++)  {              // FOR Each Character in String
			int c = (int)text.charAt(i) - CHAR_START;  // Calculate Character Index (Offset by First Char in Font)
			if (c < 0 || c >= CHAR_CNT)                // IF Character Not In Font
				c = CHAR_UNKNOWN;                         // Set to Unknown Character Index
			//TODO: optimize - applying the same model matrix to all the characters in the string
			batch.drawSprite(letterX, letterY, chrWidth, chrHeight, charRgn[c], modelMatrix);  // Draw the Character
			letterX += (charWidths[c] + spaceX ) * scaleX;    // Advance X Position by Scaled Character Width
		}
	}
	public void draw(String text, float x, float y, float z, float angleDegZ) {
		draw(text, x, y, z, 0, 0, angleDegZ);
	}
	public void draw(String text, float x, float y, float angleDeg) {
		draw(text, x, y, 0, angleDeg);
	}
	
	public void draw(String text, float x, float y) {
		draw(text, x, y, 0, 0);
	}

	//--Draw Text Centered--//
	// D: draw text CENTERED at the specified x,y position
	// A: text - the string to draw
	//    x, y, z - the x, y, z position to draw text at (bottom left of text)
	//    angleDeg - angle to rotate the text
	// R: the total width of the text that was drawn
	public float drawC(String text, float x, float y, float z, float angleDegX, float angleDegY, float angleDegZ)  {
		float len = getLength( text );                  // Get Text Length
		draw( text, x - ( len / 2.0f ), y - ( getCharHeight() / 2.0f ), z, angleDegX, angleDegY, angleDegZ );  // Draw Text Centered
		return len;                                     // Return Length
	}
	public float drawC(String text, float x, float y, float z, float angleDegZ) {
		return drawC(text, x, y, z, 0, 0, angleDegZ);
	}
	public float drawC(String text, float x, float y, float angleDeg) {
		return drawC(text, x, y, 0, angleDeg);
	}
	public float drawC(String text, float x, float y) {
		float len = getLength( text );                  // Get Text Length
		return drawC(text, x - (len / 2.0f), y - ( getCharHeight() / 2.0f ), 0);
		
	}
	public float drawCX(String text, float x, float y)  {
		float len = getLength( text );                  // Get Text Length
		draw( text, x - ( len / 2.0f ), y );            // Draw Text Centered (X-Axis Only)
		return len;                                     // Return Length
	}
	public void drawCY(String text, float x, float y)  {
		draw( text, x, y - ( getCharHeight() / 2.0f ) );  // Draw Text Centered (Y-Axis Only)
	}

	//--Set Scale--//
	// D: set the scaling to use for the font
	// A: scale - uniform scale for both x and y axis scaling
	//    sx, sy - separate x and y axis scaling factors
	// R: [none]
	public void setScale(float scale)  {
		scaleX = scaleY = scale;                        // Set Uniform Scale
	}
	public void setScale(float sx, float sy)  {
		scaleX = sx;                                    // Set X Scale
		scaleY = sy;                                    // Set Y Scale
	}

	//--Get Scale--//
	// D: get the current scaling used for the font
	// A: [none]
	// R: the x/y scale currently used for scale
	public float getScaleX()  {
		return scaleX;                                  // Return X Scale
	}
	public float getScaleY()  {
		return scaleY;                                  // Return Y Scale
	}

	//--Set Space--//
	// D: set the spacing (unscaled; ie. pixel size) to use for the font
	// A: space - space for x axis spacing
	// R: [none]
	public void setSpace(float space)  {
		spaceX = space;                                 // Set Space
	}

	//--Get Space--//
	// D: get the current spacing used for the font
	// A: [none]
	// R: the x/y space currently used for scale
	public float getSpace()  {
		return spaceX;                                  // Return X Space
	}

	//--Get Length of a String--//
	// D: return the length of the specified string if rendered using current settings
	// A: text - the string to get length for
	// R: the length of the specified string (pixels)
	public float getLength(String text) {
		float len = 0.0f;                               // Working Length
		int strLen = text.length();                     // Get String Length (Characters)
		for ( int i = 0; i < strLen; i++ )  {           // For Each Character in String (Except Last
			int c = (int)text.charAt( i ) - CHAR_START;  // Calculate Character Index (Offset by First Char in Font)
			len += ( charWidths[c] * scaleX );           // Add Scaled Character Width to Total Length
		}
		len += ( strLen > 1 ? ( ( strLen - 1 ) * spaceX ) * scaleX : 0 );  // Add Space Length
		return len;                                     // Return Total Length
	}

	//--Get Width/Height of Character--//
	// D: return the scaled width/height of a character, or max character width
	//    NOTE: since all characters are the same height, no character index is required!
	//    NOTE: excludes spacing!!
	// A: chr - the character to get width for
	// R: the requested character size (scaled)
	public float getCharWidth(char chr)  {
		int c = chr - CHAR_START;                       // Calculate Character Index (Offset by First Char in Font)
		return ( charWidths[c] * scaleX );              // Return Scaled Character Width
	}
	public float getCharWidthMax()  {
		return ( charWidthMax * scaleX );               // Return Scaled Max Character Width
	}
	public float getCharHeight() {
		return ( charHeight * scaleY );                 // Return Scaled Character Height
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
		batch.endBatch();
	}
}
