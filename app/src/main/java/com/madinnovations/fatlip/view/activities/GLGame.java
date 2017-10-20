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

package com.madinnovations.fatlip.view.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.wallet.Wallet;
import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.controller.framework.FileIO;
import com.madinnovations.fatlip.controller.framework.Game;
import com.madinnovations.fatlip.controller.framework.Input;
import com.madinnovations.fatlip.controller.framework.impl.AndroidFileIO;
import com.madinnovations.fatlip.controller.framework.impl.AndroidInput;
import com.madinnovations.fatlip.model.framework.Audio;
import com.madinnovations.fatlip.model.framework.impl.AndroidAudio;
import com.madinnovations.fatlip.view.framework.Screen;

import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@SuppressWarnings("unused")
public abstract class GLGame extends Activity implements Game, Renderer, GoogleApiClient.OnConnectionFailedListener,
		GoogleApiClient.ConnectionCallbacks {
	private static final String TAG = "GLGame";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	private static final String DIALOG_ERROR = "dialog_error";
	private static final String STATE_RESOLVING_ERROR = "resolving_error";
	private static final int RC_SIGN_IN = 1;
	private enum GLGameState {
        Initialized,
        Running,
        Paused,
        Finished,
        Idle
    }
    private GoogleApiClient googleApiClient = null;
	private BillingClient billingClient;
	private boolean resolvingError = false;
	private GLSurfaceView glView;
	private LinearLayout  parentLayout;
	private Audio         audio;
	private Input         input;
	private FileIO        fileIO;
	private Screen        screen;
	private Stack<Screen> previousScreens = new Stack<>();
	private GLGameState state = GLGameState.Initialized;
	private final Object stateChanged = new Object();
	private long startTime = System.nanoTime();

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.opengl_layout);
		parentLayout = findViewById(R.id.parent_layout);
        glView = findViewById(R.id.surface_view);
		glView.setEGLContextClientVersion(2);
        glView.setRenderer(this);
		glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, glView, 1, 1);
    }

	@Override
	protected void onStart() {
		super.onStart();
		GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
		int result = instance.isGooglePlayServicesAvailable(this);
		if(result != ConnectionResult.SUCCESS) {
			Dialog dialog = instance.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST);
			dialog.show();
		}
		else {
			GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestProfile()
					.build();
			GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(Games.API)
					.addApi(Auth.GOOGLE_SIGN_IN_API, options)
					.addApi(Wallet.API)
					.addScope(Games.SCOPE_GAMES)
					.build();
			Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
			startActivityForResult(signInIntent, RC_SIGN_IN);
			googleApiClient.connect();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(googleApiClient != null) {
			googleApiClient.disconnect();
			googleApiClient = null;
		}
	}

	@Override
    public void onResume() {
		super.onResume();
        glView.onResume();
    }

	@Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
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
		if(screen != null) {
			screen.onCreate(width, height);
		}
	}

	@Override
    public void onDrawFrame(GL10 unused) {
		GLGameState state;

        synchronized(stateChanged) {
            state = this.state;
        }

        if(state == GLGameState.Running) {
        	long currentTime = System.nanoTime();
            float deltaTime = (currentTime-startTime) / 1000000000.0f;
            startTime = currentTime;
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
		synchronized(stateChanged) {
            if(isFinishing()) {
				state = GLGameState.Finished;
			}
            else {
				state = GLGameState.Paused;
			}
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_RESOLVING_ERROR, resolvingError);
	}

	@Override
	public void onBackPressed() {
    	if(previousScreens.size() == 0) {
			super.onBackPressed();
		}
		else {
    		setScreen(previousScreens.pop(), false);
		}
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    	if(resolvingError) {
			Log.d(TAG, "onConnectionFailed: Already resolving error");
			return;
		}
    	if(!connectionResult.hasResolution()) {
			showErrorDialog(connectionResult.getErrorCode());
			resolvingError = true;
    		return;
		}
		try {
			resolvingError = true;
			connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
		}
		catch (IntentSender.SendIntentException e) {
			Log.e(TAG, "Exception caught starting resolution activity", e);
			googleApiClient.connect();
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	resolvingError = false;
	}

	private void initBilling() {
		billingClient = BillingClient.newBuilder(this).build();
		billingClient.startConnection(new BillingClientStateListener() {
			@Override
			public void onBillingSetupFinished(int responseCode) {
				if(responseCode == BillingClient.BillingResponse.OK) {

				}
			}

			@Override
			public void onBillingServiceDisconnected() {
			}
		});
	}

	private void onDialogDismissed() {
    	resolvingError = false;
	}

	private void showErrorDialog(int errorCode) {
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(getFragmentManager(), "errordialog");
	}

	// Getters and setters
	public Input getInput() {
        return input;
    }
    public FileIO getFileIO() {
        return fileIO;
    }
    public Audio getAudio() {
        return audio;
    }
    public void setScreen(Screen newScreen, boolean addToStack) {
		if (newScreen == null) {
			throw new IllegalArgumentException("Screen must not be null");
		}
        screen.pause();
        screen.dispose();
		Point size = new Point();
		getWindow().getWindowManager().getDefaultDisplay().getSize(size);
		newScreen.onCreate(size.x, size.y);
        newScreen.resume();
        newScreen.update(0);
        if(addToStack) {
        	previousScreens.push(screen);
		}
        screen = newScreen;
    }
    public Screen getCurrentScreen() {
        return screen;
    }
	public GLSurfaceView getGlView() {
		return glView;
	}
	public LinearLayout getParentLayout() {
		return parentLayout;
	}

	public static class ErrorDialogFragment extends DialogFragment {
		public ErrorDialogFragment() { }

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GoogleApiAvailability.getInstance().getErrorDialog(
					this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			((GLGame)getActivity()).onDialogDismissed();
		}
	}
}