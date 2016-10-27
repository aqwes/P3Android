package com.example.dennis.p3.APIConnections;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;

import com.example.dennis.p3.Beans.SongBean;
import com.example.dennis.p3.R;
import com.example.dennis.p3.SRChannels;
import com.example.dennis.p3.SpotifyFragment;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.io.InputStream;


public class MainActivity extends Activity implements PlayerNotificationCallback, ConnectionStateCallback {
    private static final String CLIENT_ID = "52dad81b594c4033b2cbaf90708eb517";
    private static final String REDIRECT_URI = "dennisspotify://callback";
    private SongBean songBean;
    private SpotifyFragment spotifyFragment;
    private static boolean firstConnect = true;
    private Intent sr;
    private Intent radiochannel;
    private Player mPlayer;
    private String oldValue = "";
    private boolean alreadyClicked;
    private MainActivity mainActivity = this;
    private boolean lock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(MainActivity.this, 0, request);
        spotifyFragment = new SpotifyFragment();
        spotifyFragment.setMainactivty(MainActivity.this);
        setFragment(spotifyFragment);
        sr = new Intent(this, SR.class);
        radiochannel = new Intent("radiochannel");
        startService(sr);
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.activity_main, fragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
        if (response.getType() == AuthenticationResponse.Type.TOKEN) {
            Config playerConfig = new Config(MainActivity.this, response.getAccessToken(), CLIENT_ID);
            Spotify.getPlayer(playerConfig, MainActivity.this, new Player.InitializationObserver() {

                @Override
                public void onInitialized(Player player) {
                    mPlayer = player;
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    mPlayer.addPlayerNotificationCallback(MainActivity.this);
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    public void startSong(int channel) {
        if (!alreadyClicked) {
            radiochannel.putExtra("channel", String.valueOf(SRChannels.channel[channel]));
            LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(radiochannel);
        }
    }

    public void stopSong() {
        mPlayer.pause();
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("SongBroadCast"));
        super.onResume();
    }

    public synchronized void setText() {
        spotifyFragment.setArtist(songBean.getArtist());
        spotifyFragment.setTitle(songBean.getTitle());
        spotifyFragment.setDescription(songBean.getDescription());
        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                .execute(songBean.getImageUrl());
        mPlayer.play(songBean.getUri());

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }}

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            songBean = intent.getParcelableExtra("songBean");
            if (!lock) {
                if (songBean.isError()) {
                    lock = true;
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mainActivity);
                    builder1.setMessage("No song is playing on this channel right now!");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    lock = false;
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else if (!oldValue.contains(songBean.getArtist())) {
                    oldValue = songBean.getArtist();
                    setText();
                    alreadyClicked = true;
                } else {
                    alreadyClicked = false;
                }
            }

        }
    };
    public void resumeSong() {
        mPlayer.resume();
    }};
