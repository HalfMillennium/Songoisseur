package com.digitalnode.songoseur;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SpotifyPlayerApi {
    @GET("v1/me/player/currently-playing")
    Call<CurrentlyPlaying> getCurrentlyPlaying();
}
