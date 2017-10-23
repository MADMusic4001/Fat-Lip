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

package com.madinnovations.fatlip.view.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.madinnovations.fatlip.model.Opponent;

/**
 * Custom view to display opponent faces with landmarks highlighted
 */
@SuppressWarnings("unused")
public class FaceView extends View {
	private static final String TAG = "FaceView";
	private Bitmap   bitmap;
	private Opponent opponent;

	public FaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Sets the bitmap background and the associated face detections.
	 */
	public void setContent(Bitmap bitmap, Opponent opponent) {
		this.bitmap = bitmap;
		this.opponent = opponent;
		invalidate();
	}

	/**
	 * Draws the bitmap background and the associated face landmarks.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if ((bitmap != null) && (opponent != null)) {
			double scale = drawBitmap(canvas);
			drawFaceAnnotations(canvas, scale);
		}
	}

	/**
	 * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
	 * positioning the facial landmark graphics.
	 */
	private double drawBitmap(Canvas canvas) {
		double viewWidth = canvas.getWidth();
		double viewHeight = canvas.getHeight();
		double imageWidth = bitmap.getWidth();
		double imageHeight = bitmap.getHeight();
		double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

		Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
		canvas.drawBitmap(bitmap, null, destBounds, null);
		return scale;
	}

	/**
	 * Draws a small circle for each detected landmark, centered at the detected landmark position.
	 * <p>
	 *
	 * Note that eye landmarks are defined to be the midpoint between the detected eye corner
	 * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
	 * pupil position.
	 */
	private void drawFaceAnnotations(Canvas canvas, double scale) {
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);

		Rect drawRect = opponent.getLeftEye();
		drawRect.left *= scale;
		drawRect.right *= scale;
		drawRect.top *= scale;
		drawRect.bottom *= scale;
		canvas.drawRect(drawRect, paint);

		drawRect = opponent.getRightEye();
		drawRect.left *= scale;
		drawRect.right *= scale;
		drawRect.top *= scale;
		drawRect.bottom *= scale;
		canvas.drawRect(drawRect, paint);

		drawRect = opponent.getMouth();
		drawRect.left *= scale;
		drawRect.right *= scale;
		drawRect.top *= scale;
		drawRect.bottom *= scale;
		canvas.drawRect(drawRect, paint);

		if(opponent.getNose() != null) {
			drawRect = opponent.getNose();
			drawRect.left *= scale;
			drawRect.right *= scale;
			drawRect.top *= scale;
			drawRect.bottom *= scale;
			canvas.drawRect(drawRect, paint);
		}
	}
}
