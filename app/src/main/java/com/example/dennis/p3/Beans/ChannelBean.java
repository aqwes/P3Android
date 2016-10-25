package com.example.dennis.p3.Beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dennis on 2016-10-24.
 */
public class ChannelBean {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    /**
     * Empty constructor
     */
    public ChannelBean() {}

    /**
     * Returns the ID
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id to the new id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name to the new name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

}
