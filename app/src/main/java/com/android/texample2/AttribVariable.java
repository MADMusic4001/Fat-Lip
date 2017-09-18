package com.android.texample2;

/**
 * An enumeration of the attribute variables used by an OpenGL program.
 */
public enum AttribVariable {
	A_Position(1, "a_Position"), 
	A_TexCoordinate(2, "a_TexCoordinate"), 
	A_MVPMatrixIndex(3, "a_MVPMatrixIndex");

	private int mHandle;
	private String mName;

	AttribVariable(int handle, String name) {
		mHandle = handle;
		mName = name;
	}

	// Getters
	public int getHandle() {
		return mHandle;
	}
	public String getName() {
		return mName;
	}
}
