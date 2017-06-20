package com.madinnovations.fatlip.view;

import android.opengl.GLES20;

/**
 * Class to manage OpenGL objects
 */
public class GLGraphics {
	FatLipSurfaceView glView;
	GLES20 gl;

	public GLGraphics(FatLipSurfaceView glView) {
		this.glView = glView;
	}

	public GLES20 getGL() {
		return gl;
	}

	public void setGL(GLES20 gl) {
		this.gl = gl;
	}

	public int getWidth() {
		return glView.getWidth();
	}

	public int getHeight() {
		return glView.getHeight();
	}
}
