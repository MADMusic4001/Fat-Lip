package com.android.texample2.programs;

import android.opengl.GLES20;

import com.android.texample2.AttribVariable;
import com.android.texample2.Utilities;


@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Program {
    private int programHandle;
    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private boolean mInitialized;

	/**
	 * Creates a new Program instance
	 */
	public Program() {
        mInitialized = false;
    }

	/**
	 * initializes the program with default shaders and variables
	 */
	public void init() {
        init(null, null, null);
    }

	/**
	 * Initializes the program with the given shaders and variables.
	 *
	 * @param vertexShaderCode  the vertex shader code
	 * @param fragmentShaderCode  the fragment shader code
	 * @param programVariables  an array of attribute variables
	 */
    public void init(String vertexShaderCode, String fragmentShaderCode, AttribVariable[] programVariables) {
        vertexShaderHandle = Utilities.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        fragmentShaderHandle = Utilities.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        programHandle = Utilities.createProgram(
                vertexShaderHandle, fragmentShaderHandle, programVariables);

        mInitialized = true;
    }

	/**
	 * Deletes the program and it's shaders
	 */
	public void delete() {
        GLES20.glDeleteShader(vertexShaderHandle);
        GLES20.glDeleteShader(fragmentShaderHandle);
        GLES20.glDeleteProgram(programHandle);
        mInitialized = false;
    }

    // Getters
	public int getHandle() {
		return programHandle;
	}
    public boolean initialized() {
        return mInitialized;
    }
}