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

package com.madinnovations.fatlip.model.framework;

/**
 * Interface for playing music or other long sound files.
 *
 * @author Mark
 *         Created 6/14/2017.
 */
public interface Music {
	void play();

	void stop();

	void pause();

	void setLooping(boolean looping);

	void setVolume(float volume);

	boolean isPlaying();

	boolean isStopped();

	boolean isLooping();

	void dispose();
}
