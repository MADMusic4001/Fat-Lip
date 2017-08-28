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

import android.graphics.Point;

import com.madinnovations.fatlip.model.framework.Rect;
import com.madinnovations.fatlip.model.framework.Size;
import com.madinnovations.fatlip.model.framework.TextureAtlas;

import java.util.Collection;

/**
 * TextureAtlas implementation that reads the sprite layout information from a JSON file.
 */
public class TextureAtlasJsonImpl implements TextureAtlas {
	private Collection<Frame> frames;
	private Meta meta;

	@Override
	public void load(String fileName) {

	}
}
