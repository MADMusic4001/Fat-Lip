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
package com.madinnovations.fatlip.view;

import android.app.Application;
import android.content.ClipboardManager;

import com.madinnovations.fatlip.view.di.components.ApplicationComponent;
import com.madinnovations.fatlip.view.di.components.DaggerApplicationComponent;
import com.madinnovations.fatlip.view.di.modules.ApplicationModule;
import com.madinnovations.fatlip.view.utils.AndroidResourceUtils;
import com.madinnovations.fatlip.view.utils.ResourceUtils;

/**
 * The Android Application class
 */
public class FatLipApp extends Application {
	private ApplicationComponent applicationComponent;
	private static ResourceUtils resourceUtils;



















	private static ClipboardManager clipboardManager;

	@Override
	public void onCreate() {
		super.onCreate();
		this.initializeInjector();
		resourceUtils = new AndroidResourceUtils(this);
		clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
	}

	private void initializeInjector() {
		this.applicationComponent = DaggerApplicationComponent.builder()
				.applicationModule(new ApplicationModule(this))
				.build();
	}

	public ApplicationComponent getApplicationComponent() {
		return this.applicationComponent;
	}

	/**
	 * Gets the ResourceUtils instance.
	 *
	 * @return  the ResourceUtils instance.
	 */
	public static ResourceUtils getResourceUtils() {
		return resourceUtils;
	}

	public static ClipboardManager getClipboardManager() { return clipboardManager; }
}
