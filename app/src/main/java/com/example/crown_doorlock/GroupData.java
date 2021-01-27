package com.example.crown_doorlock;

import android.graphics.Bitmap;

public class GroupData {

    private Bitmap image;
    private String name;

    public GroupData(Bitmap image, String name) {
        this.image = image;
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
