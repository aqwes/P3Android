package com.example.dennis.p3.Beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dennis on 2016-10-24.
 */
public class SongBean implements Parcelable {
    public String title;
    private String artist;
    private String uri;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setUri(String uri) {this.uri = uri;}
    public String getUri() {return uri;}

    public SongBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.uri);
    }

    protected SongBean(Parcel in) {
        this.title = in.readString();
        this.artist = in.readString();
        this.uri = in.readString();
    }

    public static final Creator<SongBean> CREATOR = new Creator<SongBean>() {
        @Override
        public SongBean createFromParcel(Parcel source) {
            return new SongBean(source);
        }

        @Override
        public SongBean[] newArray(int size) {
            return new SongBean[size];
        }
    };
}