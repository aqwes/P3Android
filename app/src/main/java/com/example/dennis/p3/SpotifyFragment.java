package com.example.dennis.p3;


import android.app.Fragment;
import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dennis.p3.APIConnections.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpotifyFragment extends Fragment {
private MainActivity mainactivty;
    private Button buttonPlay;
    private Button buttonStop;
    private TextView artist;
    private TextView title;
    private TextView description;
    private ListView listView;
    private ImageView imageview;

    public SpotifyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_spotify, container, false);
        buttonPlay = (Button) view.findViewById(R.id.buttonplay);
        buttonStop = (Button) view.findViewById(R.id.buttonstop);

        artist = (TextView) view.findViewById(R.id.textViewArtist);
        title = (TextView) view.findViewById(R.id.textViewTitle);
        description = (TextView) view.findViewById(R.id.textViewDescription);
        description.setMovementMethod(new ScrollingMovementMethod());
        imageview = (ImageView) view.findViewById(R.id.imageView);

        listView = (ListView) view.findViewById(R.id.srList);

        ArrayAdapter arrayAdapter =   new ArrayAdapter<String>(mainactivty, android.R.layout.simple_list_item_1, SRChannels.channelName);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new ListViewListener());

        buttonPlay.setOnClickListener(new ButtonListener());
        buttonStop.setOnClickListener(new ButtonListener2());
        return view;
    }

    public void setMainactivty(MainActivity mainactivty) {
        this.mainactivty = mainactivty;
    }

    public void setDescription(String description) {
        this.description.setText(description);
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mainactivty.resumeSong();

        }
    }
        private class ButtonListener2 implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                mainactivty.stopSong();
            }
        }

        public void setArtist(String artist1) {
            artist.setText("Artist: "+artist1);
        }


        public void setTitle(String title1) {
            title.setText("Title: "+title1);
        }

    private class ListViewListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mainactivty.startSong(i);
        }
    }
}
