/**
 * Copyright (C) 2015 MadMusic4001
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.madinnovations.fatlip.view.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.madinnovations.fatlip.R;
import com.madinnovations.fatlip.view.utils.FileInfo;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 * Created 7/25/2015.
 */
public class FileSelectorAdapter extends ArrayAdapter<FileInfo> {
	private static final int LAYOUT_RESOURCE_ID = R.layout.file_selector_directory;

	private LayoutInflater layoutInflater;
	private int[] colors = new int[]{
			R.color.list_even_row_background,
			R.color.list_odd_row_background};

	public FileSelectorAdapter(Context context) {
		super(context, LAYOUT_RESOURCE_ID);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView;
		ViewHolder holder;

		if (convertView == null) {
			rowView = layoutInflater.inflate(LAYOUT_RESOURCE_ID, parent, false);
			holder = new ViewHolder((ImageView)rowView.findViewById(R.id.fs_image_view),
					(CheckedTextView) rowView.findViewById(R.id.fs_checked_textview));
			rowView.setTag(holder);
		}
		else {
			rowView = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		rowView.setBackgroundColor(ContextCompat.getColor(getContext(), colors[position % colors.length]));
		FileInfo fileInfo = getItem(position);
		if(fileInfo.isDirectory()) {
			holder.fileImageView.setImageDrawable(ResourcesCompat.getDrawable(
					getContext().getResources(), R.drawable.folder, null));
		}
		else {
			holder.fileImageView.setImageDrawable(ResourcesCompat.getDrawable(
					getContext().getResources(), R.drawable.file_icon, null));
		}
		holder.fileNameView.setText(fileInfo.getFileName());
		holder.fileNameView.setChecked(fileInfo.isSelected());
		return rowView;
	}

	private class ViewHolder {
		ImageView       fileImageView;
		CheckedTextView fileNameView;

		public ViewHolder(ImageView fileImageView, CheckedTextView fileNameView) {
			this.fileImageView = fileImageView;
			this.fileNameView = fileNameView;
		}
	}
}
