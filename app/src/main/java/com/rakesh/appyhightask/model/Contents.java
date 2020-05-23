package com.rakesh.appyhightask.model;

import com.google.gson.annotations.SerializedName;

public class Contents {

    @SerializedName("en")
    private String en;

    public Contents(String en) {
        this.en = en;
    }

    public String getEn() {
        return en;
    }
}
