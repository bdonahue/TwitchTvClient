package com.wdonahue.twitchtvclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wdonahue.twitchtvclient.R;
import com.wdonahue.twitchtvclient.model.JustinTvStreamData;

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
            holder.lblViewers = (TextView) view.findViewById(R.id.lblViewers);
            holder.channelThumbnailImage = (ImageView) view.findViewById(R.id.imgChannelThumbnail);

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
        int totalViewers = stream.getChannel_count().intValue();
        holder.lblViewers.setText(getContext().getResources().getQuantityString(R.plurals.viewers, totalViewers, totalViewers));

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

        return view;
    }

    // Holder class used to efficiently recycle view positions
    private static final class Holder {
        public ImageView screenCapThumbnailImage;

        public ImageView channelThumbnailImage;

        public TextView lblTitleText;

        public TextView lblGame;

        public TextView lblUser;

        public TextView lblViewers;
    }
}
