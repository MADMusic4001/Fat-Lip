/**
 * Copyright (C) 2016 MadInnovations
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.madinnovations.fatlip.view.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of the ResourceUtils interface for getting items from the Android resource storage.
 */
@Singleton
public class AndroidResourceUtils implements ResourceUtils {
	private Context context;

	/**
	 * Creates a new AndroidResourceUtils instance.
	 *
	 * @param context  an Android Context instance.
	 */
	@Inject
	public AndroidResourceUtils(Context context) {
		this.context = context;
	}

	@Override
	public String getString(@NonNull Object id) {
		return context.getString((Integer)id);
	}

	@Override
	public Bitmap getBitmap(@NonNull Object id) {
		return BitmapFactory.decodeResource(context.getResources(), (Integer)id);
	}
}
