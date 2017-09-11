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

package com.madinnovations.fatlip.model.framework.impl;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.madinnovations.fatlip.model.framework.Audio;
import com.madinnovations.fatlip.model.framework.Music;
import com.madinnovations.fatlip.model.framework.Sound;

import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class AndroidAudio implements Audio {
	private static final String TAG = "AndroidAudio";
	private AssetManager assets;
    private SoundPool    soundPool;

    public AndroidAudio(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.assets = activity.getAssets();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			AudioAttributes audioAttributes = new AudioAttributes.Builder()
					.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
					.build();
			this.soundPool = new SoundPool.Builder()
					.setMaxStreams(20)
					.setAudioAttributes(audioAttributes)
					.build();
		}
		else {
			//noinspection deprecation
			this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		}
    }

    public Music newMusic(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = assets.openFd(filename);
            return new AndroidMusic(assetDescriptor);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load music '" + filename + "'");
        }
    }
    
    public Sound newSound(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = assets.openFd(filename);
            int soundId = soundPool.load(assetDescriptor, 0);
            return new AndroidSound(soundPool, soundId);
        } catch (IOException e) {
			Log.e(TAG, "newSound: ", e);
			throw new RuntimeException("Couldn't load sound '" + filename + "'");
        }
    }
}