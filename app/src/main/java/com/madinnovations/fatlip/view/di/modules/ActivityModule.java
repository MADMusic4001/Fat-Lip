/*
  Copyright (C) 2017 MadInnovations
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
package com.madinnovations.fatlip.view.di.modules;

import com.madinnovations.fatlip.view.activities.FatLipGame;
import com.madinnovations.fatlip.view.activities.FileSelectorDialogFragment;
import com.madinnovations.fatlip.view.di.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * The ActivityModule dependency injection class.
 */
@PerActivity
@Module
public class ActivityModule {
	private FatLipGame fatLipGame;
	private FileSelectorDialogFragment fileSelectorDialogFragment;

	public ActivityModule(FatLipGame fatLipGame) {
		this.fatLipGame = fatLipGame;
	}
	public ActivityModule(FileSelectorDialogFragment fileSelectorDialogFragment) {
		this.fileSelectorDialogFragment = fileSelectorDialogFragment;
	}

	@Provides @PerActivity
	FatLipGame provideFatLipGame() {
		return this.fatLipGame;
	}
	@Provides @PerActivity
	FileSelectorDialogFragment provideFileSelectorDialogFragment() {
		return this.fileSelectorDialogFragment;
	}
}
