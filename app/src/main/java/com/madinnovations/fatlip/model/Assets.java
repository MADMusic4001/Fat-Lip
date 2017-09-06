package com.madinnovations.fatlip.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.madinnovations.fatlip.controller.framework.impl.GLGame;
import com.madinnovations.fatlip.model.framework.Sound;

import java.io.IOException;

/**
 * Manages the audio and graphical assets for the game.
 */
public class Assets {
	private static final String TAG = "Assets";
	public static Bitmap splashScreenBitmap;
	public static Sound  click;

    public static void loadSplashScreen(GLGame game) {
		try {
			Assets.splashScreenBitmap = BitmapFactory.decodeStream(game.getFileIO().readAsset("fatlip.png"));
		}
		catch (IOException e) {
			Log.e(TAG, "load: Exception caught loading splash screen", e);
		}
    }

    public static void reload() {
    }

    public static void playSound(Sound sound) {
    }
}
