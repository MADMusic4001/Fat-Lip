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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.madinnovations.fatlip.model.Scenery;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Json serializer and deserializer for the {@link Scenery} entities
 */
@SuppressWarnings("WeakerAccess")
@Singleton
public class ScenerySerializer extends TypeAdapter<Scenery> {
	private static final String NAME = "name";
	private static final String FILE_NAME = "fileName";
	private static final String CUSTOM = "custom";

	/**
	 * Creates a new ScenerySerializer instance
	 */
	@Inject
	public ScenerySerializer() {
	}

	@Override
	public void write(JsonWriter out, Scenery value) throws IOException {
		out.beginObject();
		out.name(NAME).value(value.getName());
		out.name(FILE_NAME).value(value.getImageFileName());
		out.name(CUSTOM).value(value.isCustom());
		out.endObject();
	}

	@Override
	public Scenery read(JsonReader in) throws IOException {
		Scenery scenery = new Scenery();

		in.beginObject();
		while(in.hasNext()) {
			switch (in.nextName()) {
				case NAME:
					scenery.setName(in.nextString());
					break;
				case FILE_NAME:
					scenery.setImageFileName(in.nextString());
					break;
				case CUSTOM:
					scenery.setCustom(in.nextBoolean());
					break;
			}
		}
		in.endObject();

		return scenery;
	}
}
