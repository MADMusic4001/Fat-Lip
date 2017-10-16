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
package com.madinnovations.fatlip.view.framework;

import android.opengl.GLSurfaceView;

import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.view.activities.GLGame;

import javax.microedition.khronos.opengles.GL10;

/**
 * Base class for Screen implementations
 */
public abstract class Screen {
	protected final Game game;

	public Screen(Game game) {
		this.game = game;
	}

	/**
	 * Method to allow a screen to do one time initialization of resources it will need for rendering. The method will be
	 * called in response to {{@link GLSurfaceView.Renderer#onSurfaceChanged(GL10, int, int)}} and when the app requests a
	 * screen to be displayed by calling {@link GLGame#setScreen(Screen, boolean)}.
	 *
	 * @param width  the pixel width of the display
	 * @param height  the pixel height of the display
	 */
	public abstract void onCreate(int width, int height);

	/**
	 * Method to allow the screen to update it's model prior to rendering.
	 *
	 * @param deltaTime  the elapsed time since the last call to this method. Will be 0 the first time the method is called
	 */
	public abstract void update(float deltaTime);

	/**
	 * Method to allow the screen to draw itself.
	 *
	 * @param deltaTime  the elapsed time since the last call to this method
	 */
	public abstract void present(float deltaTime);

	/**
	 * Notifies the screen that it is being paused. This can occur when the {@link GLGame.GLGameState} changes to PAUSED or
	 * FINISHED or when the screen is about to be replaced by a different screen.
	 */
	public abstract void pause();

	/**
	 * Notifies the screen that it is about to be displayed.
	 */
	public abstract void resume();

	/**
	 * Notifies the screen that it will no longer be rendered and should release any resources it may have created.
	 */
	public abstract void dispose();
}
