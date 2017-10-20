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

import com.madinnovations.fatlip.view.di.PerActivity;
import com.madinnovations.fatlip.view.screens.ImportOpponentScreen;
import com.madinnovations.fatlip.view.screens.SetupScreen;

import dagger.Module;
import dagger.Provides;

/**
 * The ScreenModule dependency injection class.
 */
@PerActivity
@Module
public class ScreenModule {
	private ImportOpponentScreen importOpponentScreen;
	private SetupScreen          setupScreen;

	public ScreenModule(ImportOpponentScreen importOpponentScreen) {
		this.importOpponentScreen = importOpponentScreen;
	}
	public ScreenModule(SetupScreen setupScreen) {
		this.setupScreen = setupScreen;
	}

	@Provides @PerActivity
	ImportOpponentScreen provideImportOpponentScreen() {
		return this.importOpponentScreen;
	}
	@Provides @PerActivity
	SetupScreen provideSetupScreen() {
		return this.setupScreen;
	}
}
