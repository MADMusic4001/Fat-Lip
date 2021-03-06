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

package com.madinnovations.fatlip.view.di.modules;

import android.content.Context;
import android.view.View;

import com.madinnovations.fatlip.controller.framework.FileIO;
import com.madinnovations.fatlip.controller.framework.TouchHandler;
import com.madinnovations.fatlip.controller.framework.impl.AndroidFileIO;
import com.madinnovations.fatlip.controller.framework.impl.KeyboardHandler;
import com.madinnovations.fatlip.controller.framework.impl.MultiTouchHandler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 *         Created 6/24/2017.
 */
@Module
public class InputModule {
	@Provides
	@Singleton
	TouchHandler provideTouchHandler(View view, float scaleX, float scaleY) {
		return new MultiTouchHandler(view, scaleX, scaleY);
	}

	@Provides
	@Singleton
	KeyboardHandler provideKeyboardHandler(View view) {
		return new KeyboardHandler(view);
	}

	@Provides
	@Singleton
	FileIO provideFileIO(Context context) {
		return new AndroidFileIO(context);
	}
}
