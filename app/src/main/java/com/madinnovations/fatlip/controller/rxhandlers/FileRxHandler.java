/**
 * Copyright (C) 2016 MadInnovations
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.madinnovations.fatlip.controller.rxhandlers;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Creates reactive observable for requesting File operations.
 */
public class FileRxHandler {
	private Context context;

	/**
	 * Creates a new FileRxHandler instance
	 */
	@Inject
	public FileRxHandler(Context context) {
		this.context = context;
	}

	/**
	 * Creates an Observable that when subscribed to will read text from the android external files documents directory for the RMU app.
	 *
	 * @param fileName  the name of the file to read.
	 * @return an {@link Observable} instance that can be subscribed to in order to read text from a file.
	 */
	public Observable<String> readFile(final String fileName) {
		return Observable.create(
			new Observable.OnSubscribe<String>() {
				@Override
				public void call(Subscriber<? super String> subscriber) {
					FileInputStream inputStream = null;
					try {
						File dir = getImportExportDir();
						File file = new File(dir, fileName);
						inputStream = new FileInputStream(file);
						int size = inputStream.available();
						byte[] buffer = new byte[size];
						//noinspection ResultOfMethodCallIgnored
						inputStream.read(buffer);
						inputStream.close();
						subscriber.onNext(new String(buffer, "UTF-8"));
						subscriber.onCompleted();
					}
					catch (Exception e) {
						subscriber.onError(e);
					}
					finally {
						if(inputStream != null) {
							try {
								inputStream.close();
							}
							catch (IOException ignored) {}
						}
					}
				}
			}
		).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Observable<Boolean> writeFile(@NonNull final File file, @NonNull final String data) {
		return Observable.create(
			new Observable.OnSubscribe<Boolean>() {
				@Override
				public void call(Subscriber<? super Boolean> subscriber) {
					FileOutputStream outputStream = null;
					try {
						outputStream = new FileOutputStream(file);
						outputStream.write(data.getBytes());
						outputStream.close();
						subscriber.onNext(true);
						subscriber.onCompleted();
					}
					catch (Exception e) {
						subscriber.onError(e);
					}
					finally {
						if(outputStream != null) {
							try {
								outputStream.close();
							}
							catch (IOException ignored) {}
						}
					}
				}
			}
		).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Observable<Boolean> writeFile(@NonNull final String fileName, @NonNull final String data) {
		File dir = getImportExportDir();
		File file = new File(dir, fileName);
		return writeFile(file, data);
	}

	/**
	 * Gets the RMU import/export directory to use. ExternalFiles/Documents/RMU for KITKAT and above.
	 * ExternalFiles/Downloads/RMU for below KitKat.
	 *
	 * @return the File representing the directory or n
	 */
	public File getImportExportDir() {
		File dir;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RMU");
		}
		else {
			dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "RMU");
		}
		if(!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e("RMU", "Directory not created");
				throw new RuntimeException("Error creating output directory");
			}
		}
		return dir;
	}
}
