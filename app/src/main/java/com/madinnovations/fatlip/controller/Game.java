package com.madinnovations.fatlip.controller;

import com.madinnovations.fatlip.view.Screen;

/**
 * The game interface
 */
public interface Game {
	void setScreen(Screen screen);

	Screen getCurrentScreen();

	Screen getStartScreen();
}
