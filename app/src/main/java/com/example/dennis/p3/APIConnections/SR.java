package com.example.dennis.p3.APIConnections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Dennis on 2016-10-24.
 */

public class SR {

    public void startRadio(int channel) {

        String baseUrl = "http://api.sr.se/api/v2/playlists/rightnow?channelid=" + channel + "&format=json";

        HttpClient httpclient = null;
        HttpGet httpGet = null;
        HttpResponse response = null;
        StatusLine status = null;
        HttpEntity entity = null;
        InputStream data = null;
        Reader reader = null;

        GsonBuilder builder = new GsonBuilder();
        Gson json = builder.create();

        try {
            httpclient = HttpClients.createDefault();
            httpGet = new HttpGet(baseUrl);

            // Call the API and verify that all went well
            response = httpclient.execute(httpGet);
            status = response.getStatusLine();
            if (status.getStatusCode() == 200) {
                // All went well. Let's fetch the data
                entity = response.getEntity();
                data = entity.getContent();

                try {

                    reader = new InputStreamReader(data);
//                    envelope = json.fromJson(reader, Envelope.class);
//                    playlist = envelope.getPlaylist();

//                    if (playlist.getSong() != null) {
//                        printSong(playlist.getSong());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
