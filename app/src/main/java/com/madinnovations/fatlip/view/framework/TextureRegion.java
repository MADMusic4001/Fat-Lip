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

@SuppressWarnings("WeakerAccess")
public class TextureRegion {
   public float u1, v1;
   public float u2, v2;

   /**
    * Creates a new TextureRegion instance
    *
    * @param texWidth  the width of the texture the region is for
    * @param texHeight  the height of the texture the region is for
    * @param x  the left (x) coordinate of the region on the texture (in pixels)
    * @param y  the top (y) coordinate of the region on the texture (in pixels)
    * @param width  the width of the region on the texture (in pixels)
    * @param height  the height of the region on the texture (in pixels)
    */
   public TextureRegion(float texWidth, float texHeight, float x, float y, float width, float height)  {
      this.u1 = x / texWidth;
      this.v1 = y / texHeight;
      this.u2 = this.u1 + ( width / texWidth );
      this.v2 = this.v1 + ( height / texHeight );
   }
}
