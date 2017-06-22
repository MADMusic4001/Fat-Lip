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

package com.madinnovations.fatlip.controller.framework.impl;

import android.app.Activity;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.madinnovations.fatlip.controller.framework.FileIO;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Input;
import com.madinnovations.fatlip.model.framework.Audio;
import com.madinnovations.fatlip.model.framework.impl.AndroidAudio;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.framework.impl.GLGraphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class GLGame extends Activity implements Game, Renderer {
	private static final String TAG = "GLGame";
	private enum GLGameState {
        Initialized,
        Running,
        Paused,
        Finished,
        Idle
    }
	private GLSurfaceView glView;
	private GLGraphics    glGraphics;
	private Audio         audio;
	private Input         input;
	private FileIO        fileIO;
	private Screen        screen;
	private GLGameState state = GLGameState.Initialized;
	private final Object stateChanged = new Object();
	private long startTime = System.nanoTime();

    @Override 
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate: ");
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        glView = new GLSurfaceView(this);
		glView.setEGLContextClientVersion(2);
        glView.setRenderer(this);
		glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setContentView(glView);

        glGraphics = new GLGraphics(glView);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, glView, 1, 1);
    }
    
    @Override
    public void onResume() {
		Log.d(TAG, "onResume: ");
		super.onResume();
        glView.onResume();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		Log.d(TAG, "onSurfaceCreated: ");
		synchronized(stateChanged) {
            if(state == GLGameState.Initialized) {
				screen = getStartScreen();
			}
            state = GLGameState.Running;
            screen.resume();
            startTime = System.nanoTime();
        }        
    }

	@Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
		Log.d(TAG, "onSurfaceChanged: ");
		if(screen != null) {
			screen.onCreate(width, height, true);
		}
	}

    public void onDrawFrame(GL10 unused) {
		GLGameState state;
        
        synchronized(stateChanged) {
            state = this.state;
        }
        
        if(state == GLGameState.Running) {
            float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
            startTime = System.nanoTime();
            
            screen.update(deltaTime);
            screen.present(deltaTime);
        }
        
        if(state == GLGameState.Paused) {
            screen.pause();            
            synchronized(stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }
        
        if(state == GLGameState.Finished) {
            screen.pause();
            screen.dispose();
            synchronized(stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }            
        }
    }

    @Override 
    public void onPause() {
		Log.d(TAG, "onPause: ");
		synchronized(stateChanged) {
            if(isFinishing())            
                state = GLGameState.Finished;
            else
                state = GLGameState.Paused;
            while(true) {
                try {
                    stateChanged.wait();
                    break;
                }
                catch(InterruptedException ignored) {
                }
            }
        }
        glView.onPause();
        super.onPause();
    }

    @SuppressWarnings("unused")
	public GLGraphics getGLGraphics() {
        return glGraphics;
    }

    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIO;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setScreen(Screen newScreen) {
		Log.d(TAG, "setScreen: ");
		if (newScreen == null) {
			throw new IllegalArgumentException("Screen must not be null");
		}
        this.screen.pause();
        this.screen.dispose();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            getWindow().getWindowManager().getDefaultDisplay().getSize(size);
            newScreen.onCreate(size.x, size.y, true);
        }
        else {
			//noinspection deprecation
			newScreen.onCreate(getWindow().getWindowManager().getDefaultDisplay().getWidth(),
                               getWindow().getWindowManager().getDefaultDisplay().getHeight(),
                               true);
        }
        newScreen.resume();
        newScreen.update(0);
        this.screen = newScreen;
    }

    public Screen getCurrentScreen() {
        return screen;
    }
}