package com.madinnovations.fatlip.view.screens;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Settings;
import com.madinnovations.fatlip.controller.rxhandlers.OpponentRxHandler;
import com.madinnovations.fatlip.model.Assets;
import com.madinnovations.fatlip.model.Opponent;
import com.madinnovations.fatlip.view.FatLipApp;
import com.madinnovations.fatlip.view.activities.FatLipGame;
import com.madinnovations.fatlip.view.activities.FileSelectorDialogFragment;
import com.madinnovations.fatlip.view.activities.GLGame;
import com.madinnovations.fatlip.view.di.components.ScreenComponent;
import com.madinnovations.fatlip.view.di.modules.ScreenModule;
import com.madinnovations.fatlip.view.framework.Screen;
import com.madinnovations.fatlip.view.views.FaceView;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Renders the screen allowing the player to import a custom opponent
 */
@SuppressWarnings("WeakerAccess")
public class ImportOpponentScreen extends Screen implements FileSelectorDialogFragment.FileSelectorDialogListener {
	private static final String TAG = "ImportOpponentScreen";
	private static final String FILE_SELECTOR_FILTER = "fs_extension_filter";
	private static final String BITMAP_FILE_EXTENSION = ".png";
	@Inject
	protected OpponentRxHandler opponentRxHandler;
	private ConstraintLayout    importOpponentScreenLayout;
	private EditText            nameEditText;
	private Button              browseButton;
	private Button              saveButton;
	private Button              backButton;
	private FaceView            faceView;
	private Opponent            opponent;
	private String              sourcePath;
	private boolean foundLeftEye = false;
	private boolean foundRightEye = false;
	private boolean foundLeftMouth = false;
	private boolean foundRightMouth = false;
	private boolean foundBottomMouth = false;

	/**
	 * Creates a new ImportOpponentScreen instance with the given Game
	 *
	 * @param game  a Game instance that the screen can use to access resources
	 */
	@SuppressLint("InflateParams")
	public ImportOpponentScreen(Game game) {
		super(game);
		ScreenComponent screenComponent = ((FatLipApp)((GLGame)game).getApplication()).getApplicationComponent()
				.newScreenComponent(new ScreenModule(this));
		screenComponent.injectInto(this);

		((GLGame)game).runOnUiThread(() -> {
			LinearLayout parentLayout = ((GLGame)game).getParentLayout();
			importOpponentScreenLayout = (ConstraintLayout)((GLGame)game).getLayoutInflater().inflate(
					R.layout.import_opponent_screen,null);
			parentLayout.addView(importOpponentScreenLayout);
			nameEditText = ((GLGame)game).findViewById(R.id.name_edit);
			initNameEdit();
			browseButton = ((GLGame)game).findViewById(R.id.select_file_button);
			initBrowseButton();
			faceView = ((GLGame)game).findViewById(R.id.face_view);
			saveButton = ((GLGame)game).findViewById(R.id.save_button);
			initSaveButton();
			backButton = ((GLGame)game).findViewById(R.id.back_button);
			initBackButton();
			((GLGame)game).getGlView().setVisibility(View.GONE);
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

	@Override
	public void onFileSelected(String fileName) {
		if(fileName != null) {
			loadImage(fileName);
			nameEditText.setText(fileName);
		}
	}

	private void loadImage(String fileName) {
		if(!fileName.endsWith(ImportOpponentScreen.BITMAP_FILE_EXTENSION)) {
			Toast.makeText((GLGame)game, R.string.not_png, Toast.LENGTH_LONG).show();
		}
		else {
			Bitmap bitmap = BitmapFactory.decodeFile(fileName);
			if(bitmap.getWidth() != 512 || bitmap.getHeight() != 512) {
				AlertDialog alertDialog = new AlertDialog.Builder((FatLipGame)game)
						.setTitle(R.string.import_failure)
						.setMessage(R.string.size_error)
						.setPositiveButton(R.string.ok, (dialog, which) -> {})
						.create();
				alertDialog.show();
				return;
			}
			FaceDetector faceDetector = new FaceDetector.Builder((GLGame)game)
					.setTrackingEnabled(false)
					.setLandmarkType(FaceDetector.ALL_LANDMARKS)
					.build();

			Frame frame = new Frame.Builder().setBitmap(bitmap).build();
			SparseArray<Face> faces = faceDetector.detect(frame);

			if(!faceDetector.isOperational()) {
				Log.w(TAG, "Face detector dependencies are not yet available.");

				IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
				boolean hasLowStorage = ((GLGame)game).registerReceiver(null, lowstorageFilter) != null;

				if(hasLowStorage) {
					Toast.makeText((GLGame)game, R.string.low_storage_error, Toast.LENGTH_LONG).show();
					Log.w(TAG, ((GLGame)game).getString(R.string.low_storage_error));
				}
			}

			if(faces.size() == 0) {
				Toast.makeText((GLGame)game, R.string.no_face, Toast.LENGTH_LONG).show();
			}
			else if(faces.size() > 1) {
				Toast.makeText((GLGame)game, R.string.multiple_faces, Toast.LENGTH_LONG).show();
			}
			else {
				Face face = faces.valueAt(0);
				if(face.getWidth() < 300 || face.getHeight() < 300) {
					Log.d(TAG, "face.width = " + face.getWidth());
					Log.d(TAG, "face.height = " + face.getHeight());
					Toast.makeText((GLGame)game, R.string.face_too_small, Toast.LENGTH_LONG).show();
				}
				else {
					opponent = new Opponent();
					opponent.setMouth(new Rect());
					for(Landmark landmark : face.getLandmarks()) {
						switch (landmark.getType()) {
							case Landmark.BOTTOM_MOUTH:
								foundBottomMouth = true;
								opponent.getMouth().bottom = (int)landmark.getPosition().y;
								break;
							case Landmark.LEFT_EYE:
								foundLeftEye = true;
								opponent.setLeftEye(new Rect((int)landmark.getPosition().x - 35,
															 (int)landmark.getPosition().y - 25,
															 (int)landmark.getPosition().x + 35,
															 (int)landmark.getPosition().y + 10));
								break;
							case Landmark.RIGHT_EYE:
								foundRightEye = true;
								opponent.setRightEye(new Rect((int)landmark.getPosition().x - 35,
															  (int)landmark.getPosition().y - 25,
															  (int)landmark.getPosition().x + 35,
															  (int)landmark.getPosition().y + 10));
								break;
							case Landmark.NOSE_BASE:
								opponent.setNose(new Rect((int)landmark.getPosition().x - 35,
														  (int)landmark.getPosition().y - 30,
														  (int)landmark.getPosition().x + 35,
														  (int)landmark.getPosition().y + 10));
								break;
							case Landmark.LEFT_MOUTH:
								foundLeftMouth = true;
								opponent.getMouth().right = (int)landmark.getPosition().x;
								if(opponent.getMouth().top == 0 || landmark.getPosition().y < opponent.getMouth().top) {
									opponent.getMouth().top = (int)landmark.getPosition().y;
								}
								break;
							case Landmark.RIGHT_MOUTH:
								foundRightMouth = true;
								opponent.getMouth().left = (int)landmark.getPosition().x;
								if(opponent.getMouth().top == 0 || landmark.getPosition().y < opponent.getMouth().top) {
									opponent.getMouth().top = (int)landmark.getPosition().y;
								}
								break;
						}
					}
					if(!foundLeftEye || !foundRightEye) {
						Toast.makeText((GLGame)game, R.string.not_two_eyes, Toast.LENGTH_LONG).show();
					}
					else if(!foundLeftMouth || !foundRightMouth || !foundBottomMouth) {
						Toast.makeText((GLGame)game, R.string.no_mouth, Toast.LENGTH_LONG).show();
					}
					else {
						sourcePath = fileName;
						String separator = System.getProperty("file.separator");
						if (fileName.lastIndexOf(separator) == -1) {
							opponent.setImageFileName(fileName);
						}
						else {
							opponent.setImageFileName(fileName.substring(fileName.lastIndexOf(separator)));
						}
						faceView.setContent(bitmap, opponent);
						saveButton.setEnabled(true);
					}
				}
			}
		}
	}

	private void initNameEdit() {
		nameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable editable) {
				if(editable.length() > 0 && opponent != null) {
					opponent.setName(editable.toString());
				}
			}
		});
	}

	private void initBrowseButton() {
		browseButton.setOnClickListener(view -> {
			FileSelectorDialogFragment dialogFragment;
			dialogFragment = new FileSelectorDialogFragment();
			dialogFragment.setListener(ImportOpponentScreen.this);
			Bundle bundle = new Bundle();
			bundle.putString(FILE_SELECTOR_FILTER, BITMAP_FILE_EXTENSION);
			dialogFragment.setArguments(bundle);
			dialogFragment.show(((GLGame)game).getFragmentManager(), "FileSelectorDialog");
		});

	}

	private void initSaveButton() {
		saveButton.setEnabled(false);
		saveButton.setOnClickListener(v -> opponentRxHandler.saveOpponent(opponent, sourcePath)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(() -> Toast.makeText((GLGame)game, R.string.save_success, Toast.LENGTH_LONG).show(),
						   throwable -> {
					Toast.makeText((GLGame)game, R.string.save_error, Toast.LENGTH_LONG).show();
					Log.e(TAG, "An exception occurred saving the opponent", throwable);
				}));
	}

	private void initBackButton() {
		backButton.setOnClickListener(v -> (
				(GLGame)game).runOnUiThread(() -> {
					((GLGame)game).getParentLayout().removeView(importOpponentScreenLayout);
					((GLGame)game).getGlView().setVisibility(View.VISIBLE);
					game.setScreen(new HomeScreen(game), true);
					if(Settings.soundEnabled) {
						Assets.click.play(1);
					}
				})
		);
	}
}
