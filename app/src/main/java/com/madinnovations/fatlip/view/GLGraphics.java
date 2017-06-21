package com.madinnovations.fatlip.view;

import android.opengl.GLES20;

/**
 * Class to manage OpenGL objects
 */
public class GLGraphics {
	FatLipSurfaceView glView;

	public GLGraphics(FatLipSurfaceView glView) {
		this.glView = glView;
	}

	public int getWidth() {
		return glView.getWidth();
	}

	public int getHeight() {
		return glView.getHeight();
	}
}
