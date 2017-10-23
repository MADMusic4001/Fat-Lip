/*
  Copyright (C) 2016 MadInnovations
  <p/>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p/>
  http://www.apache.org/licenses/LICENSE-2.0
  <p/>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.madinnovations.fatlip.controller.rxhandlers;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.madinnovations.fatlip.Constants.getImportDir;
import static com.madinnovations.fatlip.Constants.getOpponentsOutputDir;
import static com.madinnovations.fatlip.Constants.getSceneryOutputDir;

/**
 * Creates reactive observable for requesting File operations.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FileRxHandler {
	private static final String TAG = "FileRxHandler";
	private Context context;

	/**
	 * Creates a new FileRxHandler instance
	 */
	@Inject
	public FileRxHandler(Context context) {
		this.context = context;
	}

	/**
	 * Creates an Observable that when subscribed to will read text from the android external files downloads directory for the
	 * Fat Lip app.
	 *
	 * @param fileName  the name of the file to read.
	 * @return an {@link Observable} instance that can be subscribed to in order to read text from a file.
	 */
	public Single<String> readTextFile(final String fileName) {
		return Single.fromCallable(() -> {
			FileInputStream inputStream = null;
			try {
				File dir = getImportDir(context);
				File file = new File(dir, fileName);
				inputStream = new FileInputStream(file);
				int size = inputStream.available();
				byte[] buffer = new byte[size];
				//noinspection ResultOfMethodCallIgnored
				inputStream.read(buffer);
				inputStream.close();
				return new String(buffer, "UTF-8");
			}
			finally {
				if(inputStream != null) {
					try {
						inputStream.close();
					}
					catch (IOException ignored) {}
				}
			}
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Completable writeFile(@NonNull final File file, @NonNull final String data) {
		return Completable.fromCallable(() -> {
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(file);
				outputStream.write(data.getBytes());
				outputStream.close();
				return true;
			}
			catch (Exception e) {
				return false;
			}
			finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					}
					catch (IOException ignored) {
					}
				}
			}
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Completable writeOpponentFile(@NonNull final String fileName, @NonNull final String data) {
		File dir = getOpponentsOutputDir(context);
		File file = new File(dir, fileName);
		return writeFile(file, data);
	}

	public Completable writeSceneryFile(@NonNull final String fileName, @NonNull final String data) {
		File dir = getSceneryOutputDir(context);
		File file = new File(dir, fileName);
		return writeFile(file, data);
	}
}
