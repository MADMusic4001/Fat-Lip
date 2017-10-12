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

package com.madinnovations.fatlip.view.framework;


public enum AttribVariable {
	A_Position(1, "a_Position"), 
	A_TexCoordinate(2, "a_TexCoordinate"), 
	A_MVPMatrixIndex(3, "a_MVPMatrixIndex");
	
	private int mHandle;
	private String mName;

	private AttribVariable(int handle, String name) {
		mHandle = handle;
		mName = name;
	}

	public int getHandle() {
		return mHandle;
	}
	
	public String getName() {
		return mName;
	}
}
