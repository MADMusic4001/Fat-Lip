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

package com.madinnovations.fatlip.model.serializers;

import android.graphics.Rect;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.madinnovations.fatlip.model.Opponent;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Json serializer and deserializer for the {@link Opponent} entities
 */
@SuppressWarnings("WeakerAccess")
@Singleton
public class OpponentSerializer extends TypeAdapter<Opponent> {
	private static final String NAME = "name";
	private static final String LEFT_EYE = "leftEye";
	private static final String RIGHT_EYE = "rightEye";
	private static final String NOSE = "nose";
	private static final String MOUTH = "mouth";
	private static final String FILE_NAME = "fileName";
	private static final String CUSTOM = "custom";

	/**
	 * Creates a new OpponentSerializer instance
	 */
	@Inject
	public OpponentSerializer() {
	}

	@Override
	public void write(JsonWriter out, Opponent value) throws IOException {
		out.beginObject();
		out.name(NAME).value(value.getName());
		out.name(LEFT_EYE);
		writeRect(out, value.getLeftEye());
		out.name(RIGHT_EYE);
		writeRect(out, value.getRightEye());
		out.name(NOSE);
		writeRect(out, value.getNose());
		out.name(MOUTH);
		writeRect(out, value.getMouth());
		out.name(FILE_NAME).value(value.getImageFileName());
		out.name(CUSTOM).value(value.isCustom());
		out.endObject();
	}

	@Override
	public Opponent read(JsonReader in) throws IOException {
		Opponent opponent = new Opponent();
		Rect aRect;

		in.beginObject();
		while(in.hasNext()) {
			switch (in.nextName()) {
				case NAME:
					opponent.setName(in.nextString());
					break;
				case LEFT_EYE:
					aRect = readRect(in);
					opponent.setLeftEye(aRect);
					break;
				case RIGHT_EYE:
					aRect = readRect(in);
					opponent.setRightEye(aRect);
					break;
				case NOSE:
					aRect = readRect(in);
					opponent.setNose(aRect);
					break;
				case MOUTH:
					aRect = readRect(in);
					opponent.setMouth(aRect);
					break;
				case FILE_NAME:
					opponent.setImageFileName(in.nextString());
					break;
				case CUSTOM:
					opponent.setCustom(in.nextBoolean());
					break;
			}
		}
		in.endObject();

		return opponent;
	}

	private void writeRect(JsonWriter out, Rect aRect) throws IOException {
		out.beginArray();
		out.value(aRect.left);
		out.value(aRect.top);
		out.value(aRect.right);
		out.value(aRect.bottom);
		out.endArray();
	}

	private Rect readRect(JsonReader in) throws IOException {
		Rect aRect = new Rect();

		in.beginArray();
		aRect.left = in.nextInt();
		aRect.top = in.nextInt();
		aRect.right = in.nextInt();
		aRect.bottom = in.nextInt();
		in.endArray();

		return aRect;
	}
}
