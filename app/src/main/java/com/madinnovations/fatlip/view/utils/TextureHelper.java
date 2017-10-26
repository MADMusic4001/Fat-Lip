/*
 Excerpted from "OpenGL ES for Android",
 published by The Pragmatic Bookshelf.
 Copyrights apply to this code. It may not be used to create training material,
 courses, books, articles, and the like. Contact us if you are in doubt.
 We make no guarantees that this code is fit for any purpose.
 Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.madinnovations.fatlip.view.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureHelper {
    private static final String TAG = "TextureHelper";
    private static int maxTextureSize = 0;

    /**
     * Loads a texture from a resource ID, returning the OpenGL ID for that
     * texture. Returns 0 if the load failed.
     * 
     * @param context  an Android context to use to access the android resources
     * @param resourceId  the id of a drawable resource
     * @return  the OpenGL id for the texture or 0 on failure
     */
    public static int loadTexture(Context context, @DrawableRes int resourceId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            }
            return 0;
        }

		int textureId = loadTexture(bitmap);
        bitmap.recycle();

        return textureId;
    }

    /**
     * Loads a bitmap into OpenGL to use as a texture.
     *
	 * @param bitmap  the bitmap to use as the texture
     * @return  the OpenGL id for the texture or 0 on failure
     */
    public static int loadTexture(Bitmap bitmap) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object. errorCode = " + glGetError());
            }
			Log.w(TAG, "Could not generate a new OpenGL texture object. errorCode = " + glGetError());

            return 0;
        }

        // Bind to the texture in OpenGL
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // Set filtering: a default must be set, or the texture will be black.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Load the bitmap into the bound texture.
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.

        glGenerateMipmap(GL_TEXTURE_2D);

        // Unbind from the texture.
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

    // Getters and setters
    public static int getMaxTextureSize() {
        return maxTextureSize;
    }
    public static void setMaxTextureSize(int maxTextureSize) {
        TextureHelper.maxTextureSize = maxTextureSize;
    }
}
