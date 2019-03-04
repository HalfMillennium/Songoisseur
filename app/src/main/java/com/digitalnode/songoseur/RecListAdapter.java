package com.digitalnode.songoseur;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.Track;

public class RecListAdapter extends ArrayAdapter<Recommendations> {

    private ArrayList<Recommendations> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView songName;
        TextView artist;
        ImageView album_art;
    }

    public RecListAdapter(ArrayList<Recommendations> data, Context context) {
        super(context, R.layout.rec_entry_layout, data);
        this.dataSet = data;
        this.mContext=context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Recommendations rec = dataSet.get(position);
        List<Track> songs = rec.tracks;
        if (position < songs.size()) {
            Track currTrack = songs.get(position);

            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.rec_entry_layout, parent, false);
                viewHolder.songName =  convertView.findViewById(R.id.song_name);
                viewHolder.artist = convertView.findViewById(R.id.artist);
                viewHolder.album_art = convertView.findViewById(R.id.album_cover);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            lastPosition = position;

            viewHolder.songName.setText(currTrack.name);
            viewHolder.artist.setText(currTrack.artists.get(0).name);
            Glide.with(getContext()).load(currTrack.album.images.get(0).url).into(viewHolder.album_art);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}