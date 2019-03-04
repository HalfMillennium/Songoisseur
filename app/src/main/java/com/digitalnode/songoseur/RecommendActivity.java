package com.digitalnode.songoseur;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Empty;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Recommendations;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecommendActivity extends AppCompatActivity {

    private ImageView albumCover;
    private TextView currentSong, currentArtist;
    private ListView recSongs;

    public SpotifyService spotify_service;

    public Map<String, Object> seed_map;

    public ArrayList<Recommendations> allRecs = new ArrayList<>();

    private static final String CLIENT_ID = "5e169b72b57c4047a007ecc22e0927c4";
    private static final String REDIRECT_URI = "https://github.com/HalfMillennium/Songoisseur";
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen_layout);

        setUp(MainActivity.global_auth_response);
    }

    private class GetRecommendations extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Recommendations recs = spotify_service.getRecommendations(seed_map);
            if(!allRecs.contains(recs)) {
                allRecs.add(recs);
            } else {
                Log.d(recs.tracks.get(0).name, recs.tracks.get(0).artists.get(0).name);
            }
            return "meetog";
        }
    }

    public void setUp(final AuthenticationResponse response)
    {
        SpotifyApi api = new SpotifyApi();

        api.setAccessToken(response.getAccessToken());

        final SpotifyService spotify = api.getService();

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + response.getAccessToken())
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpotifyPlayerApi spotifyPlayerApi = retrofit.create(SpotifyPlayerApi.class);

        Call<CurrentlyPlaying> call = spotifyPlayerApi.getCurrentlyPlaying();
        call.enqueue(new Callback<CurrentlyPlaying>() {
            @Override
            public void onResponse(Call<CurrentlyPlaying> call, retrofit2.Response<CurrentlyPlaying> response) {
                //Toast.makeText(MainActivity.this, "here-toast", Toast.LENGTH_SHORT).show();
                if(!response.isSuccessful())
                {
                    // should probably do something here
                    return;
                }

                setContentView(R.layout.activity_recommend);
                currentSong = findViewById(R.id.curr_song);
                currentArtist = findViewById(R.id.curr_artist);
                albumCover = findViewById(R.id.album_art);
                recSongs = findViewById(R.id.recommend_list_view);

                spotify_service = spotify;

                final CurrentlyPlaying currentlyPlaying = response.body();

                try {
                    currentSong.setText(currentlyPlaying.getItem().getName());
                    currentArtist.setText(currentlyPlaying.getItem().getArtists().get(0).getName());

                    Glide.with(getApplicationContext()).load(currentlyPlaying.getItem().getAlbum().getImages().get(0).getUrl()).into(albumCover);

                    seed_map = new HashMap<String, Object>() {{
                        put("seed_tracks", currentlyPlaying.getItem().getId());
                    }};

                    try {
                        for (int i = 0; i < 20; i++) {
                            String val = new GetRecommendations().execute().get();
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        Log.d("error retrieving recs", e.getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d("error retrieving recs", e.getMessage());
                    }

                    RecListAdapter adapter = new RecListAdapter(allRecs, getApplicationContext());
                    recSongs.setAdapter(adapter);
                    recSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            newSong(allRecs.get(position).tracks.get(position).id);
                        }
                    });

                } catch (NullPointerException e) {
                    setContentView(R.layout.nothing_playing_layout);
                }
            }

            @Override
            public void onFailure(Call<CurrentlyPlaying> call, Throwable t) {
                Toast.makeText(RecommendActivity.this, "Whoops! Something went wrong. Try again in a little bit.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void newSong(final String songID)
    {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;

                        mSpotifyAppRemote.getPlayerApi().play("spotify:track:"+songID).setResultCallback(new CallResult.ResultCallback<Empty>() {
                            @Override
                            public void onResult(Empty empty) {
                                startActivity(Intent.makeRestartActivityTask(RecommendActivity.this.getIntent().getComponent()));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        Toast.makeText(RecommendActivity.this, "Huh. Couldn't get selected track. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void tryCurrentlyPlaying(View view)
    {
        startActivity(Intent.makeRestartActivityTask(RecommendActivity.this.getIntent().getComponent()));
    }

    public static Intent makeIntent(Context context) { return new Intent(context, RecommendActivity.class); }
}
