package com.madinnovations.fatlip.view.utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Utility methods for retrieving resource items.
 */
public interface ResourceUtils {

	/**
	 * Gets a resource string for a given id
	 *
	 * @param id  the id of the resource string
	 * @return  the String for the given id or null if not found.
	 */
	public abstract String getString(@NonNull Object id);

	/**
	 * Gets the bitmap with the given id.
	 *
	 * @param id  the id of the Bitmap to retrieve
	 * @return  the Bitmap for the given id or null if not found.
	 */
	public abstract Bitmap getBitmap(@NonNull Object id);
}
