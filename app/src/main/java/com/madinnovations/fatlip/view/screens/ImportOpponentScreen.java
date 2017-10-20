package com.madinnovations.fatlip.view.screens;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.activities.FileSelectorDialogFragment;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.di.components.ScreenComponent;
import com.madinnovations.fatlip.view.di.modules.ScreenModule;
import com.madinnovations.fatlip.view.framework.Screen;

/**
 * Renders the screen allowing the player to import a custom opponent
 */
public class ImportOpponentScreen extends Screen {
	private static final String FILE_SELECTOR_FILTER = "fs_extension_filter";
	public static final String BITMAP_FILE_EXTENSION = ".png";
	private ConstraintLayout importOpponentScreenLayout;
	private EditText filePathEditText;
	private Button browseButton;
	private ImageView opponentImageView;

	/**
	 * Creates a new ImportOpponentScreen instance with the given Game
	 *
	 * @param game  a Game instance that the screen can use to access resources
	 */
	public ImportOpponentScreen(Game game) {
		super(game);
		ScreenComponent screenComponent = ((FatLipApp)((GLGame)game).getApplication()).getApplicationComponent()
				.newScreenComponent(new ScreenModule(this));
		screenComponent.injectInto(this);

		((GLGame)game).runOnUiThread(new Runnable() {
			@SuppressLint("InflateParams")
			@Override
			public void run() {
				LinearLayout parentLayout = ((GLGame)game).getParentLayout();
				importOpponentScreenLayout = (ConstraintLayout)((GLGame)game).getLayoutInflater().inflate(R.layout.import_opponent_screen,
						null);
				parentLayout.addView(importOpponentScreenLayout);
				filePathEditText = ((GLGame)game).findViewById(R.id.select_file_edit);
				initFilePathEdit();
				browseButton = ((GLGame)game).findViewById(R.id.select_file_button);
				initBrowseButton();
				opponentImageView = ((GLGame)game).findViewById(R.id.opponent_imageview);
				initOpponentImageView();
				((GLGame)game).getGlView().setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onCreate(int width, int height) {

	}

	@Override
	public void update(float deltaTime) {

	}

	@Override
	public void present(float deltaTime) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	private void initFilePathEdit() {

	}

	private void initBrowseButton() {
		browseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogFragment dialogFragment;
				dialogFragment = new FileSelectorDialogFragment();
				Bundle bundle = new Bundle();
				bundle.putString(FILE_SELECTOR_FILTER, BITMAP_FILE_EXTENSION);
				dialogFragment.setArguments(bundle);
				dialogFragment.show(((GLGame)game).getFragmentManager(), "FileSelectorDialog");
			}
		});

	}

	private void initOpponentImageView() {

	}
}
