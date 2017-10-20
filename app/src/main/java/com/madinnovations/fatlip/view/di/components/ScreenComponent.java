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

package com.madinnovations.fatlip.view.di.components;

import com.madinnovations.fatlip.view.di.PerActivity;
import com.madinnovations.fatlip.view.di.modules.ScreenModule;
import com.madinnovations.fatlip.view.screens.HomeScreen;
import com.madinnovations.fatlip.view.screens.ImportOpponentScreen;
import com.madinnovations.fatlip.view.screens.LoadingScreen;
import com.madinnovations.fatlip.view.screens.SetupScreen;

import dagger.Subcomponent;

/**
 * The ScreenComponent dependency injection interface
 */
@PerActivity
@Subcomponent(modules = {ScreenModule.class})
public interface ScreenComponent {
	void injectInto(HomeScreen homeScreen);
	void injectInto(ImportOpponentScreen importOpponentScreen);
	void injectInto(LoadingScreen loadingScreen);
	void injectInto(SetupScreen setupScreen);
}
