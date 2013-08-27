package com.wdonahue.rapidparsing.adapters;

import java.util.List;

import com.squareup.picasso.Picasso;
import com.wdonahue.rapidparsing.R;
import com.wdonahue.rapidparsing.model.JustinTvStreamData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JustinTvStreamAdapter extends ArrayAdapter<JustinTvStreamData> {
	private LayoutInflater mInflater;

	public JustinTvStreamAdapter(Context context, int textViewResourceId,
			List<JustinTvStreamData> objects) {
		super(context, textViewResourceId, objects);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Holder holder = null;

		if (view == null) {
			// View doesn't exist so create it and create the holder
			view = mInflater.inflate(R.layout.grid_item, parent, false);

			holder = new Holder();
			holder.thumbnailImage = (ImageView) view
					.findViewById(R.id.imgThumbnail);
			holder.captionText = (TextView) view.findViewById(R.id.lblCaption);

			view.setTag(holder);
		} else {
			// Just get our existing holder
			holder = (Holder) view.getTag();
		}

		// Populate via the holder for speed
		JustinTvStreamData stream = getItem(position);

		// Populate the item contents
		holder.captionText.setText(stream.getTitle());
		Picasso.with(getContext())
		.load(stream.getChannel().getImage_url_large())
		.placeholder(R.drawable.white)
		.into(holder.thumbnailImage);

		return view;
	}

	// Holder class used to efficiently recycle view positions
	private static final class Holder {
		public ImageView thumbnailImage;
		public TextView captionText;
	}
}
