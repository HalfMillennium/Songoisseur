package com.digitalnode.songoseur;

import com.google.gson.annotations.SerializedName;

public class CurrentlyPlaying {
    @SerializedName("id")
    private String songId;
    private String albumId;

    public String getSongId() {
        return songId;
    }

    public String getAlbumId() {
        return albumId;
    }
}
