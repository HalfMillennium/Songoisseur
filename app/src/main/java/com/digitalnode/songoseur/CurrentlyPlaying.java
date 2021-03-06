
package com.digitalnode.songoseur;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentlyPlaying {

    @SerializedName("context")
    @Expose
    private Context context;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("progress_ms")
    @Expose
    private Long progressMs;
    @SerializedName("is_playing")
    @Expose
    private Boolean isPlaying;
    @SerializedName("currently_playing_type")
    @Expose
    private String currentlyPlayingType;
    @SerializedName("item")
    @Expose
    private Item item;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getProgressMs() {
        return progressMs;
    }

    public void setProgressMs(Long progressMs) {
        this.progressMs = progressMs;
    }

    public Boolean getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(Boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public String getCurrentlyPlayingType() {
        return currentlyPlayingType;
    }

    public void setCurrentlyPlayingType(String currentlyPlayingType) {
        this.currentlyPlayingType = currentlyPlayingType;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
