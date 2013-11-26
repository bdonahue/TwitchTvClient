package com.wdonahue.rapidparsing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wdonahue.rapidparsing.R;
import com.wdonahue.rapidparsing.model.JustinTvStreamData;

import java.util.List;

public class JustinTvStreamAdapter extends ArrayAdapter<JustinTvStreamData> {
    private LayoutInflater mInflater;

    public JustinTvStreamAdapter(Context context, int textViewResourceId, List<JustinTvStreamData> objects) {
        super(context, textViewResourceId, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder;

        if (view == null) {
            // View doesn't exist so create it and create the holder
            view = mInflater.inflate(R.layout.grid_item, parent, false);

            holder = new Holder();
            holder.screenCapThumbnailImage = (ImageView) view.findViewById(R.id.imgScreencapThumbnail);
            holder.lblTitleText = (TextView) view.findViewById(R.id.lblTitle);
            holder.lblGame = (TextView) view.findViewById(R.id.lblGame);
            holder.lblUser = (TextView) view.findViewById(R.id.lblUser);
            holder.lblNew = (TextView) view.findViewById(R.id.lblNew);
            holder.lblViewers = (TextView) view.findViewById(R.id.lblViewers);
            holder.channelThumbnailImage = (ImageView) view.findViewById(R.id.imgChannelThumbnail);
            holder.contentTypeImage = (ImageView) view.findViewById(R.id.imgContentType);

            view.setTag(holder);
        } else {
            // Just get our existing holder
            holder = (Holder) view.getTag();
        }

        // Populate via the holder for speed
        JustinTvStreamData stream = getItem(position);

        // Populate the item contents
        holder.lblTitleText.setText(stream.getTitle());
        holder.lblGame.setText(stream.getMeta_game());
        holder.lblUser.setText(stream.getChannel().getLogin());
        holder.lblViewers.setText(stream.getStream_count().toString() + " Viewers");

        if (stream.isNew) {
            holder.lblNew.setVisibility(View.VISIBLE);
        } else {
            holder.lblNew.setVisibility(View.GONE);
        }

        // Load the screen cap image on a background thread
        Picasso.with(getContext())
                .load(stream.getChannel().getScreen_cap_url_medium())
                .placeholder(R.drawable.white)
                .into(holder.screenCapThumbnailImage);

        // Load the channel thumbnail image on a background thread
        Picasso.with(getContext())
                .load(stream.getChannel().getImage_url_medium())
                .placeholder(R.drawable.transparent)
                .into(holder.channelThumbnailImage);

        // If gaming content then show the controller graphic
        if (stream.getChannel().getCategory().contains("gaming")) {
            // Load the channel type image on a background thread
            Picasso.with(getContext())
                    .load(R.drawable.ic_action_gamepad)
                    .placeholder(R.drawable.transparent)
                    .into(holder.contentTypeImage);
        } else {
            holder.contentTypeImage.setImageDrawable(null);
        }

        return view;
    }

    // Holder class used to efficiently recycle view positions
    private static final class Holder {
        public ImageView screenCapThumbnailImage;

        public ImageView channelThumbnailImage;

        public ImageView contentTypeImage;

        public TextView lblTitleText;

        public TextView lblGame;

        public TextView lblUser;

        public TextView lblViewers;

        public TextView lblNew;
    }
}
