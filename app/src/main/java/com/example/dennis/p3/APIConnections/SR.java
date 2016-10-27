package com.example.dennis.p3.APIConnections;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dennis.p3.Beans.SongBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Dennis on 2016-10-24.
 */

public class SR extends IntentService {
    private SongBean songBean;
    private static boolean firstConnect = true;
    private String oldChannel = "";
    private Intent intent = new Intent("SongBroadCast");

    public SR() {
        super("SR");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("radiochannel"));
        songBean = new SongBean();

    }

    public void saveData(String uri, String imageurl) {
        songBean.setUri(uri);
        songBean.setImageUrl(imageurl);
        intent.putExtra("songBean", songBean);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);
    }


    public void saveArtistData(String description) {
        songBean.setDescription(description);
    }

    public void findSong(String channelNr) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String baseUrl = "http://api.sr.se/api/v2/playlists/rightnow?channelid=" + channelNr + "&format=json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            songBean.setArtist(response.getJSONObject("playlist").getJSONObject("song").getString("artist"));
                            songBean.setTitle(response.getJSONObject("playlist").getJSONObject("song").getString("title"));
                            songBean.setError(false);
                            getInfoFromLastFMboutArtist();
                        } catch (JSONException e) {
                            songBean.setError(true);
                            saveData(null, null);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());

                    }
                });
        queue.add(jsObjRequest);
    }

    public void getInfoFromLastFMboutArtist() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String searchstring = songBean.getArtist().replaceAll("\\s+", "%20");

        String baseUrl = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + searchstring + "&api_key=ca2da1267f0c97e5fa4697c44a91220d&format=json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            saveArtistData(response.getJSONObject("artist").getJSONObject("bio").getString("summary"));
                            songBean.setError(false);
                            getSpotifyURI();
                        } catch (JSONException e) {
                            songBean.setError(true);
                            saveData(null, null);

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());

                    }
                });
        queue.add(jsObjRequest);

    }

    public void getSpotifyURI() {
        String title = null;
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            title = URLEncoder.encode(songBean.getTitle(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String baseUrl = "https://api.spotify.com/v1/search?query=" + title.replaceAll("\\s+", "%20") + "&offset=0&limit=1&type=track";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            songBean.setError(false);

                            saveData(response.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getString("uri").toString(),
                                    response.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url").toString()
                            );
                        } catch (JSONException e) {
                            songBean.setError(true);
                            saveData(null, null);
                            System.out.println(e);

                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());

                    }
                });

        queue.add(jsObjRequest);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String channelNr = intent.getStringExtra("channel");
            if (!oldChannel.contains(channelNr)) {
                findSong(channelNr);
                oldChannel = channelNr;
            }
        }
    };
};