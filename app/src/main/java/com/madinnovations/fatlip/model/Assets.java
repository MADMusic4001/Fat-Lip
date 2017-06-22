package com.madinnovations.fatlip.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.madinnovations.fatlip.controller.framework.impl.GLGame;
import com.madinnovations.fatlip.model.framework.Sound;

import java.io.IOException;

public class Assets {
	private static final String TAG = "Assets";
	public static Bitmap homeScreenBitmap;

    public static void load(GLGame game) {
		try {
			homeScreenBitmap = BitmapFactory.decodeStream(game.getFileIO().readAsset("fatlip.png"));
		}
		catch (IOException e) {
			Log.e(TAG, "load: Exception caught loading assets", e);
		}
    }

    public static void reload() {
    }

    public static void playSound(Sound sound) {
    }
}
