package com.example.dennis.p3.APIConnections;

import android.app.IntentService;
import android.content.Intent;
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

import java.util.ArrayList;

/**
 * Created by Dennis on 2016-10-24.
 */

public class SR extends IntentService{
    private SongBean songBean;
    private String uri;
    public SR() {
        super("SR");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        songBean = new SongBean();
        RequestQueue queue = Volley.newRequestQueue(this);
        String baseUrl = "http://api.sr.se/api/v2/playlists/rightnow?channelid=2576&format=json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            songBean.setArtist(response.getJSONObject("playlist").getJSONObject("song").getString("artist"));
                            songBean.setTitle(response.getJSONObject("playlist").getJSONObject("song").getString("title"));
                            getSpotifyURI();
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    public void getSpotifyURI(){
        String title = null;
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            title = URLEncoder.encode(songBean.getTitle(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String baseUrl = "https://api.spotify.com/v1/search?query=" + title.replaceAll("\\s+","").toLowerCase() + "&offset=0&limit=1&type=track";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            saveData(response.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getString("uri").toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
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
    
    public void getSRChannelList() {
        final ArrayList<ChannelBean> channelList = new ArrayList<ChannelBean>();
        RequestQueue queue = Volley.newRequestQueue(this);
        String baseUrl = "http://api.sr.se/api/v2/channels/?format=json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            for(int i = 0; i < 10; i++) {
                                ChannelBean channelBean = new ChannelBean();
                                channelBean.setId(Integer.parseInt(response.getJSONArray("channels").getJSONObject(i).getString("id")));
                                channelBean.setName(response.getJSONArray("channels").getJSONObject(i).getString("name"));
                                channelList.add(channelBean);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
    
    public void saveData(String uri){
        songBean.setUri(uri);
        Intent intent = new Intent("SongBroadCast");
        intent.putExtra("songBean", songBean);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);
    }

}
