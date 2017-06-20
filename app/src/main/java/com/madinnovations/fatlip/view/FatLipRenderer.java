package com.madinnovations.fatlip.view;

import com.madinnovations.fatlip.controller.Game;

/**
 * Created by madanle on 6/20/17.
 */
public class FatLipRenderer extends GLRenderer {
	Game game;

	public FatLipRenderer(Game game) {
		this.game = game;
	}

	@Override
	public void onCreate(int width, int height, boolean contextLost) {

	}

	@Override
	public void onDrawFrame(boolean firstDraw) {
		game.getCurrentScreen().present(System.nanoTime());
	}
}
